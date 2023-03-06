package hr.codenamecode.tapioca;

import hr.codenamecode.tapioca.internal.OctetStreamBodyHandler;
import hr.codenamecode.tapioca.internal.FilterDef;
import hr.codenamecode.tapioca.internal.Processor;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRegistration;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Extend this class to start configuring/implementing your API. In essence, this is just a {@link
 * ServletContextListener} with some sugar on top to make implementing {@link
 * jakarta.servlet.http.HttpServlet} fun and easy. It hides a lot of boilerplate from the developer,
 * and also adds a few powerful features such as mapping using regular expressions. It feels like
 * JAX-RS, but it is a lot simpler and thinner. Tapioca depends only on Jakarta Servlet
 * specification, but you can use any other Jakarta EE specification just like you would used it
 * normally when developing Jakarta EE applications/services.
 *
 * <p>You can register it just by using {@link jakarta.servlet.annotation.WebListener} annotation,
 * or you can add it programmatic (see tests).
 *
 * <pre><code>
 * &#64;WebListener
 * public class CodenamecodeApi extends Api {
 *
 *   &#64;Override
 *   protected void configure() {
 *
 *     // Classic way of creating class instance on the fly - no dependency injection
 *     setRequestHandlerFactory(requestHandlerClass -> {
 *       try {
 *         return requestHandlerClass.getDeclaredConstructor().newInstance();
 *       } catch (Exception e) {
 *         // handle exceptions
 *       }
 *     });
 *
 *     // CDI way of crating class instances on the fly (you could also use Guice, HK2 etc.)
 *     setRequestHandlerFactory(requestHandlerClass -> CDI.current().select(requestHandlerClass).get());
 *   }
 * }
 * </code>
 * </pre>
 *
 * @author vedransmid@gmail.com
 */
public abstract class Api implements ServletContextListener {

  private final List<ServletConfigurer> servlets;
  private final Set<FilterDef> filters;
  private final Map<String, Object> contextObjects;
  private final Map<String, String> initParameters;

  private Function<Class<? extends RequestHandler>, RequestHandler> requestHandlerFactory;
  private final Map<String, BodyHandler> bodyHandlers = new ConcurrentHashMap<>();
  private ExceptionHandler exceptionHandler;

  public Api() {
    this.contextObjects = new HashMap<>();
    this.initParameters = new HashMap<>();
    this.servlets = new ArrayList<>();
    this.filters = new LinkedHashSet<>();

    this.requestHandlerFactory = Defaults.DEFAULT_REQUEST_HANDLER_FACTORY;
    this.exceptionHandler = Defaults.DEFAULT_EXCEPTION_HANDLER;

    this.bodyHandlers.put(MediaType.APPLICATION_OCTET_STREAM, new OctetStreamBodyHandler());
  }

  /** Implement this method to configure servlets, filters etc. */
  protected abstract void configure();

  /**
   * Set request handler provider. If no request handler factory is set, a default one is used,
   * {@link Defaults#DEFAULT_REQUEST_HANDLER_FACTORY}
   *
   * @param requestHandlerFactory Function which provides request handler instance to be executed by
   *     the {@link Processor}
   * @throws NullPointerException
   */
  protected void setRequestHandlerFactory(
      Function<Class<? extends RequestHandler>, RequestHandler> requestHandlerFactory)
      throws NullPointerException {
    this.requestHandlerFactory = Objects.requireNonNull(requestHandlerFactory);
  }

  /**
   * Register new body handler. Body handlers are used when reading from request body or writing to
   * response body.
   *
   * @param bodyHandler Body handler implementation
   */
  protected void registerBodyHandler(BodyHandler bodyHandler) {
    this.bodyHandlers.put(bodyHandler.getMediaType(), bodyHandler);
  }

  /**
   * Set exception handler. If no exception handler is set, a default one is used, {@link
   * Defaults#DEFAULT_EXCEPTION_HANDLER}. If you set a custom exception handler make sure you handle
   * all cases ({@link ApiException} especially).
   *
   * @param exceptionHandler {@link ExceptionHandler}
   * @throws NullPointerException
   */
  protected void setExceptionHandler(ExceptionHandler exceptionHandler)
      throws NullPointerException {
    this.exceptionHandler = Objects.requireNonNull(exceptionHandler);
  }

  /**
   * Set {@link ServletContext} attribute.
   *
   * @param <T>
   * @param name Attribute name
   * @param o Attribute value
   * @return Bound object
   */
  protected <T> T bind(String name, T o) {
    contextObjects.putIfAbsent(name, o);
    return o;
  }

  /**
   * Set {@link ServletContext} init parameter.
   *
   * @param name Init parameter name
   * @param value Init parameter value
   */
  protected void setInitParameter(String name, String value) {
    initParameters.putIfAbsent(name, value);
  }

  /**
   * Set {@link Filter}.
   *
   * @param filter {@link Filter} implementation
   * @param urlPatterns URL patterns this filter will be filtering
   */
  protected void filter(Filter filter, String... urlPatterns) {
    filter(filter, false, Map.of(), urlPatterns);
  }

  /**
   * Set {@link Filter}.
   *
   * @param filter {@link Filter} implementation
   * @param isAsyncSupported Enable async support for filter
   * @param urlPatterns URL patterns this filter will be filtering
   */
  protected void filter(Filter filter, boolean isAsyncSupported, String... urlPatterns) {
    filter(filter, isAsyncSupported, Map.of(), urlPatterns);
  }

