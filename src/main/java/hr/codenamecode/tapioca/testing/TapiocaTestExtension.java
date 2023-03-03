package hr.codenamecode.tapioca.testing;

import hr.codenamecode.tapioca.Api;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback;

public class TapiocaTestExtension
    implements TestInstancePostProcessor, TestInstancePreDestroyCallback {

  private final Random portGenerator = new Random();
  private final Map<Object, Tomcat> tomcats = new ConcurrentHashMap<>();

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

    Tomcat tomcat = new Tomcat();
    
    Path baseDir = new File("").toPath().toAbsolutePath().resolve("target");
    tomcat.setBaseDir(baseDir.toString());

    Connector connector = tomcat.getConnector();
    connector.setPort(acquirePort());
    tomcat.setConnector(connector);

    tomcats.put(testInstance, tomcat);
    tomcat.setHostname(tapiocaTest.hostname());

    Context tomcatContext = tomcat.addContext(tapiocaTest.contextPath(), null);

    for (Class<? extends Api> listener : tapiocaTest.listeners()) {
      tomcatContext.addApplicationListener(listener.getName());
    }

    URI baseUri =
        URI.create(
            "http://"
                + tapiocaTest.hostname()
                + ":"
                + connector.getPort()
                + tapiocaTest.contextPath());

    setDeclaredField(testInstance, tapiocaTest.tapiocaBaseURIFieldName(), baseUri);
    setDeclaredField(
        testInstance,
        tapiocaTest.tapiocaSimpleHttptClientFieldName(),
        new SimpleHttpClient(baseUri));

    tomcat.start();
  }

  @Override
  public void preDestroyTestInstance(ExtensionContext context) throws Exception {
    Object testInstance = context.getTestInstance().get();
    Tomcat tomcat = tomcats.get(testInstance);
    tomcat.stop();
  }

  private int acquirePort() {
    int port = portGenerator.nextInt(30000) + 20000;
    boolean anyMatch =
        tomcats.values().stream().anyMatch(tomcat -> tomcat.getConnector().getPort() == port);
    if (anyMatch) {
      acquirePort();
    }
    return port;
  }
}
