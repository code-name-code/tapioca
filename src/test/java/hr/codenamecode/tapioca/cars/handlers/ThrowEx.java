package hr.codenamecode.tapioca.cars.handlers;

import hr.codenamecode.tapioca.ApiException;
import hr.codenamecode.tapioca.RequestHandler;
import hr.codenamecode.tapioca.Response;
import hr.codenamecode.tapioca.Request;

public class ThrowEx implements RequestHandler {

  @Override
  public void handle(Request req, Response resp) {
    if (req.getParameter("type").equals("illegal")) {
      throw new IllegalArgumentException("Illegal argument");
    } else if (req.getParameter("type").equals("api")) {
      throw new ApiException(501, "API exception");
    }
  }
}
