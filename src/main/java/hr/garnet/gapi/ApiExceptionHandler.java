package hr.garnet.gapi;

/**
 * @author vedransmid@gmail.com
 */
public interface ApiExceptionHandler {
  void handleException(Exception e, ApiRequest req, ApiResponse resp);
}
