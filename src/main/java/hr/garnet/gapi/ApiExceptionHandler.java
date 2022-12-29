package hr.garnet.gapi;

/**
 * Implement thin interface to provide global exception handler. This exception handler will handle
 * all exceptions which occur during servlet execution. This basically means that you can delegate
 * all exception handling in your {@link ApiCommand} implementations to the custom exception
 * handler. You can register custom exception handler in the class which extends {@link Api} by
 * calling <b>setExceptionHandler</b> method.
 *
 * <p>Only one exception handler should be defined per application/service.
 *
 * @author vedransmid@gmail.com
 */
public interface ApiExceptionHandler {
  void handleException(Exception e, ApiRequest req, ApiResponse resp);
}
