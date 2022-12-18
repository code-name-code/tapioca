# GAPI (Garnet API)

## Prerequisites
* JDK 19+

## Usage example
```java
// This example shows usage without using DI
// For more detailed examples check ApiTest

public class HelloWorldCommand implements ApiCommand {
    @Override
    public void execute(ApiRequest req, ApiResponse resp) {
        resp.text(200, "Hello World!");
    }
}

@WebListener
public class GarnetApi extends Api {
    
  Jsonb jsonb = JsonbBuilder.create();
  
  // DI is also available here

  @Override
  protected void configure() {
    // How commands are created (You can configure any DI provider here instead)
    setCommandProvider(commandClass -> {
        try {
            return commandClass.getDeclaredConstructor().newInstance();
        } catch (Exception e){
            // handle exceptions
        }
    });
    
    // In case you wish to use json, you can provide reader and writer to benefit
    // from ApiResponse in-built helper methods
    setJsonReader((s, aClass) -> jsonb.fromJson(s, aClass));
    setJsonWriter(jsonb::toJson);

    // Define filter for /*
    filter(
      (servletRequest, servletResponse, filterChain) -> 
        filterChain.doFilter(servletRequest, servletResponse),
          "/*");
    
    // Define servlet (you can have as many as you like)
    serve(api -> {
      // api.setUrlPatterns("/*"); you can set url patterns here
      // or in serve method (this one overrides setUrlPatterns method)
      api.post("", HelloWorldCommand.class); // define post method
    }, "/*");
  }
}
```