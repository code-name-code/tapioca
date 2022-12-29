package hr.garnet.gapi;

import static jakarta.servlet.http.HttpServletResponse.*;

import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Wrapper around {@link HttpServletRequest} which provides some additional, convenient methods out
 * of the box.
 *
 * @author vedransmid@gmail.com
 */
public class ApiRequest extends HttpServletRequestWrapper {

  private final Matcher pathMatcher;

  public ApiRequest(HttpServletRequest request, String mapping) {
    super(request);
    String matchedValue = getMatchedValue(request.getHttpServletMapping());
    this.pathMatcher = Pattern.compile(mapping).matcher(matchedValue);
  }

  /**
   * @param name Path parameter name.
   * @return Path parameter value for the provided path parameter name.
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
   * Converts request body to an instance of provided class. This method should be called only once
   * per command. This method assumes that incoming request body is of application/json media type.
   *
   * @param <T>
   * @param clazz
   * @return An instance of clazz.
   */
  public <T> T json(Class<T> clazz) {
    try {
      byte[] content = getInputStream().readAllBytes();
      return (T) ApiBindings.getJsonReader().apply(new String(content), clazz);
    } catch (IOException e) {
      throw new ApiException(SC_BAD_REQUEST);
    }
  }

  /**
   * This method is used internally by GAPI to retrieve command which will be executed.
   *
   * @param httpServletMapping
   * @return A portion or request's uri which is matched by the servlet.
   */
  public static String getMatchedValue(HttpServletMapping httpServletMapping) {
    return switch (httpServletMapping.getMappingMatch()) {
      case EXACT -> ""; // to accept empty mapping, e.g. /test should fire command mapped to ""
      case PATH -> httpServletMapping.getMatchValue();
      default -> null;
    };
  }
}
