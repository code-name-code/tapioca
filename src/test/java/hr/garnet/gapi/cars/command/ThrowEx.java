package hr.garnet.gapi.cars.command;

import hr.garnet.gapi.ApiCommand;
import hr.garnet.gapi.ApiRequest;
import hr.garnet.gapi.ApiResponse;

public class ThrowEx implements ApiCommand {

  @Override
  public void execute(ApiRequest req, ApiResponse resp) {
    throw new IllegalArgumentException("Illegal argument");
  }
}
