package hr.codenamecode.tapioca.testing;

import hr.codenamecode.tapioca.Api;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Path;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback;

public class TapiocaTomcatTestExtension
    implements TestInstancePostProcessor, TestInstancePreDestroyCallback {

  private Tomcat tomcat;

  private void setDeclaredField(Object testInstance, String name, Object value)
      throws IllegalArgumentException, IllegalAccessException {
    try {
      Field base = testInstance.getClass().getDeclaredField(name);
      if (base != null) {
        base.setAccessible(true);
        base.set(testInstance, value);
      }
    } catch (NoSuchFieldException ignored) {

    }
  }

  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext context)
      throws Exception {
    TapiocaTest tapiocaTest = testInstance.getClass().getAnnotation(TapiocaTest.class);

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
    setDeclaredField(testInstance, tapiocaTest.tapiocaBaseURIFieldName(), baseUri);
    setDeclaredField(
        testInstance,
        tapiocaTest.tapiocaSimpleHttptClientFieldName(),
        new SimpleHttpClient(baseUri));

    tomcat.start();
  }

  @Override
  public void preDestroyTestInstance(ExtensionContext context) throws Exception {
    if (tomcat != null) {
      tomcat.stop();
    }
  }
}
