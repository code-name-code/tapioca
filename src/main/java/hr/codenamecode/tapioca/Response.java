package hr.codenamecode.tapioca;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.Objects;

/**
 * Wrapper around {@link HttpServletResponse} which provides some additional, convenient methods out
 * of the box. Used by {@link RequestHandler}.
 *
 * @author vedransmid@gmail.com
 */
public class Response extends HttpServletResponseWrapper {

  public enum ContentDispositionType {
    ATTACHMENT,
    INLINE
  }

  public Response(HttpServletResponse response) {
    super(response);
  }

  /**
   * Writes data to the output stream using body handler based on specified content type.
   *
   * @param data
   * @param contentType
   */
  public void setBody(Object data, String contentType) {
    BodyHandler handler =
        Objects.requireNonNull(
            Bindings.getBodyHandlers().get(contentType),
            "Missing body handler for media type[%s]".formatted(contentType));
    setBody(data, handler);
  }

  /**
   * Writes data to the output stream using body handler based on HTTP Content-Type header.
   *
   * @param data
   */
  public void setBody(Object data) {
    setBody(data, getContentType());
  }

  /**
   * Writes data to the output stream using specified body handler.
   *
   * @param data
   * @param handler
   */
  public void setBody(Object data, BodyHandler handler) {
    Objects.requireNonNull(handler, "Missing body handler");
    try {
      handler.write(data, getOutputStream());
    } catch (IOException e) {
      throw new ApiException(SC_INTERNAL_SERVER_ERROR, e);
    }
  }

  /**
   * Convenient method for setting Content-Disposition HTTP header.
   *
   * @param filename
   * @param type
   */
  public void setContentDisposition(String filename, ContentDispositionType type) {
    setHeader(
        "Content-Disposition", "%s; filename=%s".formatted(type.name().toLowerCase(), filename));
  }
}
