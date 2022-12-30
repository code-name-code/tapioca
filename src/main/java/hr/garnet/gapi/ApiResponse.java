package hr.garnet.gapi;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Wrapper around {@link HttpServletResponse} which provides some additional, convenient methods out
 * of the box. Used by {@link ApiCommand}.
 *
 * @author vedransmid@gmail.com
 */
public class ApiResponse extends HttpServletResponseWrapper {

  public ApiResponse(HttpServletResponse response) {
    super(response);
  }

  /**
   * Send response in one line
   *
   * @param status Response status
   * @param contentType Response content type
   * @param data Data to be written to the response output stream
   */
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

  /**
   * Send json response in one line
   *
   * @param status Response status
   * @param data Data to be written to the response output stream. This data should be JSON
   *     compatible.
   */
  public void json(int status, Object data) {
    send(status, "application/json", json(data).getBytes(StandardCharsets.UTF_8));
  }

  private String json(Object data) {
    return ApiBindings.getJsonWriter().apply(data);
  }
}
