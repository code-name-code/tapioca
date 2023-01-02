package hr.garnet.gapi.cars.webmethod;

import hr.garnet.gapi.WebMethod;
import hr.garnet.gapi.Request;
import hr.garnet.gapi.Response;

public class Head implements WebMethod {

  @Override
  public void invoke(Request req, Response resp) {
    resp.setHeader("X-Method", "Head");
  }
}
