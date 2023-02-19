package hr.codenamecode.tapioca;

import static jakarta.servlet.http.HttpServletResponse.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;

/**
 * Wrapper around {@link HttpServletRequest} which provides some additional, convenient methods out
 * of the box. Used by {@link RequestHandler}.
 *
 * @author vedransmid@gmail.com
 */
public class Request extends HttpServletRequestWrapper {

  /**
   * Matcher used to extract path parameters. It assumes that mapping for the request handler uses
   * regular expressions with named groups.
   *
   * <p>e.g. <b>http://localhost:8080/resources/cars/(?&lt;name&gt;\w+)</b>
   */
  private Matcher pathMatcher;

  public Request(HttpServletRequest request) {
    super(request);
  }

  public Request(HttpServletRequest request, Matcher pathMatcher) {
    super(request);
    this.pathMatcher = pathMatcher;
  }

  /**
   * Get path parameter. e.g.
   *
   * <p>For mapping: <b>http://localhost:8080/resources/cars/(?&lt;name&gt;\w+)</b> and incoming
   * request: <b>http://localhost:8080/resources/cars/porsche</b> method call <code>
   * getPathParam("name")</code> will return "porsche" wrapped in an {@link Optional}.
   *
   * @param name Path parameter name
   * @return Path parameter value for the provided path parameter name
   */
  public Optional<String> getPathParam(String name) {
    if (pathMatcher == null) {
      throw new ApiException(SC_BAD_REQUEST, "Path matcher is not provided");
    }

    try {
      boolean match = pathMatcher.find();
      return Optional.ofNullable(match ? pathMatcher.group(name) : null);
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  /**
   * Convert request body to a given type using specified media type handler.
   *
   * @param <T>
   * @param type
   * @param handler
   * @return
   */
  public <T> T body(Class<T> type, MediaTypeHandler handler) {
    byte[] content;

    try {
      content = getInputStream().readAllBytes();
    } catch (IOException e) {
      throw new ApiException(SC_BAD_REQUEST, e);
    }

    String input = new String(content);
    return handler.from(input, type);
  }

  /**
   * Convert request body to a given type using media type handler based on Content-Type HTTP header
   * value.
   *
   * @param <T>
   * @param type
   * @return
   */
  public <T> T body(Class<T> type) {
    MediaTypeHandler handler = Bindings.getMediaTypeHandlers().get(getContentType());
    return body(type, handler);
  }
}
