package hr.codenamecode.tapioca.cars.handlers;

import hr.codenamecode.tapioca.Response;
import hr.codenamecode.tapioca.RequestHandler;
import hr.codenamecode.tapioca.Request;

public class Trace implements RequestHandler {

  @Override
  public void handle(Request req, Response resp) {
    resp.setHeader("X-Method", "Trace");
  }
}
