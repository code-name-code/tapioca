package hr.codenamecode.tapioca;

import hr.codenamecode.tapioca.internal.Processor;
import jakarta.servlet.ServletContext;
import java.util.NoSuchElementException;
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
  public static final String SC_REQUEST_HANDLER_FACTORY =
      "hr.codenamecode.tapioca.requesthandler.factory";
  public static final String SC_EXCEPTION_HANDLER = "hr.codenamecode.tapioca.exception.handler";

  /**
   * Dedicated {@link ServletContext} attribute used by tapioca internally to convert incoming JSON
   * request body into an instance of specified class.
   *
   * @return {@link BiFunction} performing conversion
   * @throws NullPointerException
   */
  @SuppressWarnings("unchecked")
  public static BiFunction<String, Class<?>, ?> getJsonReader() throws NullPointerException {
    Object reader = servletContext.getAttribute(SC_JSON_READER);
    return (BiFunction<String, Class<?>, ?>)
        Objects.requireNonNull(reader, "JSON reader is not provided");
  }

  /**
   * Dedicated {@link ServletContext} attribute used by tapioca internally to convert objects into
   * JSON which can be written to the {@link jakarta.servlet.http.HttpServlet} output stream.
   *
   * @return {@link Function} performing conversion
   * @throws NullPointerException
   */
  @SuppressWarnings("unchecked")
  public static Function<Object, String> getJsonWriter() throws NullPointerException {
    Object writer = servletContext.getAttribute(SC_JSON_WRITER);
    return (Function<Object, String>) Objects.requireNonNull(writer, "JSON writer is not provided");
  }

  /**
   * Dedicated attribute used internally by Tapioca as a {@link RequestHandler} provider.
   *
   * @return {@link Function} which provisions @{link {@link RequestHandler}} to be executed by
   *     {@link Processor}.
   * @throws NullPointerException
   */
  @SuppressWarnings("unchecked")
  public static Function<Class<? extends RequestHandler>, RequestHandler> getRequestHandlerFactory()
      throws NullPointerException {
    Object requestHandlerFactory = servletContext.getAttribute(SC_REQUEST_HANDLER_FACTORY);
    return (Function<Class<? extends RequestHandler>, RequestHandler>)
        Objects.requireNonNull(requestHandlerFactory, "Request handler factory is not provided");
  }

  /**
   * Dedicated attribute used internally by Tapioca to handle exceptions thrown during {@link
   * Processor} execution.
   *
   * @return {@link ExceptionHandler} wrapped in {@link Optional}
   * @throws NullPointerException
   */
  public static ExceptionHandler getExceptionHandler() throws NullPointerException {
    Object exceptionHandler = servletContext.getAttribute(SC_EXCEPTION_HANDLER);
    return (ExceptionHandler)
        Objects.requireNonNull(exceptionHandler, "Exception handler is not provided");
  }

  /**
   * Get any attribute from {@link ServletContext} by attribute's name.Attributes should be bound to
   * {@link ServletContext} using {@link Api#bind(String, Object)} method.
   *
   * @param <T>
   * @param name Attribute name
   * @return Attribute value found in the {@link ServletContext} for the given attribute name
   * @throws NoSuchElementException
   */
  @SuppressWarnings("unchecked")
  public static <T> T lookup(String name) throws NoSuchElementException {
    Object attribute = servletContext.getAttribute(name);
    if (attribute == null) {
      throw new NoSuchElementException("Attribute [%s] is not bound".formatted(name));
    }
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
