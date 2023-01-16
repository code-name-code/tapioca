package hr.codenamecode.tapioca.cars.requesthandler;

import hr.codenamecode.tapioca.RequestHandler;
import hr.codenamecode.tapioca.Response;
import hr.codenamecode.tapioca.cars.Car;
import hr.codenamecode.tapioca.Request;
import hr.codenamecode.tapioca.ApiTest;

public class CreateCar implements RequestHandler {

  @Override
  public void handle(Request req, Response resp) {
    Car car = req.json(Car.class);
    ApiTest.CARS.add(car);
    resp.send(201, "text/plain", "Created new %s".formatted(car.brand).getBytes());
  }
}
