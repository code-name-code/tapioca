package hr.codenamecode.tapioca.cars;

import hr.codenamecode.tapioca.internal.Http;
import static jakarta.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import hr.codenamecode.tapioca.internal.TapiocaTest;
import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

@TapiocaTest(listeners = {CarApi.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CarApiTest {

  // Automatically set by the TapiocaTestExstension
  Http http;

  @Test
  @Order(1)
  public void should_return_an_empty_list_of_cars() throws IOException, InterruptedException {
    HttpResponse<String> response = http.get("cars");

    assertEquals(200, response.statusCode());
    assertEquals("[]", response.body());
  }

  @Test
  @Order(2)
  public void should_create_new_car() throws IOException, InterruptedException {
    HttpResponse<String> response = http.post("cars", "{\"brand\":\"Porsche\"}");

    assertEquals(201, response.statusCode());
    assertEquals("Created new Porsche", response.body());
  }

  @Test
  @Order(3)
  public void should_return_a_list_of_single_car() throws IOException, InterruptedException {
    HttpResponse<String> response = http.get("cars");

    assertEquals(200, response.statusCode());
    assertEquals("[{\"brand\":\"Porsche\"}]", response.body());
  }

  @Test
  @Order(4)
  public void should_resolve_path_parameter() throws IOException, InterruptedException {
    HttpResponse<String> response = http.get("cars/Porsche");

    assertEquals(200, response.statusCode());
    assertEquals("{\"brand\":\"Porsche\"}", response.body());
  }

  @Test
  @Order(5)
  public void should_return_404_for_undefined_path() throws IOException, InterruptedException {
    HttpResponse<String> response = http.get("undefined");

    assertEquals(404, response.statusCode());
  }

  @Test
  @Order(6)
  public void filter_should_be_triggered_for_registered_uri()
      throws IOException, InterruptedException {
    HttpResponse<String> response = http.get("cars");

    assertEquals("Tapioca", response.headers().map().get("X-Served-By").get(0));
  }

  @Test
  @Order(7)
  public void should_map_delete_method() throws IOException, InterruptedException {
    HttpResponse<String> response = http.delete("cars/Porsche");

    assertEquals(SC_NO_CONTENT, response.statusCode());
  }

  @Test
  @Order(8)
  public void should_map_put_method() throws IOException, InterruptedException {
    HttpResponse<String> response = http.put("cars", "");

    assertEquals(SC_NO_CONTENT, response.statusCode());
  }

  @Test
  @Order(10)
  public void default_exception_handler_should_report_500_for_non_APIException()
      throws IOException, InterruptedException {
    HttpResponse<String> response = http.get("exts/throwex?type=illegal");

    // Does not reveal exception message, just reports http status 500
    assertEquals(500, response.statusCode());
    assertEquals("", response.body());
  }

  @Test
  @Order(15)
  public void
      default_exception_handler_should_report_http_status_and_message_from_thrown_APIException()
          throws IOException, InterruptedException {
    HttpResponse<String> response = http.get("exts/throwex?type=api");

    assertEquals(501, response.statusCode());
    assertEquals("API exception", response.body());
  }

  @Test
  @Order(20)
  public void should_use_inline_web_method_implementation()
      throws IOException, InterruptedException {
    HttpResponse<String> response = http.get("exts/inlineImpl");

    assertEquals(200, response.statusCode());
    assertEquals("inlineImpl", response.body());
  }

  @Test
  @Order(30)
  public void should_return_context_object() throws IOException, InterruptedException {
    HttpResponse<String> response = http.get("exts/contextObject");

    assertEquals(200, response.statusCode());
    assertEquals("ok", response.body());
  }

  @Test
  @Order(30)
  public void should_download_file(@TempDir Path path) throws IOException, InterruptedException {
    // Create temporary files
    Path catalogPath = path.resolve("catalog.txt");
    Files.writeString(catalogPath, "Mazda,Suzuki", StandardOpenOption.CREATE_NEW);

    // Placeholder download file
    Path downloadedCatalogPath = path.resolve("downloaded_catalog.txt");
    Files.writeString(downloadedCatalogPath, "Mazda,Suzuki", StandardOpenOption.CREATE_NEW);

    HttpResponse<Path> response =
        http.download(
            "cars/download?path="
                + URLEncoder.encode(catalogPath.toString(), StandardCharsets.UTF_8),
            path);

    assertEquals(200, response.statusCode());
    assertEquals(
        "attachment; filename=downloaded_catalog.txt",
        response.headers().firstValue("Content-Disposition").get());
    assertEquals(downloadedCatalogPath.toString(), response.body().toString());
    assertEquals("Mazda,Suzuki", Files.readString(downloadedCatalogPath));
  }
}
