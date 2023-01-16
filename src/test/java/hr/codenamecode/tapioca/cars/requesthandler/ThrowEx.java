package hr.codenamecode.tapioca.cars.requesthandler;

import hr.codenamecode.tapioca.RequestHandler;
import hr.codenamecode.tapioca.Response;
import hr.codenamecode.tapioca.Request;

public class ThrowEx implements RequestHandler {

  @Override
  public void handle(Request req, Response resp) {
    throw new IllegalArgumentException("Illegal argument");
  }
}
