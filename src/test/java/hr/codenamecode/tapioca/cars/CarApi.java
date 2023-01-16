package hr.codenamecode.tapioca.cars;

import hr.codenamecode.tapioca.Api;
import hr.codenamecode.tapioca.Bindings;
import hr.codenamecode.tapioca.cars.requesthandler.CreateCar;
import hr.codenamecode.tapioca.cars.requesthandler.DeleteCarByBrand;
import hr.codenamecode.tapioca.cars.requesthandler.GetAllCars;
import hr.codenamecode.tapioca.cars.requesthandler.GetCarByBrand;
import hr.codenamecode.tapioca.cars.requesthandler.ThrowEx;
import hr.codenamecode.tapioca.cars.requesthandler.UpdateCar;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class CarApi extends Api {

  Jsonb jsonb = JsonbBuilder.create();

  @Override
  protected void configure() {
    setRequestHandlerFactory(
        aClass -> {
          try {
            return aClass.getDeclaredConstructor().newInstance();
          } catch (InstantiationException
              | IllegalAccessException
              | NoSuchMethodException
              | InvocationTargetException e) {
            return null;
          }
        });

    setJsonReader(jsonb::fromJson);
    setJsonWriter(jsonb::toJson);

    setExceptionHandler(
        (e, req, resp) -> {
          try {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(e.getMessage());
          } catch (IOException ignored) {

          }
        });

    bind("key", "ok");

    filter(
        (request, response, chain) -> {
          ((HttpServletResponse) response).setHeader("X-Served-By", "Tapioca");
          chain.doFilter(request, response);
        },
        "/*");

    servlet(
        api -> {
          api.get(
              "contextObject",
              (req, resp) ->
                  resp.send(200, "text/plain", Bindings.<String>lookup("key").getBytes()));
          api.get(
              "inlineImpl", (req, resp) -> resp.send(200, "text/plain", "inlineImpl".getBytes()));
          api.get("throwex", ThrowEx.class);
        },
        "/exts/*");

    servlet(
        api -> {
          api.get(GetAllCars.class);
          api.get("(?<brand>\\w+)", GetCarByBrand.class);
          api.post(CreateCar.class);
          api.delete("(?<brand>\\w+)", DeleteCarByBrand.class);
          api.put(UpdateCar.class);
        },
        "/cars/*");
  }
}
