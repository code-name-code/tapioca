package hr.garnet.gapi;

import static jakarta.servlet.http.HttpServletResponse.*;

import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiRequest extends HttpServletRequestWrapper {

  private final Matcher pathMatcher;

  public ApiRequest(HttpServletRequest request, String mapping) {
    super(request);
    String matchedValue = getMatchedValue(request.getHttpServletMapping());
    this.pathMatcher = Pattern.compile(mapping).matcher(matchedValue);
  }

  public Optional<String> getPathParam(String name) {
    try {
      boolean match = pathMatcher.find();
      return Optional.ofNullable(match ? pathMatcher.group(name) : null);
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  public <T> T json(Class<T> clazz) {
    try {
      byte[] content = getInputStream().readAllBytes();
      return (T) ApiBindings.getJsonReader().apply(new String(content), clazz);
    } catch (IOException e) {
      throw new ApiException(SC_BAD_REQUEST);
    }
  }

  public static String getMatchedValue(HttpServletMapping httpServletMapping) {
    return switch (httpServletMapping.getMappingMatch()) {
      case EXACT -> ""; // to accept empty mapping, e.g. /test should fire command mapped to ""
      case PATH -> httpServletMapping.getMatchValue();
      default -> null;
    };
  }
}
