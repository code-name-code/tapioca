package hr.garnet.gapi.cars.webmethod;

import hr.garnet.gapi.WebMethod;
import hr.garnet.gapi.Request;
import hr.garnet.gapi.Response;
import hr.garnet.gapi.ApiTest;

public class GetAllCars implements WebMethod {

  @Override
  public void invoke(Request req, Response resp) {
    resp.json(200, ApiTest.CARS);
  }
}
