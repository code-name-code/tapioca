package hr.garnet.gapi;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

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
        ApiResponse apiResp = new ApiResponse(req, resp);
        ApiCommandHolder apiCommandHolder = commandMappings.get(mapping);
        ApiCommand command;
        commandMappingFound = true;
        try {
          if (apiCommandHolder.containsImplementation()) {
            command = apiCommandHolder.getCommandImpl();
          } else {
            command =
                ApiSCBindings.getCommandProvider(req.getServletContext())
                    .apply(apiCommandHolder.getCommandClass());
          }
          command.execute(apiReq, apiResp);
          if (!resp.isCommitted()) {
            resp.flushBuffer();
          }
        } catch (Exception e) {
          ApiSCBindings.getExceptionHandler(req.getServletContext())
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