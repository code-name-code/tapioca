package hr.garnet.gapi;

import static jakarta.servlet.http.HttpServletResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiTest {

  public static class Car {
    public String brand;
  }

  public static class ThrowEx implements ApiCommand {
    @Override
    public void execute(ApiRequest req, ApiResponse resp) {
      throw new IllegalArgumentException("Illegal argument");
    }
  }

  public static class GetAllCars implements ApiCommand {
    @Override
    public void execute(ApiRequest req, ApiResponse resp) {
      resp.json(200, cars);
    }
  }

  public static class GetCarByBrand implements ApiCommand {
    @Override
    public void execute(ApiRequest req, ApiResponse resp) {
      Optional<String> brandParam = req.getPathParam("brand");
      if (brandParam.isPresent()) {
        cars.stream()
            .filter(car -> car.brand.equalsIgnoreCase(brandParam.get()))
            .findFirst()
            .ifPresentOrElse(car -> resp.json(200, car), () -> resp.setStatus(SC_NOT_FOUND));
      } else {
        resp.setStatus(SC_NOT_FOUND);
      }
    }
  }

  public static class CreateCar implements ApiCommand {
    @Override
    public void execute(ApiRequest req, ApiResponse resp) {
      Car car = req.json(Car.class);
      cars.add(car);
      resp.send(201, "text/plain", "Created new %s".formatted(car.brand).getBytes());
    }
  }

  public static class DeleteCarByBrand implements ApiCommand {
    @Override
    public void execute(ApiRequest req, ApiResponse resp) {
      resp.setStatus(SC_NO_CONTENT);
    }
  }

  public static class UpdateCar implements ApiCommand {
    @Override
    public void execute(ApiRequest req, ApiResponse resp) {
      resp.setStatus(SC_NO_CONTENT);
    }
  }

  public static class Options implements ApiCommand {
    @Override
    public void execute(ApiRequest req, ApiResponse resp) {
      resp.setHeader("X-Method", "Options");
    }
  }

  public static class Head implements ApiCommand {
    @Override
    public void execute(ApiRequest req, ApiResponse resp) {
      resp.setHeader("X-Method", "Head");
    }
  }

  public static class Trace implements ApiCommand {
    @Override
    public void execute(ApiRequest req, ApiResponse resp) {
      resp.setHeader("X-Method", "Trace");
    }
  }

  public static class CarApi extends Api {
    @Override
    protected void configure() {
      setCommandProvider(
          aClass -> {
            try {
              return aClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException
                | IllegalAccessException
                | NoSuchMethodException
                | InvocationTargetException e) {
              return null;
            }
          });

      setJsonReader(
          (s, aClass) -> {
            Car car = new Car();
            Matcher matcher = Pattern.compile("\\{\"brand\":\"(?<brand>\\w+)\"}").matcher(s);
            boolean found = matcher.find();
            if (found) {
              car.brand = matcher.group("brand");
            }
            return car;
          });

      setJsonWriter(
          o -> {
            if (o instanceof List carList) {
              if (carList.isEmpty()) {
                return "[]";
              } else {
                Car car = (Car) carList.get(0);
                return "[{\"brand\":\"%s\"}]".formatted(car.brand);
              }
            } else {
              return "[{\"brand\":\"%s\"}]".formatted(((Car) o).brand);
            }
          });

      setExceptionHandler(
          (e, req, resp) -> {
            try {
              resp.setStatus(SC_INTERNAL_SERVER_ERROR);
              resp.getWriter().write(e.getMessage());
            } catch (IOException ignored) {

            }
          });

      filter(
          (servletRequest, servletResponse, filterChain) -> {
            ((HttpServletResponse) servletResponse).setHeader("X-Served-By", "GAPI");
            filterChain.doFilter(servletRequest, servletResponse);
          },
          "/*");

      serve(
          api -> {
            api.get("", GetAllCars.class);
            api.get("throwex", ThrowEx.class);
            api.get("(?<brand>\\w+)", GetCarByBrand.class);
            api.post("", CreateCar.class);
            api.delete("(?<brand>\\w+)", DeleteCarByBrand.class);
            api.put("", UpdateCar.class);
            api.head("", Head.class);
          },
          "/cars/*");
    }
  }

  static Tomcat tomcat;
  static final int port = 11112;
  static final URI uri = URI.create("http://localhost:%d/".formatted(port));
  static final List<Car> cars = new ArrayList<>(1);

  @BeforeAll
  public static void setup() throws Exception {
    tomcat = new Tomcat();
    tomcat.setBaseDir(Path.of(new File("").getAbsolutePath()).resolve("target").toString());
    tomcat.setPort(11112);
    tomcat.getConnector();

    Context context = tomcat.addContext("", null);
    context.addApplicationListener(CarApi.class.getName());

    tomcat.start();
  }

  @AfterAll
  public static void teardown() throws Exception {
    tomcat.stop();
  }

  @Test
  @Order(1)
  public void should_return_an_empty_list_of_cars() throws IOException, InterruptedException {
    HttpRequest getAllCars = HttpRequest.newBuilder(uri.resolve("cars/")).GET().build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(getAllCars, HttpResponse.BodyHandlers.ofString());

    assertEquals(200, response.statusCode());
    assertEquals("[]", response.body());
  }

  @Test
  @Order(2)
  public void should_create_new_car() throws IOException, InterruptedException {
    HttpRequest postCar =
        HttpRequest.newBuilder(uri.resolve("cars/"))
            .POST(HttpRequest.BodyPublishers.ofString("{\"brand\":\"Porsche\"}"))
            .build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(postCar, HttpResponse.BodyHandlers.ofString());

    assertEquals(201, response.statusCode());
    assertEquals("Created new Porsche", response.body());
  }

  @Test
  @Order(3)
  public void should_return_a_list_of_single_car() throws IOException, InterruptedException {
    HttpRequest getAllCars = HttpRequest.newBuilder(uri.resolve("cars/")).GET().build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(getAllCars, HttpResponse.BodyHandlers.ofString());

    assertEquals(200, response.statusCode());
    assertEquals("[{\"brand\":\"Porsche\"}]", response.body());
  }

  @Test
  @Order(4)
  public void should_resolve_path_parameter() throws IOException, InterruptedException {
    HttpRequest getCarByBrand =
        HttpRequest.newBuilder(uri.resolve("cars/").resolve("Porsche")).GET().build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(getCarByBrand, HttpResponse.BodyHandlers.ofString());

    assertEquals(200, response.statusCode());
    assertEquals("[{\"brand\":\"Porsche\"}]", response.body());
  }

  @Test
  @Order(5)
  public void should_return_404_for_undefined_path() throws IOException, InterruptedException {
    HttpRequest getTenants = HttpRequest.newBuilder(uri.resolve("undefined")).GET().build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(getTenants, HttpResponse.BodyHandlers.ofString());

    assertEquals(404, response.statusCode());
  }

  @Test
  @Order(6)
  public void filter_should_be_triggered_for_registered_uri()
      throws IOException, InterruptedException {
    HttpRequest getTenants = HttpRequest.newBuilder(uri.resolve("cars/")).GET().build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(getTenants, HttpResponse.BodyHandlers.ofString());

    assertEquals("GAPI", response.headers().map().get("X-Served-By").get(0));
  }

  @Test
  @Order(7)
  public void should_map_delete_method() throws IOException, InterruptedException {
    HttpRequest deleteCar =
        HttpRequest.newBuilder(uri.resolve("cars/").resolve("Porsche")).DELETE().build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(deleteCar, HttpResponse.BodyHandlers.ofString());

    assertEquals(SC_NO_CONTENT, response.statusCode());
  }

  @Test
  @Order(8)
  public void should_map_put_method() throws IOException, InterruptedException {
    HttpRequest updateCar =
        HttpRequest.newBuilder(uri.resolve("cars/"))
            .PUT(HttpRequest.BodyPublishers.ofString(""))
            .build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(updateCar, HttpResponse.BodyHandlers.ofString());

    assertEquals(SC_NO_CONTENT, response.statusCode());
  }

  @Test
  @Order(9)
  public void should_map_head_method() throws IOException, InterruptedException {
    HttpRequest updateCar = HttpRequest.newBuilder(uri.resolve("cars/")).HEAD().build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(updateCar, HttpResponse.BodyHandlers.ofString());

    assertEquals("Head", response.headers().map().get("X-Method").get(0));
  }

  @Test
  @Order(10)
  public void should_use_exception_handler() throws IOException, InterruptedException {
    HttpRequest throwex =
        HttpRequest.newBuilder(uri.resolve("cars/").resolve("throwex")).GET().build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(throwex, HttpResponse.BodyHandlers.ofString());

    assertEquals(500, response.statusCode());
    assertEquals("Illegal argument", response.body());
  }
}
