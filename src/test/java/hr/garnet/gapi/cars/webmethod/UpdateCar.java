package hr.garnet.gapi.cars.webmethod;

import static jakarta.servlet.http.HttpServletResponse.SC_NO_CONTENT;

import hr.garnet.gapi.WebMethod;
import hr.garnet.gapi.Request;
import hr.garnet.gapi.Response;

public class UpdateCar implements WebMethod {

  @Override
  public void invoke(Request req, Response resp) {
    resp.setStatus(SC_NO_CONTENT);
  }
}
