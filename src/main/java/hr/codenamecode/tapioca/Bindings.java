package hr.codenamecode.tapioca;

import hr.codenamecode.tapioca.internal.Processor;
import jakarta.servlet.ServletContext;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Bindings for the {@link ServletContext}. This is basically a utility class you can use to reduce
 * boilerplate when extracting attributes from the {@link ServletContext}.
 *
 * @author vedransmid@gmail.com
 */
public class Bindings {

  private static ServletContext servletContext;

  public static final String SC_BODY_HANDLERS = "hr.codenamecode.tapioca.body.handlers";
  public static final String SC_REQUEST_HANDLER_FACTORY =
      "hr.codenamecode.tapioca.requesthandler.factory";
  public static final String SC_EXCEPTION_HANDLER = "hr.codenamecode.tapioca.exception.handler";

  /**
   * @return Map of registered body handlers.
   */
  public static Map<String, BodyHandler> getBodyHandlers() {
    return lookup(SC_BODY_HANDLERS);
  }

  /**
   * Get body handler for the given media type.
   *
   * @param mediaType
   * @return
   */
  public static BodyHandler getBodyHandler(String mediaType) {
    return getBodyHandlers().get(mediaType);
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
