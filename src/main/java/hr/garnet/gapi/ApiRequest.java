package hr.garnet.gapi;

import static jakarta.servlet.http.HttpServletResponse.*;

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
    String matchedValue = getMatchedValue(request, mapping);
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
      return (T) ApiSCBindings.getJsonReader(getServletContext()).apply(new String(content), clazz);
    } catch (IOException e) {
      throw new ApiException(SC_BAD_REQUEST);
    }
  }

  public static String getMatchedValue(HttpServletRequest req, String mapping) {
    int offset = mapping.startsWith("/") ? 0 : 1;
    String basePath = req.getContextPath() + req.getServletPath();
    return basePath.equals(req.getRequestURI())
        ? req.getRequestURI()
        : req.getRequestURI().substring(basePath.length() + offset);
  }
}
