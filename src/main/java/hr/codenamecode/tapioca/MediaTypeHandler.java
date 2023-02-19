package hr.codenamecode.tapioca;

public interface MediaTypeHandler {
  
  String getMediaType();

  <T> T from(String input, Class<T> type);

  String to(Object object);
}
