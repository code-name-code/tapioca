package hr.garnet.gapi.cars.command;

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import hr.garnet.gapi.ApiCommand;
import hr.garnet.gapi.ApiRequest;
import hr.garnet.gapi.ApiResponse;
import hr.garnet.gapi.ApiTest;
import java.util.Optional;

public class GetCarByBrand implements ApiCommand {

  @Override
  public void execute(ApiRequest req, ApiResponse resp) {
    Optional<String> brandParam = req.getPathParam("brand");
    if (brandParam.isPresent()) {
      ApiTest.CARS.stream()
          .filter(car -> car.brand.equalsIgnoreCase(brandParam.get()))
          .findFirst()
          .ifPresentOrElse(car -> resp.json(200, car), () -> resp.setStatus(SC_NOT_FOUND));
    } else {
      resp.setStatus(SC_NOT_FOUND);
    }
  }
}
