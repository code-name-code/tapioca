package hr.garnet.gapi.cars.command;

import hr.garnet.gapi.ApiCommand;
import hr.garnet.gapi.ApiRequest;
import hr.garnet.gapi.ApiResponse;

public class Options implements ApiCommand {

  @Override
  public void execute(ApiRequest req, ApiResponse resp) {
    resp.setHeader("X-Method", "Options");
  }
}
