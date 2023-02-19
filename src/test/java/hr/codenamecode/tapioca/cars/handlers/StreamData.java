package hr.codenamecode.tapioca.cars.handlers;

import hr.codenamecode.tapioca.MediaType;
import hr.codenamecode.tapioca.Request;
import hr.codenamecode.tapioca.RequestHandler;
import hr.codenamecode.tapioca.Response;
import java.io.ByteArrayInputStream;

public class StreamData implements RequestHandler {

  @Override
  public void handle(Request req, Response resp) throws Exception {
    ByteArrayInputStream stream = new ByteArrayInputStream("data".getBytes());
    
    resp.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    resp.setBody(stream);
    resp.setStatus(200);
  }
}
