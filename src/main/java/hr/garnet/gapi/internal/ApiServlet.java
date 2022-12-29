package hr.garnet.gapi.internal;

import hr.garnet.gapi.ApiBindings;
import hr.garnet.gapi.ApiCommand;
import hr.garnet.gapi.ApiRequest;
import hr.garnet.gapi.ApiResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/** @author vedransmid@gmail.com */
public class ApiServlet extends HttpServlet {

  private final ApiServletConfigurer apiConfigurer;

  public ApiServlet(ApiServletConfigurer apiConfigurer) {
    this.apiConfigurer = apiConfigurer;
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    Map<String, ApiCommandHolder> commandMappings =
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

    if (commandMappings == null) {
      resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
      return;
    }

    boolean commandMappingFound = false;
    for (String mapping : commandMappings.keySet()) {
      String matchedValue = ApiRequest.getMatchedValue(req.getHttpServletMapping());
      if (matchedValue.matches(mapping)) {
        ApiRequest apiReq = new ApiRequest(req, mapping);
        ApiResponse apiResp = new ApiResponse(resp);
        ApiCommandHolder apiCommandHolder = commandMappings.get(mapping);
        ApiCommand command;
        commandMappingFound = true;
        try {
          if (apiCommandHolder.containsImplementation()) {
            command = apiCommandHolder.getCommandImpl();
          } else {
            command = ApiBindings.getCommandProvider().apply(apiCommandHolder.getCommandClass());
          }
          command.setServletConfig(getServletConfig());
          command.execute(apiReq, apiResp);
          if (!resp.isCommitted()) {
            resp.flushBuffer();
          }
        } catch (Exception e) {
          ApiBindings.getExceptionHandler()
              .ifPresent(
                  eh -> {
                    eh.handleException(e, apiReq, apiResp);
                    try {
                      if (!resp.isCommitted()) {
                        resp.flushBuffer();
                      }
                    } catch (IOException ignored) {

                    }
                  });
        }
      }
    }

    if (!commandMappingFound) {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
  }
}
