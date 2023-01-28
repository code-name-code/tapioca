package hr.codenamecode.tapioca.internal;

import hr.codenamecode.tapioca.Response;
import hr.codenamecode.tapioca.Bindings;
import hr.codenamecode.tapioca.Request;
import hr.codenamecode.tapioca.RequestHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author vedransmid@gmail.com
 */
public class Processor extends HttpServlet {

  private final ServletConfigurer apiConfigurer;

  public Processor(ServletConfigurer servletConfigurer) {
    this.apiConfigurer = servletConfigurer;
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    Map<String, RequestHandlerHolder> requestHandlers = getRequestHandlerMappings(req.getMethod());

    if (requestHandlers == null || requestHandlers.isEmpty()) {
      try {
        super.service(req, resp);
      } catch (ServletException | IOException ex) {
      }
      return;
    }

    String matchedMapping = getMatchedMapping(requestHandlers, req.getHttpServletMapping());

    if (matchedMapping == null) {
      try {
        super.service(req, resp);
      } catch (ServletException | IOException ex) {
      }
      return;
    }

    Request apiReq = new Request(req, matchedMapping);
    Response apiResp = new Response(resp);

    try {
      RequestHandler method;
      RequestHandlerHolder requestHandlerHolder = requestHandlers.get(matchedMapping);

      if (requestHandlerHolder.containsImplementation()) {
        method = requestHandlerHolder.getRequestHandlerImpl();
      } else {
        method =
            Bindings.getRequestHandlerFactory()
                .apply(requestHandlerHolder.getRequestHandlerClass());
      }

      method.setServletConfig(getServletConfig());
      method.handle(apiReq, apiResp);

      if (!resp.isCommitted()) {
        resp.flushBuffer();
      }
    } catch (Exception e) {
      Bindings.getExceptionHandler().handleException(e, apiReq, apiResp);
      try {
        if (!resp.isCommitted()) {
          resp.flushBuffer();
        }
      } catch (IOException ignored) {
      }
    }
  }

  private Map<String, RequestHandlerHolder> getRequestHandlerMappings(String httpMethod) {
    return switch (httpMethod) {
      case "GET" -> apiConfigurer.getGetMapping();
      case "POST" -> apiConfigurer.getPostMapping();
      case "PUT" -> apiConfigurer.getPutMapping();
      case "DELETE" -> apiConfigurer.getDeleteMapping();
      case "TRACE" -> apiConfigurer.getTraceMapping();
      case "HEAD" -> apiConfigurer.getHeadMapping();
      case "OPTIONS" -> apiConfigurer.getOptionsMapping();
      default -> null;
    };
  }

  private String getMatchedMapping(
      Map<String, RequestHandlerHolder> requestHandlers, HttpServletMapping httpServletMapping) {

    for (String mapping : requestHandlers.keySet()) {
      String matchedValue = Request.getMatchedValue(httpServletMapping);
      if (matchedValue != null && matchedValue.matches(mapping)) {
        return mapping;
      }
    }

    return null;
  }
}
