package hr.garnet.gapi;

import hr.garnet.gapi.internal.ApiFilterDef;
import hr.garnet.gapi.internal.ApiServlet;
import hr.garnet.gapi.internal.ApiServletConfigurer;
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
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/** @author vedransmid@gmail.com */
public abstract class Api implements ServletContextListener {

  private final List<ApiServletConfigurer> servlets = new ArrayList<>();
  private final Set<ApiFilterDef> filters = new LinkedHashSet<>();

  private Function<Class<? extends ApiCommand>, ApiCommand> commandProvider;
  private BiFunction<String, Class<?>, ?> jsonReader;
  private Function<Object, String> jsonWriter;
  private ApiExceptionHandler exceptionHandler;
  private final Map<String, Object> contextObjects = new HashMap<>();
  private final Map<String, String> initParameters = new HashMap<>();

  protected abstract void configure();

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

  protected void setInitParameter(String name, String value) {
    initParameters.putIfAbsent(name, value);
  }

  protected void filter(Filter filter, String... urlPatterns) {
    filter(filter, false, Map.of(), urlPatterns);
  }

  protected void filter(Filter filter, boolean isAsyncSupported, String... urlPatterns) {
    filter(filter, isAsyncSupported, Map.of(), urlPatterns);
  }

  protected void filter(Filter filter, Map<String, String> initParameters, String... urlPatterns) {
    filter(filter, false, initParameters, urlPatterns);
  }

  protected void filter(
      Filter filter,
      boolean isAsyncSupported,
      Map<String, String> initParameters,
      String... urlPatterns) {
    filters.add(new ApiFilterDef(filter, isAsyncSupported, initParameters, urlPatterns));
  }

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

    initParameters.forEach(sc::setInitParameter);
    contextObjects.forEach(sc::setAttribute);
  }

  private void addFilters(ServletContext sc) {
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

  private void addServlets(ServletContext sc) {
    servlets.forEach(
        apiConfigurer -> {
          ServletRegistration.Dynamic registration;
          ApiServlet apiServlet = new ApiServlet(apiConfigurer);
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
