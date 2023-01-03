package hr.codenamecode.tapioca.cars.webmethod;

import hr.codenamecode.tapioca.Response;
import hr.codenamecode.tapioca.WebMethod;
import hr.codenamecode.tapioca.Request;

public class ThrowEx implements WebMethod {

  @Override
  public void invoke(Request req, Response resp) {
    throw new IllegalArgumentException("Illegal argument");
  }
}
