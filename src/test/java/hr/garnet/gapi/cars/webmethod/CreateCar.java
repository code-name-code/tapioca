package hr.garnet.gapi.cars.webmethod;

import hr.garnet.gapi.WebMethod;
import hr.garnet.gapi.Request;
import hr.garnet.gapi.Response;
import hr.garnet.gapi.ApiTest;
import hr.garnet.gapi.cars.Car;

public class CreateCar implements WebMethod {

  @Override
  public void invoke(Request req, Response resp) {
    Car car = req.json(Car.class);
    ApiTest.CARS.add(car);
    resp.send(201, "text/plain", "Created new %s".formatted(car.brand).getBytes());
  }
}
