package hr.codenamecode.tapioca.internal;

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

public class TapiocaTestExtension implements BeforeAllCallback, AfterAllCallback {

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

      tomcat.getConnector();

      Context tomcatContext = tomcat.addContext(tapiocaTest.contextPath(), null);

      for (Class<? extends Api> listener : tapiocaTest.listeners()) {
        tomcatContext.addApplicationListener(listener.getName());
      }

      URI baseUri = URI.create("http://localhost:" + port + tapiocaTest.contextPath());
      try {
        Field base = context.getTestInstance().get().getClass().getDeclaredField("base");
        if (base != null) {
          base.setAccessible(true);
          base.set(context.getTestInstance().get(), baseUri);
        }
      } catch (NoSuchFieldException ignored) {
      }

      try {
        Field http = context.getTestInstance().get().getClass().getDeclaredField("http");
        if (http != null) {
          http.setAccessible(true);
          http.set(context.getTestInstance().get(), new Http(baseUri));
        }
      } catch (NoSuchFieldException e) {
      }

      tomcat.start();
    }
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    tomcat.stop();
  }
}
