package hr.codenamecode.tapioca.internal;

import hr.codenamecode.tapioca.Response;
import hr.codenamecode.tapioca.ApiException;
import hr.codenamecode.tapioca.Bindings;
import hr.codenamecode.tapioca.Request;
import hr.codenamecode.tapioca.WebMethod;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

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
    Map<String, WebMethodHolder> webMethodMappings =
        switch (req.getMethod()) {
          case "GET" -> apiConfigurer.getGetMapping();
          case "POST" -> apiConfigurer.getPostMapping();
          case "PUT" -> apiConfigurer.getPutMapping();
          case "DELETE" -> apiConfigurer.getDeleteMapping();
          case "TRACE" -> apiConfigurer.getTraceMapping();
          case "HEAD" -> apiConfigurer.getHeadMapping();
          case "OPTIONS" -> apiConfigurer.getOptionsMapping();
          default -> null;
        };

    if (webMethodMappings == null) {
      resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
      return;
    }

    boolean mappingFound = false;
    for (String mapping : webMethodMappings.keySet()) {
      String matchedValue = Request.getMatchedValue(req.getHttpServletMapping());
      if (matchedValue.matches(mapping)) {
        Request apiReq = new Request(req, mapping);
        Response apiResp = new Response(resp);
        WebMethodHolder webMethodHolder = webMethodMappings.get(mapping);
        WebMethod method;
        mappingFound = true;
        try {
          if (webMethodHolder.containsImplementation()) {
            method = webMethodHolder.getWebMethodImpl();
          } else {
            method = Bindings.getCommandProvider().apply(webMethodHolder.getWebMethodClass());
          }
          method.setServletConfig(getServletConfig());
          method.invoke(apiReq, apiResp);
          if (!resp.isCommitted()) {
            resp.flushBuffer();
          }
        } catch (Exception e) {
          Bindings.getExceptionHandler()
              .ifPresentOrElse(
                  eh -> {
                    eh.handleException(e, apiReq, apiResp);
                    try {
                      if (!resp.isCommitted()) {
                        resp.flushBuffer();
                      }
                    } catch (IOException ignored) {

                    }
                  },
                  () -> {
                    try {
                      if (!resp.isCommitted()) {
                        if (e instanceof ApiException apiException) {
                          resp.setStatus(apiException.getStatus());
                          if (Objects.nonNull(apiException.getMessage())) {
                            resp.getWriter().append(apiException.getMessage());
                          }
                        } else {
                          resp.setStatus(SC_INTERNAL_SERVER_ERROR);
                        }
                        resp.flushBuffer();
                      }
                    } catch (IOException ignored) {

                    }
                  });
          return;
        }
      }
    }

    if (!mappingFound) {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
  }
}
