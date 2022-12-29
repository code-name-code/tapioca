package hr.garnet.gapi;

import jakarta.servlet.ServletContext;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author vsmid
 */
public class ApiBindings {

  private static ServletContext servletContext;

  public static final String SC_JSON_READER = "hr.garnet.gapi.json.reader";
  public static final String SC_JSON_WRITER = "hr.garnet.gapi.json.writer";
  public static final String SC_COMMAND_PROVIDER = "hr.garnet.gapi.command.provider";
  public static final String SC_EXCEPTION_HANDLER = "hr.garnet.gapi.exception.handler";

  public static BiFunction<String, Class<?>, ?> getJsonReader() {
    return (BiFunction<String, Class<?>, ?>) servletContext.getAttribute(SC_JSON_READER);
  }

  public static Function<Object, String> getJsonWriter() {
    return (Function<Object, String>) servletContext.getAttribute(SC_JSON_WRITER);
  }

  public static Function<Class<? extends ApiCommand>, ApiCommand> getCommandProvider() {
    return (Function<Class<? extends ApiCommand>, ApiCommand>)
        servletContext.getAttribute(SC_COMMAND_PROVIDER);
  }

  public static Optional<ApiExceptionHandler> getExceptionHandler() {
    return Optional.ofNullable(
        (ApiExceptionHandler) servletContext.getAttribute(SC_EXCEPTION_HANDLER));
  }

  public static <T> T get(String key) {
    return (T) servletContext.getAttribute(key);
  }

  /**
   * This method should only be called by GAPI library.
   *
   * @param sc {@link ServletContext}
   */
  public static void setServletContext(ServletContext sc) {
    if (Objects.isNull(servletContext)) {
      servletContext = sc;
    }
  }
}
