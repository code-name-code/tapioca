package hr.garnet.web.gapi;

import jakarta.servlet.ServletContext;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ApiSCBindings {

  public static final String SC_JSON_READER = "jsonReader";
  public static final String SC_JSON_WRITER = "jsonWriter";
  public static final String SC_COMMAND_PROVIDER = "commandProvider";

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
}
