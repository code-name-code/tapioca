package hr.codenamecode.tapioca;

import static jakarta.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import hr.codenamecode.tapioca.cars.Car;
import hr.codenamecode.tapioca.cars.CarApi;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiTest {

  Tomcat tomcat;
  public static final List<Car> CARS = new ArrayList<>(1);
  final int PORT = 11112;
  final URI uri = URI.create("http://localhost:%d/".formatted(PORT));

  @BeforeAll
  public void setup() throws Exception {
    tomcat = new Tomcat();
    tomcat.setBaseDir(Path.of(new File("").getAbsolutePath()).resolve("target").toString());
    tomcat.setPort(11112);
    tomcat.getConnector();

    Context context = tomcat.addContext("", null);
    context.addApplicationListener(CarApi.class.getName());

    tomcat.start();
  }

  @AfterAll
  public void teardown() throws Exception {
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
    assertEquals("{\"brand\":\"Porsche\"}", response.body());
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

    assertEquals("Tapioca", response.headers().map().get("X-Served-By").get(0));
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
  @Order(10)
  public void default_exception_handler_should_report_500_for_non_APIException()
      throws IOException, InterruptedException {
    HttpClient httpClient = HttpClient.newHttpClient();

    HttpRequest throwex =
        HttpRequest.newBuilder(uri.resolve("exts/").resolve("throwex?type=illegal")).GET().build();

    HttpResponse<String> response = httpClient.send(throwex, HttpResponse.BodyHandlers.ofString());

    // Does not reveal exception message, just reports http status 500
    assertEquals(500, response.statusCode());
    assertEquals("", response.body());
  }

  @Test
  @Order(15)
  public void
      default_exception_handler_should_report_http_status_and_message_from_thrown_APIException()
          throws IOException, InterruptedException {
    HttpClient httpClient = HttpClient.newHttpClient();

    HttpRequest throwex =
        HttpRequest.newBuilder(uri.resolve("exts/").resolve("throwex?type=api")).GET().build();

    HttpResponse<String> response = httpClient.send(throwex, HttpResponse.BodyHandlers.ofString());

    assertEquals(501, response.statusCode());
    assertEquals("API exception", response.body());
  }

  @Test
  @Order(20)
  public void should_use_inline_web_method_implementation()
      throws IOException, InterruptedException {
    HttpRequest inlineImpl =
        HttpRequest.newBuilder(uri.resolve("exts/").resolve("inlineImpl")).GET().build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(inlineImpl, HttpResponse.BodyHandlers.ofString());

    assertEquals(200, response.statusCode());
    assertEquals("inlineImpl", response.body());
  }

  @Test
  @Order(30)
  public void should_return_context_object() throws IOException, InterruptedException {
    HttpRequest contextObject =
        HttpRequest.newBuilder(uri.resolve("exts/").resolve("contextObject")).GET().build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(contextObject, HttpResponse.BodyHandlers.ofString());

    assertEquals(200, response.statusCode());
    assertEquals("ok", response.body());
  }
}
