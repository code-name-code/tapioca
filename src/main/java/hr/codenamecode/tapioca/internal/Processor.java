package hr.codenamecode.tapioca.internal;

import hr.codenamecode.tapioca.ServletConfigurer;
import hr.codenamecode.tapioca.Response;
import hr.codenamecode.tapioca.Bindings;
import hr.codenamecode.tapioca.ExceptionHandler;
import hr.codenamecode.tapioca.Request;
import hr.codenamecode.tapioca.RequestHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    Request apiReq = new Request(req, getPathMatcher(req, matchedMapping));
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
    } catch (Exception e) {
      ExceptionHandler exceptionHandler = Bindings.getExceptionHandler();
      if (exceptionHandler != null) {
        exceptionHandler.handleException(e, apiReq, apiResp);
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

  private Matcher getPathMatcher(HttpServletRequest req, String matchedMapping) {
    String matchedValue = getMatchedValue(req.getHttpServletMapping());
    return Pattern.compile(matchedMapping).matcher(matchedValue);
  }

  private String getMatchedMapping(
      Map<String, RequestHandlerHolder> requestHandlers, HttpServletMapping httpServletMapping) {

    for (String mapping : requestHandlers.keySet()) {
      String matchedValue = getMatchedValue(httpServletMapping);
      if (matchedValue != null && matchedValue.matches(mapping)) {
        return mapping;
      }
    }

    return null;
  }

  /**
   * This method is used internally by Tapioca to retrieve request handler which will be executed by
   * the {@link Processor}.
   *
   * @param httpServletMapping {@link HttpServletMapping}
   * @return A part or request's URI which is matched by the servlet. This URI part is then matched
   *     against each key in the {@link java.util.HashMap} containing request handler mappings. For
   *     more details on how matching is done, see {@link Processor}.
   */
  private String getMatchedValue(HttpServletMapping httpServletMapping) {
    return switch (httpServletMapping.getMappingMatch()) {
      case EXACT -> ""; // to accept empty mapping, e.g. /test should fire request handler mapped to
        // ""
      case PATH -> httpServletMapping.getMatchValue();
      default -> null;
    };
  }
}
