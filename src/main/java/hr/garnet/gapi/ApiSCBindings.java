package hr.garnet.gapi;

import jakarta.servlet.ServletContext;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ApiSCBindings {

  public static final String SC_JSON_READER = "hr.garnet.gapi.json.reader";
  public static final String SC_JSON_WRITER = "hr.garnet.gapi.json.writer";
  public static final String SC_COMMAND_PROVIDER = "hr.garnet.gapi.command.provider";
  public static final String SC_EXCEPTION_HANDLER = "hr.garnet.gapi.exception.handler";

  public static BiFunction<String, Class<?>, ?> getJsonReader(ServletContext sc) {
    return (BiFunction<String, Class<?>, ?>) sc.getAttribute(SC_JSON_READER);
  }

  public static Function<Object, String> getJsonWriter(ServletContext sc) {
    return (Function<Object, String>) sc.getAttribute(SC_JSON_WRITER);
  }

  public static Function<Class<? extends ApiCommand>, ApiCommand> getCommandProvider(
      ServletContext sc) {
    return (Function<Class<? extends ApiCommand>, ApiCommand>) sc.getAttribute(SC_COMMAND_PROVIDER);
  }

  public static Optional<ApiExceptionHandler> getExceptionHandler(ServletContext sc) {
    return Optional.ofNullable((ApiExceptionHandler) sc.getAttribute(SC_EXCEPTION_HANDLER));
  }
}
