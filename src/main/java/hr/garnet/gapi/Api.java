package hr.garnet.gapi;

import jakarta.servlet.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author vedransmid@gmail.com
 */
public abstract class Api implements ServletContextListener {

  private final List<ApiServletConfigurer> servlets = new ArrayList<>();
  private final Map<Filter, String[]> filters = new LinkedHashMap<>();

  private Function<Class<? extends ApiCommand>, ApiCommand> commandProvider;
  private BiFunction<String, Class<?>, ?> jsonReader;
  private Function<Object, String> jsonWriter;
  private ApiExceptionHandler exceptionHandler;
  private final Map<String, Object> contextObjects = new HashMap<>();

  protected void setCommandProvider(
      Function<Class<? extends ApiCommand>, ApiCommand> commandProvider) {
    this.commandProvider = commandProvider;
  }

  protected void setJsonReader(BiFunction<String, Class<?>, ?> jsonReader) {
    this.jsonReader = jsonReader;
  }

  protected void setJsonWriter(Function<Object, String> jsonWriter) {
    this.jsonWriter = jsonWriter;
  }

  protected void setExceptionHandler(ApiExceptionHandler exceptionHandler) {
    this.exceptionHandler = exceptionHandler;
  }

  protected void bind(String key, Object o) {
    contextObjects.putIfAbsent(key, o);
  }

  protected void filter(Filter filter, String... urlPatterns) {
    filters.put(filter, urlPatterns);
  }

  protected abstract void configure();

  protected void serve(ApiServletConfigurer apiConfigurer, String... urlPatterns) {
    apiConfigurer.setUrlPatterns(urlPatterns);
    servlets.add(apiConfigurer);
  }

  protected void serve(
      Consumer<ApiServletConfigurer> apiConfigurerConsumer, String... urlPatterns) {
    ApiServletConfigurer apiConfigurer = new ApiServletConfigurer();
    apiConfigurer.setUrlPatterns(urlPatterns);
    apiConfigurerConsumer.accept(apiConfigurer);
    servlets.add(apiConfigurer);
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ApiBindings.setServletContext(sce.getServletContext());

    configure();
    addToServletContext(sce.getServletContext());
    addFilters(sce.getServletContext());
    addServlets(sce.getServletContext());
  }

  private void addToServletContext(ServletContext sc) {
    sc.setAttribute(ApiBindings.SC_JSON_READER, jsonReader);
    sc.setAttribute(ApiBindings.SC_JSON_WRITER, jsonWriter);
    sc.setAttribute(ApiBindings.SC_COMMAND_PROVIDER, commandProvider);
    sc.setAttribute(ApiBindings.SC_EXCEPTION_HANDLER, exceptionHandler);

    contextObjects.forEach(sc::setAttribute);
  }

  private void addFilters(ServletContext sc) {
    filters.forEach(
        (filter, mapping) -> {
          FilterRegistration.Dynamic registration;
          registration = sc.addFilter(filter.getClass().getSimpleName(), filter);
          registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, mapping);
        });
  }

  private void addServlets(ServletContext sc) {
    servlets.forEach(
        apiConfigurer -> {
          String servletName =
              apiConfigurer.getClass().getSimpleName() + servlets.indexOf(apiConfigurer);

          ServletRegistration.Dynamic registration;
          registration = sc.addServlet(servletName, new ApiServlet(apiConfigurer));
          registration.addMapping(apiConfigurer.getUrlPatterns());
          registration.setAsyncSupported(apiConfigurer.isAsyncSupported());

          if (apiConfigurer.getMultipartConfig() != null) {
            registration.setMultipartConfig(apiConfigurer.getMultipartConfig());
          }
        });
  }
}
