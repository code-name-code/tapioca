package hr.codenamecode.tapioca;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Wrapper around {@link HttpServletResponse} which provides some additional, convenient methods out
 * of the box. Used by {@link RequestHandler}.
 *
 * @author vedransmid@gmail.com
 */
public class Response extends HttpServletResponseWrapper {

  public Response(HttpServletResponse response) {
    super(response);
  }

  /**
   * Send generic response
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
      throw new ApiException(SC_INTERNAL_SERVER_ERROR, e);
    }
  }

  /**
   * Send JSON response
   *
   * @param status Response status
   * @param data Data to be written to the response output stream. This data should be JSON
   *     compatible.
   */
  public void json(int status, Object data) {
    send(status, "application/json", json(data).getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Sends file response
   *
   * @param inputStream File to be downloaded in the form of {@link InputStream}
   * @param attachment If set to true, The first parameter in the HTTP context will be set to
   *     attachment, otherwise inline
   * @param contentType Content type
   * @param filename When used in combination with Content-Disposition: attachment, it is used as
   *     the default filename for an eventual "Save As" dialog presented to the user
   */
  public void file(
      InputStream inputStream, boolean attachment, String contentType, String filename) {
    try {
      ServletOutputStream outputStream = getOutputStream();

      int c;
      while ((c = inputStream.read()) != -1) {
        outputStream.write(c);
        outputStream.flush();
      }

      String inlineOrAttachment = attachment ? "attachment" : "inline";

      setContentType(contentType);
      setHeader("Content-Disposition", "%s; filename=%s".formatted(inlineOrAttachment, filename));
      setStatus(SC_OK);
    } catch (IOException e) {
      throw new ApiException(SC_INTERNAL_SERVER_ERROR, e);
    }
  }

  private String json(Object data) {
    return Bindings.getJsonWriter().apply(data);
  }
}
