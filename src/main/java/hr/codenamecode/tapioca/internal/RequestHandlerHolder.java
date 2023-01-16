package hr.codenamecode.tapioca.internal;

import hr.codenamecode.tapioca.RequestHandler;

import java.util.Objects;

/**
 * @author vedransmid@gmail.com
 */
public class RequestHandlerHolder {

  private Class<? extends RequestHandler> requestHandlerClass;
  private RequestHandler requestHandlerImpl;

  private RequestHandlerHolder() {}

  public RequestHandlerHolder(Class<? extends RequestHandler> requestHandlerClass) {
    this.requestHandlerClass = requestHandlerClass;
  }

  public RequestHandlerHolder(RequestHandler requestHandlerImpl) {
    this.requestHandlerImpl = requestHandlerImpl;
  }

  public boolean containsImplementation() {
    return Objects.nonNull(requestHandlerImpl);
  }

  public RequestHandler getRequestHandlerImpl() {
    return requestHandlerImpl;
  }

  public Class<? extends RequestHandler> getRequestHandlerClass() {
    return requestHandlerClass;
  }
}
