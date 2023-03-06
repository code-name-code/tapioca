package hr.codenamecode.tapioca.cars;

import hr.codenamecode.tapioca.Api;
import hr.codenamecode.tapioca.Bindings;
import hr.codenamecode.tapioca.MediaType;
import hr.codenamecode.tapioca.cars.handlers.CreateCar;
import hr.codenamecode.tapioca.cars.handlers.DeleteCarByBrand;
import hr.codenamecode.tapioca.cars.handlers.DownloadCarCatalog;
import hr.codenamecode.tapioca.cars.handlers.GetAllCars;
import hr.codenamecode.tapioca.cars.handlers.GetCarByBrand;
import hr.codenamecode.tapioca.cars.handlers.StreamData;
import hr.codenamecode.tapioca.cars.handlers.ThrowEx;
import hr.codenamecode.tapioca.cars.handlers.UpdateCar;
import jakarta.servlet.http.HttpServletResponse;

public class CarApi extends Api {

  @Override
  protected void configure() {

    registerBodyHandler(new JsonBodyHandler());

    bind("key", "ok");

    filter(
        (request, response, chain) -> {
          ((HttpServletResponse) response).setHeader("X-Served-By", "Tapioca");
          chain.doFilter(request, response);
        },
        "/*");

    serve(
        api -> {
          api.get(
              "contextObject",
              (req, resp) -> {
                resp.setContentType(MediaType.TEXT_PLAIN);
                resp.getWriter().write(Bindings.<String>lookup("key"));
                resp.setStatus(HttpServletResponse.SC_OK);
              });
          api.get(
              "inlineImpl",
              (req, resp) -> {
                resp.setContentType(MediaType.TEXT_PLAIN);
                resp.getWriter().write("inlineImpl");
                resp.setStatus(HttpServletResponse.SC_OK);
              });
          api.get("throwex", ThrowEx.class);
        },
        "/exts/*");

    serve(
        api -> {
          api.get(GetAllCars.class);
          api.get("download", DownloadCarCatalog.class);
          api.get("stream", StreamData.class);
          api.get("(?<brand>\\w+)", GetCarByBrand.class);
          api.post(CreateCar.class);
          api.delete("(?<brand>\\w+)", DeleteCarByBrand.class);
          api.put(UpdateCar.class);
        },
        "/cars",
        "/cars/*");
  }
}
