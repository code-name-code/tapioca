package hr.garnet.gapi;

import jakarta.servlet.ServletContext;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Bindings for the {@link ServletContext}. This is basically a utility class you can use to reduce
 * boilerplate when extracting attributes from the {@link ServletContext}.
 *
 * @author vedransmid@gmail.com
 */
public class ApiBindings {

  private static ServletContext servletContext;

  public static final String SC_JSON_READER = "hr.garnet.gapi.json.reader";
  public static final String SC_JSON_WRITER = "hr.garnet.gapi.json.writer";
  public static final String SC_COMMAND_PROVIDER = "hr.garnet.gapi.command.provider";
  public static final String SC_EXCEPTION_HANDLER = "hr.garnet.gapi.exception.handler";

  /**
   * Dedicated {@link ServletContext} attribute used by GAPI internally to convert incoming JSON
   * request body into an instance of specified class.
   *
   * @return {@link BiFunction} performing conversion
   */
  @SuppressWarnings("unchecked")
  public static BiFunction<String, Class<?>, ?> getJsonReader() {
    return (BiFunction<String, Class<?>, ?>) servletContext.getAttribute(SC_JSON_READER);
  }

  /**
   * Dedicated {@link ServletContext} attribute used by GAPI internally to convert objects into JSON
   * which can be written to the {@link jakarta.servlet.http.HttpServlet} output stream.
   *
   * @return {@link Function} performing conversion
   */
  @SuppressWarnings("unchecked")
  public static Function<Object, String> getJsonWriter() {
    return (Function<Object, String>) servletContext.getAttribute(SC_JSON_WRITER);
  }

  /**
   * Dedicated attribute used internally by GAPI as a {@link ApiCommand} provider.
   *
   * @return {@link Function} which provisions @{link {@link ApiCommand}} to be executed by {@link
   *     hr.garnet.gapi.internal.ApiServlet}.
   */
  @SuppressWarnings("unchecked")
  public static Function<Class<? extends ApiCommand>, ApiCommand> getCommandProvider() {
    return (Function<Class<? extends ApiCommand>, ApiCommand>)
        servletContext.getAttribute(SC_COMMAND_PROVIDER);
  }

  /**
   * Dedicated attribute used internally by GAPI to handle exceptions thrown during {@link
   * hr.garnet.gapi.internal.ApiServlet} execution.
   *
   * @return {@link ApiExceptionHandler} wrapped in {@link Optional}
   */
  public static Optional<ApiExceptionHandler> getExceptionHandler() {
    return Optional.ofNullable(
        (ApiExceptionHandler) servletContext.getAttribute(SC_EXCEPTION_HANDLER));
  }

  /**
   * Get any attribute from {@link ServletContext} by attribute's name. Attributes should be bound
   * to {@link ServletContext} using {@link Api#bind(String, Object)} method.
   *
   * @param name Attribute name
   * @return Attribute value found in the {@link ServletContext} for the given attribute name
   */
  @SuppressWarnings("unchecked")
  public static <T> T lookup(String name) {
    return (T) servletContext.getAttribute(name);
  }

  /**
   * This method should only be called by GAPI library. It is used to reduce boilerplate code needed
   * to extract attribute from {@link ServletContext} when using static methods.
   *
   * @param sc {@link ServletContext}
   */
  public static void setServletContext(ServletContext sc) {
    if (Objects.isNull(servletContext)) {
      servletContext = sc;
    }
  }
}
