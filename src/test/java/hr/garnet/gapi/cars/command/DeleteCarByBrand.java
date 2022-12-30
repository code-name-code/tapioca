package hr.garnet.gapi.cars.command;

import static jakarta.servlet.http.HttpServletResponse.SC_NO_CONTENT;

import hr.garnet.gapi.ApiCommand;
import hr.garnet.gapi.ApiRequest;
import hr.garnet.gapi.ApiResponse;

public class DeleteCarByBrand implements ApiCommand {

  @Override
  public void execute(ApiRequest req, ApiResponse resp) {
    resp.setStatus(SC_NO_CONTENT);
  }
}
