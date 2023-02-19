package hr.codenamecode.tapioca.internal;

import hr.codenamecode.tapioca.BodyHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OctetStreamBodyHandler implements BodyHandler {

  @Override
  public String getMediaType() {
    return "application/octet-stream";
  }

  @Override
  public <T> T read(InputStream source, Class<T> type) throws IOException {
    return (T) source;
  }

  @Override
  public void write(Object object, OutputStream sink) throws IOException {
    InputStream is = (InputStream) object;
    int c;
    while ((c = is.read()) != -1) {
      sink.write(c);
      sink.flush();
    }
  }
}
