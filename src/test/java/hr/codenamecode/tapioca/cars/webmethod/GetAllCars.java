package hr.codenamecode.tapioca.cars.webmethod;

import hr.codenamecode.tapioca.Response;
import hr.codenamecode.tapioca.WebMethod;
import hr.codenamecode.tapioca.Request;
import hr.codenamecode.tapioca.ApiTest;

public class GetAllCars implements WebMethod {

  @Override
  public void invoke(Request req, Response resp) {
    resp.json(200, ApiTest.CARS);
  }
}
