package hr.codenamecode.tapioca;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class Defaults {

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

  public static final ExceptionHandler DEFAULT_EXCEPTION_HANDLER =
      (e, req, resp) -> {
        if (e == null || resp == null) return;
        try {
          if (e instanceof ApiException apiException) {
            resp.setStatus(apiException.getStatus());
            resp.getWriter().write(e.getMessage());
          } else {
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
          }
        } catch (IOException ignored) {

        }
      };
}
