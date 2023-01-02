# GAPI (Garnet API)

## Prerequisites

* JDK 17+

## Usage example

```java
// This example shows usage without using DI
// For more detailed examples check ApiTest

import hr.garnet.gapi.Bindings;
import hr.garnet.gapi.WebMethod;

public class HelloWorld implements WebMethod {

  @Override
  public void execute(ApiRequest req, ApiResponse resp) {
    resp.text(200, "Hello World!");
  }
}

@WebListener
public class GarnetApi extends Api {

  // You could use injection here
  Jsonb jsonb = JsonbBuilder.create();

  // DI is also available here

  @Override
  protected void configure() {
    // How web methods are created
    // e.g. CDI - setWebMethodProvider(webMethodClass -> CDI.current().select(webMethodClass).get());
    // Instead CDI you could also use Guice, HK2 etc.
    setWebMethodProvider(webMethodClass -> {
      try {
        // Classic way of creating class instances on the fly
        return webMethodClass.getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        // handle exceptions
      }
    });

    // Set global exception handler
    setExceptionHandler(
        (e, req, resp) -> {
          try {
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(e.getMessage());
          } catch (IOException ignored) {

          }
        });

    // In case you wish to use json, you can provide reader and writer to benefit
    // from ApiResponse in-built helper methods
    setJsonReader((s, aClass) -> jsonb.fromJson(s, aClass));
    setJsonWriter(jsonb::toJson);

    bind("key", "Bind anything to the servlet context as attribute");
    setInitParameter("name", "Set servlet context initial parameter");

    // Define filter for /*
    filter(
        (servletRequest, servletResponse, filterChain) -> {
          // Print bound servlet context key value
          System.out.println(Bindings.<String>lookup("key"));
          filterChain.doFilter(servletRequest, servletResponse);
        },
        "/*");

    // Define servlet (you can have as many as you like)
    serve(api -> {
      // api.setUrlPatterns("/*"); you can set url patterns here
      // or in serve method (takes precedence)

      api.post("", HelloWorld.class); // define post method

      // Define get method by providing immediate implementation
      api.get("inlineImpl", (req, resp) -> resp.send(200, "text/plain", "inline impl.".getBytes()));
    }, "/*");
  }
}
```