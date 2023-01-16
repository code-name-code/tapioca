package hr.codenamecode.tapioca.cars.requesthandler;

import static jakarta.servlet.http.HttpServletResponse.SC_NO_CONTENT;

import hr.codenamecode.tapioca.Response;
import hr.codenamecode.tapioca.RequestHandler;
import hr.codenamecode.tapioca.Request;

public class UpdateCar implements RequestHandler {

  @Override
  public void handle(Request req, Response resp) {
    resp.setStatus(SC_NO_CONTENT);
  }
}
