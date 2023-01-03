package hr.codenamecode.tapioca.cars.webmethod;

import static jakarta.servlet.http.HttpServletResponse.SC_NO_CONTENT;

import hr.codenamecode.tapioca.Response;
import hr.codenamecode.tapioca.WebMethod;
import hr.codenamecode.tapioca.Request;

public class UpdateCar implements WebMethod {

  @Override
  public void invoke(Request req, Response resp) {
    resp.setStatus(SC_NO_CONTENT);
  }
}
