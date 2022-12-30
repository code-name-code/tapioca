package hr.garnet.gapi;

/**
 * This exception handler handles all exceptions which occur during servlet execution. This
 * basically means that you can delegate all exception handling logic in your {@link ApiCommand}
 * implementations to the custom exception handler implementation. You can register custom exception
 * handler in classes extending {@link Api} by calling {@link
 * Api#setExceptionHandler(ApiExceptionHandler)} method.
 *
 * <p>Only one exception handler should be defined per application/service.
 *
 * @author vedransmid@gmail.com
 */
public interface ApiExceptionHandler {

  /**
   * Method called by the {@link hr.garnet.gapi.internal.ApiServlet} to handle exception.
   *
   * @param e Exception thrown
   * @param req Incoming {@link jakarta.servlet.http.HttpServletRequest}
   * @param resp Outgoing {@link jakarta.servlet.http.HttpServletResponse}
   */
  void handleException(Exception e, ApiRequest req, ApiResponse resp);
}
