package hr.codenamecode.tapioca;

import hr.codenamecode.tapioca.internal.Processor;
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
public class Bindings {

  private static ServletContext servletContext;

  public static final String SC_JSON_READER = "hr.codenamecode.tapioca.json.reader";
  public static final String SC_JSON_WRITER = "hr.codenamecode.tapioca.json.writer";
  public static final String SC_WEB_METHOD_PROVIDER = "hr.codenamecode.tapioca.webmethod.provider";
  public static final String SC_EXCEPTION_HANDLER = "hr.codenamecode.tapioca.exception.handler";

  /**
   * Dedicated {@link ServletContext} attribute used by tapioca internally to convert incoming JSON
   * request body into an instance of specified class.
   *
   * @return {@link BiFunction} performing conversion
   */
  @SuppressWarnings("unchecked")
  public static BiFunction<String, Class<?>, ?> getJsonReader() {
    return (BiFunction<String, Class<?>, ?>) servletContext.getAttribute(SC_JSON_READER);
  }

  /**
   * Dedicated {@link ServletContext} attribute used by tapioca internally to convert objects into
   * JSON which can be written to the {@link jakarta.servlet.http.HttpServlet} output stream.
   *
   * @return {@link Function} performing conversion
   */
  @SuppressWarnings("unchecked")
  public static Function<Object, String> getJsonWriter() {
    return (Function<Object, String>) servletContext.getAttribute(SC_JSON_WRITER);
  }

  /**
   * Dedicated attribute used internally by Tapioca as a {@link WebMethod} provider.
   *
   * @return {@link Function} which provisions @{link {@link WebMethod}} to be executed by {@link
   *     Processor}.
   */
  @SuppressWarnings("unchecked")
  public static Function<Class<? extends WebMethod>, WebMethod> getCommandProvider() {
    return (Function<Class<? extends WebMethod>, WebMethod>)
        servletContext.getAttribute(SC_WEB_METHOD_PROVIDER);
  }

  /**
   * Dedicated attribute used internally by Tapioca to handle exceptions thrown during {@link
   * Processor} execution.
   *
   * @return {@link ExceptionHandler} wrapped in {@link Optional}
   */
  public static Optional<ExceptionHandler> getExceptionHandler() {
    return Optional.ofNullable(
        (ExceptionHandler) servletContext.getAttribute(SC_EXCEPTION_HANDLER));
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
   * This method should only be called by Tapioca library. It is used to reduce boilerplate code
   * needed to extract attribute from {@link ServletContext} when using static methods.
   *
   * @param sc {@link ServletContext}
   */
  public static void setServletContext(ServletContext sc) {
    if (Objects.isNull(servletContext)) {
      servletContext = sc;
    }
  }
}
