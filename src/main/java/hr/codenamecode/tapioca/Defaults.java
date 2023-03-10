package hr.codenamecode.tapioca;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class Defaults {

  private static final String ERROR_LOG_MESSAGE = "An error ocurred in the processor servlet";

  /**
   * Default request handler factory. Describes how new request handler instances are created. This
   * is an entry point for introducing dependency injection frameworks such as Google Guice, CDI
   * etc. This factory will be used when HTTP method is defined by using URI path and a request
   * handler class.
   *
   * <p>e.g. <code>api.get("cars", GetCars.class);</code>
   *
   * <p>e.g. <code>
   *  setRequestHandlerFactory(requestHandlerClass -> CDI.current().select(requestHandlerClass).get());
   * </code>
   */
  public static final Function<Class<? extends RequestHandler>, RequestHandler>
      DEFAULT_REQUEST_HANDLER_FACTORY =
          requestHandlerClass -> {
            try {
              return requestHandlerClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException
                | IllegalAccessException
                | NoSuchMethodException
                | InvocationTargetException e) {
              throw new RuntimeException(
                  "Failed to instantiate request handler for class[%s]"
                      .formatted(requestHandlerClass.getName()));
            }
          };

  /**
   * Default exception handler will write HTTP status and message received from {@link ApiException}
   * to the underlying servlet output stream. All other {@link Exception} are considered unhandled
   * and will only report HTTP status 500.
   */
  public static final ExceptionHandler DEFAULT_EXCEPTION_HANDLER =
      (e, req, resp) -> {
        if (e == null || resp == null) return;
        try {
          if (e instanceof ApiException apiException) {
            req.getServletContext().log("API exception: [%s]".formatted(e.getMessage()));
            resp.setStatus(apiException.getStatus());
            resp.getWriter().write(e.getMessage());
          } else {
            req.getServletContext().log(ERROR_LOG_MESSAGE, e);
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
          }
        } catch (IOException ignored) {

        }
      };
}
