package hr.garnet.gapi.cars.command;

import hr.garnet.gapi.ApiCommand;
import hr.garnet.gapi.ApiRequest;
import hr.garnet.gapi.ApiResponse;
import hr.garnet.gapi.ApiTest;
import hr.garnet.gapi.cars.Car;

public class CreateCar implements ApiCommand {

  @Override
  public void execute(ApiRequest req, ApiResponse resp) {
    Car car = req.json(Car.class);
    ApiTest.CARS.add(car);
    resp.send(201, "text/plain", "Created new %s".formatted(car.brand).getBytes());
  }
}
