package hr.codenamecode.tapioca.cars.handlers;

import hr.codenamecode.tapioca.MediaType;
import hr.codenamecode.tapioca.Request;
import hr.codenamecode.tapioca.RequestHandler;
import hr.codenamecode.tapioca.Response;
import static hr.codenamecode.tapioca.Response.ContentDispositionType.ATTACHMENT;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import java.io.FileInputStream;

public class DownloadCarCatalog implements RequestHandler {

  @Override
  public void handle(Request req, Response resp) throws Exception {
    String catalogPath = req.getParameter("path");

    resp.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    resp.setContentDisposition("downloaded_catalog.txt", ATTACHMENT);
    resp.setBody(new FileInputStream(catalogPath));
    resp.setStatus(SC_OK);
  }
}
