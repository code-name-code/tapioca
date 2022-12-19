package hr.garnet.gapi;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

public class ApiServlet extends HttpServlet {

  private final ApiServletConfigurer apiConfigurer;

  public ApiServlet(ApiServletConfigurer apiConfigurer) {
    this.apiConfigurer = apiConfigurer;
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    Map<String, Class<? extends ApiCommand>> commandMappings =
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
        Class<? extends ApiCommand> commandClass = commandMappings.get(mapping);
        commandMappingFound = true;
        try {
          ApiSCBindings.getCommandProvider(req.getServletContext())
              .apply(commandClass)
              .execute(new ApiRequest(req, mapping), new ApiResponse(req, resp));
        } catch (ApiException e) {
          resp.setStatus(e.getStatus());
        }
      }
    }

    if (!commandMappingFound) {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
  }
}
