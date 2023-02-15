package hr.codenamecode.tapioca;

import hr.codenamecode.tapioca.Api;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Path;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TapiocaTomcatTestExtension implements BeforeAllCallback, AfterAllCallback {

  private Tomcat tomcat;

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    if (context.getTestInstance().isPresent()) {
      TapiocaTest tapiocaTest =
          context.getTestInstance().get().getClass().getAnnotation(TapiocaTest.class);

      tomcat = new Tomcat();

      Path baseDir = new File("").toPath().resolve("target");
      tomcat.setBaseDir(baseDir.toString());

      String port = System.getProperty("tomcat.port", String.valueOf(tapiocaTest.port()));
      tomcat.setPort(Integer.parseInt(port));
      tomcat.setHostname(tapiocaTest.hostname());

      tomcat.getConnector();

      Context tomcatContext = tomcat.addContext(tapiocaTest.contextPath(), null);

      for (Class<? extends Api> listener : tapiocaTest.listeners()) {
        tomcatContext.addApplicationListener(listener.getName());
      }

      URI baseUri =
          URI.create("http://" + tapiocaTest.hostname() + ":" + port + tapiocaTest.contextPath());
      setDeclaredField(context, "base", baseUri);
      setDeclaredField(context, "http", new TapiocaHttpTestClient(baseUri));

      tomcat.start();
    }
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    tomcat.stop();
  }

  private void setDeclaredField(ExtensionContext context, String name, Object value)
      throws IllegalArgumentException, IllegalAccessException {
    try {
      Field base = context.getTestInstance().get().getClass().getDeclaredField(name);
      if (base != null) {
        base.setAccessible(true);
        base.set(context.getTestInstance().get(), value);
      }
    } catch (NoSuchFieldException ignored) {
    }
  }
}
