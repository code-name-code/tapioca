# GAPI (Garnet API)

## Prerequisites

* JDK 17+

## Usage example

```java
// This example shows usage without using DI
// For more detailed examples check ApiTest

import hr.garnet.gapi.ApiBindings;

public class HelloWorldCommand implements ApiCommand {

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
        // How commands are created
        // e.g. CDI - setCommandProvider(commandClass -> command -> CDI.current().select(commandClass).get());
        // Instead CDI you could also use Guice, HK2 etc.
        setCommandProvider(commandClass -> {
            try {
                // Classic way of creating class instances on the fly
                return commandClass.getDeclaredConstructor().newInstance();
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
                    System.out.println(ApiBindings.<String>lookup("key"));
                    filterChain.doFilter(servletRequest, servletResponse);
                },
                "/*");

        // Define servlet (you can have as many as you like)
        serve(api -> {
            // api.setUrlPatterns("/*"); you can set url patterns here
            // or in serve method (takes precedence)
            
            api.post("", HelloWorldCommand.class); // define post method

            // Define get method by providing immediate implementation
            api.get("inlineImpl", (req, resp) -> resp.send(200, "text/plain", "inline impl.".getBytes()));
        }, "/*");
    }
}
```