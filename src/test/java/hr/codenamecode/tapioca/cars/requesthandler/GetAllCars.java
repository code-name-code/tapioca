package hr.codenamecode.tapioca.cars.requesthandler;

import hr.codenamecode.tapioca.RequestHandler;
import hr.codenamecode.tapioca.Response;
import hr.codenamecode.tapioca.Request;
import hr.codenamecode.tapioca.ApiTest;

public class GetAllCars implements RequestHandler {

  @Override
  public void handle(Request req, Response resp) {
    resp.json(200, ApiTest.CARS);
  }
}
