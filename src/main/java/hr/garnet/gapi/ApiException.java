package hr.garnet.gapi;

/**
 * {@link hr.garnet.gapi.internal.ApiServlet} knows how to handle these kind of exceptions. If there
 * is no global exception handler defined and {@link ApiException} is thrown inside {@link
 * ApiCommand}, {@link hr.garnet.gapi.internal.ApiServlet} will write status and message received
 * from the caught {@link ApiException} to the response output stream.
 *
 * @author vedransmid@gmail.com
 */
public class ApiException extends RuntimeException {

  private final int status;

  public ApiException(int status) {
    this.status = status;
  }

  public ApiException(Throwable cause, int status) {
    super(cause);
    this.status = status;
  }

  public ApiException(String message, Throwable cause, int status) {
    super(message, cause);
    this.status = status;
  }

  public ApiException(int status, String message) {
    super(message);
    this.status = status;
  }

  public int getStatus() {
    return status;
  }
}
