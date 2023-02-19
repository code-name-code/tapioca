package hr.codenamecode.tapioca;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface BodyHandler {

  /**
   * @return Media type handled by this handler. e.g. application/json
   */
  String getMediaType();

  /**
   * Converts source to an instance of class specified by parameter type.
   *
   * @param <T>
   * @param source
   * @param type
   * @return
   * @throws IOException
   */
  <T> T read(InputStream source, Class<T> type) throws IOException;

  /**
   * Writes parameter object to an output stream specified by parameter sink.
   *
   * @param object
   * @param sink
   * @throws IOException
   */
  void write(Object object, OutputStream sink) throws IOException;
}
