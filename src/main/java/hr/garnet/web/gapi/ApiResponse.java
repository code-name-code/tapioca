package hr.garnet.web.gapi;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ApiResponse extends HttpServletResponseWrapper {

  private final HttpServletRequest request;

  public ApiResponse(HttpServletRequest request, HttpServletResponse response) {
    super(response);
    this.request = request;
  }

  public void send(int status, String contentType, byte[] data) {
    try {
      addHeader("Content-Type", contentType);
      setStatus(status);
      if (data != null) {
        getOutputStream().write(data);
      }
    } catch (IOException e) {
      throw new ApiException(SC_INTERNAL_SERVER_ERROR);
    }
  }

  public void json(int status, Object data) {
    send(status, "application/json", json(data).getBytes(StandardCharsets.UTF_8));
  }

  private String json(Object data) {
    return ApiSCBindings.getJsonWriter(request.getServletContext()).apply(data);
  }
}
