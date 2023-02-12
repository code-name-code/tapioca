package hr.codenamecode.tapioca;

import static jakarta.servlet.http.HttpServletResponse.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Optional;
import java.util.function.BiFunction;
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
   * Converts incoming request body into an instance of provided class. This method should be called
   * only once per request handler otherwise exception will be thrown (stream can be read only
   * once). This method assumes that incoming request body is of <b>application/json</b> media type.
   * If conversion from JSON to object fails, {@link ApiException} with SC_BAD_REQUEST(400) status
   * is thrown.
   *
   * <p>NOTE: This method requires {@link Api#jsonReader} to be set. Use {@link
   * Api#setJsonReader(java.util.function.BiFunction)} to set it.
   *
   * @param <T>
   * @param clazz Targeted object instance class
   * @return An instance of parameter clazz
   */
  @SuppressWarnings("unchecked")
  public <T> T json(Class<T> clazz) {
    try {
      byte[] content = getInputStream().readAllBytes();
      BiFunction<String, Class<?>, ?> jsonReader = Bindings.getJsonReader();
      return (T) jsonReader.apply(new String(content), clazz);
    } catch (IOException e) {
      throw new ApiException(SC_BAD_REQUEST, e);
    }
  }
}
