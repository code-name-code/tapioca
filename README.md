# tAPIoca

Tapioca (/ˌtæpiˈoʊkə/; Portuguese: [tapiˈɔkɐ]) is a starch extracted from the storage roots of the cassava plant (Manihot esculenta, also known as manioc), a species native to the North and Northeast regions of Brazil, but whose use is now spread throughout South America. It is a perennial shrub adapted to the hot conditions of tropical lowlands. Cassava copes better with poor soils than many other food plants.

Tapioca is a staple food for millions of people in tropical countries. It provides only carbohydrate food value, and is low in protein, vitamins and minerals. In other countries, it is used as a thickening agent in various manufactured foods.

Tapioca is derived from the word tipi'óka, its name in the Tupi language spoken by natives when the Portuguese first arrived in the Northeast Region of Brazil around 1500. This Tupi word is translated as 'sediment' or 'coagulant' and refers to the curd-like starch sediment that is obtained in the extraction process.

source: [Wikipedia](https://en.wikipedia.org/wiki/Tapioca)

## Prerequisites

* JDK 17+

## Usage example

```java
// This example shows usage without using DI
// For more detailed examples check ApiTest

import hr.codenamecode.tapioca.Bindings;
import hr.codenamecode.tapioca.RequestHandler;

public class HelloWorld implements RequestHandler {

    @Override
    public void handle(Request req, Response resp) {
        resp.text(200, "Hello World!");
    }
}

@WebListener
public class CodenamecodeApi extends Api {

    // You could use injection here
    Jsonb jsonb = JsonbBuilder.create();

    // DI is also available here

    @Override
    protected void configure() {
        // How request handlers are created
        // e.g. CDI - setRequestHandlerFactory(requestHandlerClass -> CDI.current().select(requestHandlerClass).get());
        // Instead CDI you could also use Guice, HK2 etc.
        // If no request handler factory is set, a default one is used (see Defaults.DEFAULT_REQUEST_HANDLER_FACTORY)
        setRequestHandlerFactory(requestHandlerClass -> {
            try {
                // Classic way of creating class instances on the fly
                return requestHandlerClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                // handle exceptions
            }
        });

        // Set exception handler
        // If no exception handler is set, a default one is used (see Defaults.DEFAULT_EXCEPTION_HANDLER)
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

        // Define servlet (you can have as many servlets as you like)
        servlet(api -> {
            // api.setUrlPatterns("/*"); you can set url patterns here
            // or in serve method (takes precedence)

            api.post(HelloWorld.class); // define post method, mapped as "" path

            // Define get method by providing immediate implementation
            api.get("inlineImpl", (req, resp) -> resp.send(200, "text/plain", "inline impl.".getBytes()));
        }, "/*");
    }
}
```