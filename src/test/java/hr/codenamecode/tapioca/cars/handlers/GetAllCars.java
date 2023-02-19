package hr.codenamecode.tapioca.cars.handlers;

import hr.codenamecode.tapioca.MediaType;
import static hr.codenamecode.tapioca.MediaType.APPLICATION_JSON;
import hr.codenamecode.tapioca.RequestHandler;
import hr.codenamecode.tapioca.Response;
import hr.codenamecode.tapioca.Request;
import hr.codenamecode.tapioca.cars.Storage;
import jakarta.servlet.http.HttpServletResponse;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

public class GetAllCars implements RequestHandler {

  @Override
  public void handle(Request req, Response resp) {
    
    resp.setContentType(APPLICATION_JSON);
    resp.setBody(Storage.CARS);
    resp.setStatus(SC_OK);
  }
}
