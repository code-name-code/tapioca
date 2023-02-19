package hr.codenamecode.tapioca.cars;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import hr.codenamecode.tapioca.BodyHandler;
import hr.codenamecode.tapioca.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class JsonBodyHandler implements BodyHandler {

  private final Jsonb jsonb = JsonbBuilder.create();

  @Override
  public String getMediaType() {
    return MediaType.APPLICATION_JSON;
  }

  @Override
  public <T> T read(InputStream source, Class<T> type) throws IOException {
    return jsonb.fromJson(source, type);
  }

  @Override
  public void write(Object object, OutputStream sink) throws IOException {
    String json = jsonb.toJson(object);
    sink.write(json.getBytes(StandardCharsets.UTF_8));
  }
}
