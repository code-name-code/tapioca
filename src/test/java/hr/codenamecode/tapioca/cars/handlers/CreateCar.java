package hr.codenamecode.tapioca.cars.handlers;

import hr.codenamecode.tapioca.MediaType;
import hr.codenamecode.tapioca.RequestHandler;
import hr.codenamecode.tapioca.Response;
import hr.codenamecode.tapioca.cars.Car;
import hr.codenamecode.tapioca.Request;
import hr.codenamecode.tapioca.cars.Storage;
import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import java.io.IOException;

public class CreateCar implements RequestHandler {

  @Override
  public void handle(Request req, Response resp) throws IOException {
    Car car = req.getBody(Car.class);
    Storage.CARS.add(car);

    resp.setContentType(MediaType.TEXT_PLAIN);
    resp.getWriter().write("Created new %s".formatted(car.brand));
    resp.setStatus(SC_CREATED);
  }
}
