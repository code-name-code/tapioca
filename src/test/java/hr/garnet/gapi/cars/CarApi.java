package hr.garnet.gapi.cars;

import hr.garnet.gapi.Api;
import hr.garnet.gapi.Bindings;
import hr.garnet.gapi.cars.webmethod.CreateCar;
import hr.garnet.gapi.cars.webmethod.DeleteCarByBrand;
import hr.garnet.gapi.cars.webmethod.GetAllCars;
import hr.garnet.gapi.cars.webmethod.GetCarByBrand;
import hr.garnet.gapi.cars.webmethod.Head;
import hr.garnet.gapi.cars.webmethod.ThrowEx;
import hr.garnet.gapi.cars.webmethod.UpdateCar;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class CarApi extends Api {

  Jsonb jsonb = JsonbBuilder.create();

  @Override
  protected void configure() {
    setWebMethodProvider(
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
          ((HttpServletResponse) response).setHeader("X-Served-By", "GAPI");
          chain.doFilter(request, response);
        },
        "/*");

    serve(
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

    serve(
        api -> {
          api.get("", GetAllCars.class);
          api.get("(?<brand>\\w+)", GetCarByBrand.class);
          api.post("", CreateCar.class);
          api.delete("(?<brand>\\w+)", DeleteCarByBrand.class);
          api.put("", UpdateCar.class);
          api.head("", Head.class);
        },
        "/cars/*");
  }
}
