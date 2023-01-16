package hr.codenamecode.tapioca;

import static jakarta.servlet.http.HttpServletResponse.*;

import hr.codenamecode.tapioca.internal.Processor;
import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  private final Matcher pathMatcher;

  public Request(HttpServletRequest request, String mapping) {
    super(request);
    String matchedValue = getMatchedValue(request.getHttpServletMapping());
    this.pathMatcher = Pattern.compile(mapping).matcher(matchedValue);
  }

  /**
   * s e.g.
   *
   * <p>For mapping: <b>http://localhost:8080/resources/cars/(?&lt;name&gt;\w+)</b> and incoming
   * request: <b>http://localhost:8080/resources/cars/porsche</b> method call <code>
   * getPathParam("name")</code> will return "porsche" wrapped in an {@link Optional}.
   *
   * @param name Path parameter name
   * @return Path parameter value for the provided path parameter name
   */
  public Optional<String> getPathParam(String name) {
    try {
      boolean match = pathMatcher.find();
      return Optional.ofNullable(match ? pathMatcher.group(name) : null);
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  /**
   * Converts incoming request body into an instance of provided class. This method should be called
   * only once per request handler otherwise exception will be thrown (stream can be read only once).
   * This method assumes that incoming request body is of <b>application/json</b> media type. If
   * conversion from JSON to object fails, {@link ApiException} with SC_BAD_REQUEST(400) status is
   * thrown.
   *
   * @param <T>
   * @param clazz Targeted object instance class
   * @return An instance of parameter clazz
   */
  @SuppressWarnings("unchecked")
  public <T> T json(Class<T> clazz) {
    try {
      byte[] content = getInputStream().readAllBytes();
      return (T) Bindings.getJsonReader().apply(new String(content), clazz);
    } catch (IOException e) {
      throw new ApiException(SC_BAD_REQUEST);
    }
  }

  /**
   * This method is used internally by Tapioca to retrieve request handler which will be executed by the
   * {@link Processor}.
   *
   * @param httpServletMapping {@link HttpServletMapping}
   * @return A part or request's URI which is matched by the servlet. This URI part is then matched
   *     against each key in the {@link java.util.HashMap} containing request handler mappings. For more
   *     details on how matching is done, see {@link Processor}.
   */
  public static String getMatchedValue(HttpServletMapping httpServletMapping) {
    return switch (httpServletMapping.getMappingMatch()) {
      case EXACT -> ""; // to accept empty mapping, e.g. /test should fire request handler mapped to ""
      case PATH -> httpServletMapping.getMatchValue();
      default -> null;
    };
  }
}
