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
   * Send response
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
   * Send response using one of the registered media type handlers. Media type handler used depends
   * on the specified contentType parameter.
   *
   * @param status
   * @param contentType
   * @param data
   */
  public void send(int status, String contentType, Object data) {
    MediaTypeHandler handler = Bindings.getMediaTypeHandlers().get(contentType);
    String output = handler.to(data);
    send(status, contentType, output.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Send JSON response
   *
   * <p>NOTE: This method requires {@link Api#jsonWriter} to be set. Use {@link
   * Api#setJsonWriter(java.util.function.Function)} to set it.
   *
   * @param status Response status
   * @param data Data to be written to the response output stream. This data should be JSON
   *     compatible.
   */
  public void json(int status, Object data) {
    String contentType = "application/json";
    MediaTypeHandler handler = Bindings.getMediaTypeHandlers().get(contentType);
    String json = handler.to(data);
    send(status, contentType, json.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Writes file's byte stream to the response. Closes given input stream.
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
      try (inputStream) {
        String inlineOrAttachment = attachment ? "attachment" : "inline";

        setContentType(contentType);
        setHeader("Content-Disposition", "%s; filename=%s".formatted(inlineOrAttachment, filename));

        ServletOutputStream outputStream = getOutputStream();

        int c;
        while ((c = inputStream.read()) != -1) {
          outputStream.write(c);
          outputStream.flush();
        }
      }

      setStatus(SC_OK);
    } catch (IOException e) {
      throw new ApiException(SC_INTERNAL_SERVER_ERROR, e);
    }
  }

  /**
   * Writes input stream to the response
   *
   * @param inputStream
   * @param contentType
   */
  public void stream(InputStream inputStream, String contentType) {
    try {
      try (inputStream) {
        setContentType(contentType);

        ServletOutputStream outputStream = getOutputStream();

        int c;
        while ((c = inputStream.read()) != -1) {
          outputStream.write(c);
          outputStream.flush();
        }
      }

      setStatus(SC_OK);
    } catch (IOException e) {
      throw new ApiException(SC_INTERNAL_SERVER_ERROR, e);
    }
  }
}
