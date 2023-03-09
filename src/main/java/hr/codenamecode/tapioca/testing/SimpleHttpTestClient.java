package hr.codenamecode.tapioca.testing;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class SimpleHttpTestClient {

  private final HttpClient client;
  private final URI base;

  public SimpleHttpTestClient(URI base) {
    this.client = HttpClient.newHttpClient();
    this.base = base;
  }

  public SimpleHttpTestClient(HttpClient client, URI base) {
    this.client = client;
    this.base = base;
  }

  public HttpResponse<String> get(String uri, String... headers) {
    HttpRequest req = getBuilder(uri, headers).GET().build();

    try {
      return client.send(req, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public HttpResponse<String> post(String uri, String body, String... headers) {
    HttpRequest req =
        getBuilder(uri, headers).POST(HttpRequest.BodyPublishers.ofString(body)).build();

    try {
      return client.send(req, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public HttpResponse<String> delete(String uri, String... headers) {
    HttpRequest req = getBuilder(uri, headers).DELETE().build();

    try {
      return HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public HttpResponse<String> put(String uri, String body, String... headers) {
    HttpRequest req =
        getBuilder(uri, headers).PUT(HttpRequest.BodyPublishers.ofString(body)).build();

    try {
      return client.send(req, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public HttpResponse<Path> download(String uri, Path directory, String... headers) {
    HttpRequest req = getBuilder(uri, headers).GET().build();
    
    try {
      return client.send(
          req, HttpResponse.BodyHandlers.ofFileDownload(directory, StandardOpenOption.WRITE));
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private HttpRequest.Builder getBuilder(String uri, String... headers) {
    HttpRequest.Builder builder = HttpRequest.newBuilder(base.resolve(uri));

    if (headers.length > 0) {
      builder.headers(headers);
    }

    return builder;
  }

  public HttpClient getClient() {
    return client;
  }

  public URI getBase() {
    return base;
  }
}
