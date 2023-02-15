package hr.codenamecode.tapioca;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class TapiocaHttpTestClient {

  HttpClient client = HttpClient.newHttpClient();
  URI base;

  public TapiocaHttpTestClient(URI base) {
    this.base = base;
  }

  public HttpResponse<String> get(String uri) {
    HttpRequest req = HttpRequest.newBuilder(base.resolve(uri)).GET().build();
    try {
      return client.send(req, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public HttpResponse<String> post(String uri, String body) {
    HttpRequest req =
        HttpRequest.newBuilder(base.resolve(uri))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
    try {
      return client.send(req, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public HttpResponse<String> delete(String uri) {
    HttpRequest req = HttpRequest.newBuilder(base.resolve(uri)).DELETE().build();
    try {
      return HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public HttpResponse<String> put(String uri, String body) {
    HttpRequest req =
        HttpRequest.newBuilder(base.resolve(uri))
            .PUT(HttpRequest.BodyPublishers.ofString(body))
            .build();
    try {
      return client.send(req, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public HttpResponse<Path> download(String uri, Path directory) {
    HttpRequest req = HttpRequest.newBuilder(base.resolve(uri)).GET().build();
    try {
      return client.send(
          req, HttpResponse.BodyHandlers.ofFileDownload(directory, StandardOpenOption.WRITE));
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}
