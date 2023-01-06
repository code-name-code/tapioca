package hr.codenamecode.tapioca;

import hr.codenamecode.tapioca.internal.Processor;

/**
 * {@link Processor} knows how to handle these kind of exceptions. If there is no global exception
 * handler defined and {@link ApiException} is thrown inside {@link WebMethod}, {@link Processor}
 * will write status and message received from the caught {@link ApiException} to the response
 * output stream.
 *
 * <p>If there is no special exception handling logic required inside your {@link WebMethod} use
 * this exception to report basic textual message with HTTP status code to the caller.
 *
 * @author vedransmid@gmail.com
 */
public class ApiException extends RuntimeException {

  private final int status;

  public ApiException(int status) {
    this.status = status;
  }

  public ApiException(int status, String message) {
    super(message);
    this.status = status;
  }

  public ApiException(int status, String message, Throwable cause) {
    super(message, cause);
    this.status = status;
  }

  public int getStatus() {
    return status;
  }
}