  /**
   * Set {@link Filter}.
   *
   * @param filter {@link Filter} implementation
   * @param initParameters Map af filter init parameters
   * @param urlPatterns URL patterns this filter will be filtering
   */
  protected void filter(Filter filter, Map<String, String> initParameters, String... urlPatterns) {
    filter(filter, false, initParameters, urlPatterns);
  }

  /**
   * Set {@link Filter}.
   *
   * @param filter {@link Filter} implementation
   * @param isAsyncSupported Enable async support for filter
   * @param initParameters Map af filter init parameters
   * @param urlPatterns URL patterns this filter will be filtering
   */
  protected void filter(
      Filter filter,
      boolean isAsyncSupported,
      Map<String, String> initParameters,
      String... urlPatterns) {
    filters.add(new FilterDef(filter, isAsyncSupported, initParameters, urlPatterns));
  }

  /**
   * Configure servlet for the configured URL patterns. Underlying servlet implementation used is
   * {@link Processor}. Each call to this method will create a new instance of {@link
   * Processor}.Configuring servlet using this method offers same functionalities as {@link
   * jakarta.servlet.ServletRegistration.Dynamic} through {@link ServletConfigurer}.
   *
   * @param apiConfigurer {@link ServletConfigurer}
   * @param urlPatterns URL patterns this servlet will be serving
   */
  @SuppressWarnings("ClassEscapesDefinedScope")
  protected void serve(ServletConfigurer apiConfigurer, String... urlPatterns) {
    apiConfigurer.setUrlPatterns(urlPatterns);
    servlets.add(apiConfigurer);
  }

  /**
   * Configure servlet for the configured URL patterns. Underlying servlet implementation used is
   * {@link Processor}. Each call to this method will create a new instance of {@link Processor}.
   * Configuring servlet using this method offers same functionalities as {@link
   * jakarta.servlet.ServletRegistration.Dynamic} through {@link ServletConfigurer}. This method
   * offers functional alternative to {@link Api#serve(ServletConfigurer, String[])} method through
   * {@link Consumer}. This way you provide inline servlet configuration without the need for
   * creating a separate class.
   *
   * @param apiConfigurerConsumer {@link ServletConfigurer} consumer
   * @param urlPatterns URL patterns this servlet will be serving
   */
  @SuppressWarnings("ClassEscapesDefinedScope")
  protected void serve(Consumer<ServletConfigurer> apiConfigurerConsumer, String... urlPatterns) {
    ServletConfigurer apiConfigurer = new ServletConfigurer();
    apiConfigurer.setUrlPatterns(urlPatterns);
    apiConfigurerConsumer.accept(apiConfigurer);
    servlets.add(apiConfigurer);
  }

  /**
   * This method should only be called by the container and never by the developer. Tapioca hooks to
   * this method to initialize itself.
   *
   * @param sce {@link ServletContextEvent} containing the ServletContext that is being initialized
   */
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    Bindings.setServletContext(sce.getServletContext());

    configure();

    setServletContext(sce.getServletContext());
    registerFilters(sce.getServletContext());
    registerServlets(sce.getServletContext());
  }

  /**
   * Set {@link ServletContext} attributes and init parameters.
   *
   * @param sc {@link ServletContext}
   */
  private void setServletContext(ServletContext sc) {
    sc.setAttribute(Bindings.SC_BODY_HANDLERS, bodyHandlers);
    sc.setAttribute(Bindings.SC_REQUEST_HANDLER_FACTORY, requestHandlerFactory);
    sc.setAttribute(Bindings.SC_EXCEPTION_HANDLER, exceptionHandler);

    initParameters.forEach(sc::setInitParameter);
    contextObjects.forEach(sc::setAttribute);
  }

  /**
   * Register filters.
   *
   * @param sc {@link ServletContext}
   */
  private void registerFilters(ServletContext sc) {
    filters.forEach(
        (filterDef) -> {
          FilterRegistration.Dynamic registration;
          registration = sc.addFilter(filterDef.filter().toString(), filterDef.filter());
          registration.addMappingForUrlPatterns(
              EnumSet.of(DispatcherType.REQUEST), true, filterDef.urlPatterns());
          registration.setAsyncSupported(filterDef.asyncSupported());
          filterDef.initParameters().forEach(registration::setInitParameter);
        });
  }

  /**
   * Register servlets.
   *
   * @param sc {@link ServletContext}
   */
  private void registerServlets(ServletContext sc) {
    servlets.forEach(
        apiConfigurer -> {
          ServletRegistration.Dynamic registration;
          Processor apiServlet = new Processor(apiConfigurer);
          registration = sc.addServlet(apiServlet.toString(), apiServlet);
          registration.addMapping(apiConfigurer.getUrlPatterns());
          registration.setAsyncSupported(apiConfigurer.isAsyncSupported());

          if (apiConfigurer.getMultipartConfig() != null) {
            registration.setMultipartConfig(apiConfigurer.getMultipartConfig());
          }

          apiConfigurer.getInitParameters().forEach(registration::setInitParameter);

          // TODO Implement remaining programmatic options (e.g. security)
        });
  }
}
