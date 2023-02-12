package hr.codenamecode.tapioca.cars.requesthandler;

import hr.codenamecode.tapioca.Request;
import hr.codenamecode.tapioca.RequestHandler;
import hr.codenamecode.tapioca.Response;
import java.io.FileInputStream;

public class DownloadCarCatalog implements RequestHandler {

  @Override
  public void handle(Request req, Response resp) throws Exception {
    String catalogPath = req.getParameter("path");
    resp.file(new FileInputStream(catalogPath), true, "text/plain", "downloaded_catalog.txt");
  }
}
