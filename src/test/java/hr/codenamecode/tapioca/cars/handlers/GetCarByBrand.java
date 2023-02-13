package hr.codenamecode.tapioca.cars.handlers;

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import hr.codenamecode.tapioca.RequestHandler;
import hr.codenamecode.tapioca.Response;
import hr.codenamecode.tapioca.Request;
import hr.codenamecode.tapioca.cars.Storage;
import java.util.Optional;

public class GetCarByBrand implements RequestHandler {

  @Override
  public void handle(Request req, Response resp) {
    Optional<String> brandParam = req.getPathParam("brand");
    if (brandParam.isPresent()) {
      Storage.CARS.stream()
          .filter(car -> car.brand.equalsIgnoreCase(brandParam.get()))
          .findFirst()
          .ifPresentOrElse(car -> resp.json(200, car), () -> resp.setStatus(SC_NOT_FOUND));
    } else {
      resp.setStatus(SC_NOT_FOUND);
    }
  }
}
