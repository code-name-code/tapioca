package hr.garnet.gapi.cars.command;

import hr.garnet.gapi.ApiCommand;
import hr.garnet.gapi.ApiRequest;
import hr.garnet.gapi.ApiResponse;
import hr.garnet.gapi.ApiTest;

public class GetAllCars implements ApiCommand {

  @Override
  public void execute(ApiRequest req, ApiResponse resp) {
    resp.json(200, ApiTest.CARS);
  }
}
