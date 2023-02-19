package hr.codenamecode.tapioca.cars;

import hr.codenamecode.tapioca.MediaTypeHandler;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class JsonMediaTypeHandler implements MediaTypeHandler {

  private final Jsonb jsonb = JsonbBuilder.create();

  @Override
  public String getMediaType() {
    return "application/json";
  }

  @Override
  public <T> T from(String input, Class<T> type) {
    return jsonb.fromJson(input, type);
  }

  @Override
  public String to(Object object) {
    return jsonb.toJson(object);
  }
}
