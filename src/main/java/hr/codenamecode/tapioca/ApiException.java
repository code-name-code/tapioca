package hr.codenamecode.tapioca;

import hr.codenamecode.tapioca.internal.Processor;

/**
 * {@link Processor} knows how to handle these kind of exceptions. If there is no global exception
 * handler defined and {@link ApiException} is thrown inside {@link RequestHandler}, {@link
 * Processor} will write status and message received from the caught {@link ApiException} to the
 * response output stream.
 *
 * <p>If there is no special exception handling logic required inside your {@link RequestHandler}
 * use this exception to report basic textual message with HTTP status code to the caller.
 *
 * @author vedransmid@gmail.com
 */
public class ApiException extends RuntimeException {

  private final int status;
  private boolean silent = false;

  public ApiException(int status) {
    this.status = status;
  }

  public ApiException(int status, String message) {
    super(message);
    this.status = status;
  }

  public ApiException(int status, Throwable cause) {
    super(cause);
    this.status = status;
  }

  public ApiException(int status, String message, Throwable cause) {
    super(message, cause);
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

  public boolean isSilent() {
    return silent;
  }

  /**
   * Sometimes you may wish to avoid printing full {@link ApiException} stack trace and just leave
   * an INFO note. This is the case where you use {@link ApiException} as a control mechanism and
   * you know what you are doing. An example would be handling of path parameter by {@link
   * Request#getPathParam(java.lang.String)} method.
   *
   * <p>NOTE: This only applies if you are using default exception handler.
   *
   * @param silent If set to true, exception is logged with level INFO and there will be no stack
   *     trace in the log, otherwise, log level SEVERE is used along with stack trace.
   */
  public void setSilent(boolean silent) {
    this.silent = silent;
  }
}
