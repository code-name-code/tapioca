package hr.codenamecode.tapioca.cars.webmethod;

import hr.codenamecode.tapioca.Response;
import hr.codenamecode.tapioca.WebMethod;
import hr.codenamecode.tapioca.Request;

public class Trace implements WebMethod {

  @Override
  public void invoke(Request req, Response resp) {
    resp.setHeader("X-Method", "Trace");
  }
}
