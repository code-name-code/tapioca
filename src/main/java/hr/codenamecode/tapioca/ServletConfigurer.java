package hr.codenamecode.tapioca;

import hr.codenamecode.tapioca.RequestHandler;
import hr.codenamecode.tapioca.internal.RequestHandlerHolder;
import jakarta.servlet.MultipartConfigElement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author vedransmid@gmail.com
 */
public class ServletConfigurer {

  private final Map<String, RequestHandlerHolder> postMapping = new ConcurrentHashMap<>();
  private final Map<String, RequestHandlerHolder> putMapping = new ConcurrentHashMap<>();
  private final Map<String, RequestHandlerHolder> getMapping = new ConcurrentHashMap<>();
  private final Map<String, RequestHandlerHolder> deleteMapping = new ConcurrentHashMap<>();
  private final Map<String, RequestHandlerHolder> traceMapping = new ConcurrentHashMap<>();
  private final Map<String, RequestHandlerHolder> headMapping = new ConcurrentHashMap<>();
  private final Map<String, RequestHandlerHolder> optionsMapping = new ConcurrentHashMap<>();

  private final Map<String, String> initParameters = new HashMap<>();

  private String[] urlPatterns;
  private boolean asyncSupported;
  private MultipartConfigElement multipartConfig;

  public void get(String path, Class<? extends RequestHandler> method) {
    getMapping.put(stripSlashes(path), new RequestHandlerHolder(method));
  }

  public void get(Class<? extends RequestHandler> method) {
    getMapping.put("", new RequestHandlerHolder(method));
  }

  public void get(String path, RequestHandler method) {
    getMapping.put(stripSlashes(path), new RequestHandlerHolder(method));
  }

  public void get(RequestHandler method) {
    getMapping.put("", new RequestHandlerHolder(method));
  }

  public void post(String path, Class<? extends RequestHandler> method) {
    postMapping.put(stripSlashes(path), new RequestHandlerHolder(method));
  }

  public void post(Class<? extends RequestHandler> method) {
    postMapping.put("", new RequestHandlerHolder(method));
  }

  public void post(String path, RequestHandler method) {
    postMapping.put(stripSlashes(path), new RequestHandlerHolder(method));
  }

  public void post(RequestHandler method) {
    postMapping.put("", new RequestHandlerHolder(method));
  }

  public void put(String path, Class<? extends RequestHandler> method) {
    putMapping.put(stripSlashes(path), new RequestHandlerHolder(method));
  }

  public void put(Class<? extends RequestHandler> method) {
    putMapping.put("", new RequestHandlerHolder(method));
  }

  public void put(String path, RequestHandler method) {
    putMapping.put(stripSlashes(path), new RequestHandlerHolder(method));
  }

  public void put(RequestHandler method) {
    putMapping.put("", new RequestHandlerHolder(method));
  }

  public void delete(String path, Class<? extends RequestHandler> method) {
    deleteMapping.put(stripSlashes(path), new RequestHandlerHolder(method));
  }

  public void delete(Class<? extends RequestHandler> method) {
    deleteMapping.put("", new RequestHandlerHolder(method));
  }

  public void delete(String path, RequestHandler method) {
    deleteMapping.put(stripSlashes(path), new RequestHandlerHolder(method));
  }

  public void delete(RequestHandler method) {
    deleteMapping.put("", new RequestHandlerHolder(method));
  }

  public void trace(String path, Class<? extends RequestHandler> method) {
    traceMapping.put(stripSlashes(path), new RequestHandlerHolder(method));
  }

  public void trace(Class<? extends RequestHandler> method) {
    traceMapping.put("", new RequestHandlerHolder(method));
  }

  public void trace(String path, RequestHandler method) {
    traceMapping.put(stripSlashes(path), new RequestHandlerHolder(method));
  }

  public void trace(RequestHandler method) {
    traceMapping.put("", new RequestHandlerHolder(method));
  }

  public void head(String path, Class<? extends RequestHandler> method) {
    headMapping.put(stripSlashes(path), new RequestHandlerHolder(method));
  }

  public void head(Class<? extends RequestHandler> method) {
    headMapping.put("", new RequestHandlerHolder(method));
  }

  public void head(String path, RequestHandler method) {
    headMapping.put(stripSlashes(path), new RequestHandlerHolder(method));
  }

  public void head(RequestHandler method) {
    headMapping.put("", new RequestHandlerHolder(method));
  }

  public void options(String path, Class<? extends RequestHandler> method) {
    optionsMapping.put(stripSlashes(path), new RequestHandlerHolder(method));
  }

  public void options(Class<? extends RequestHandler> method) {
    optionsMapping.put("", new RequestHandlerHolder(method));
  }

  public void options(String path, RequestHandler method) {
    optionsMapping.put(stripSlashes(path), new RequestHandlerHolder(method));
  }

  public void options(RequestHandler method) {
    optionsMapping.put("", new RequestHandlerHolder(method));
  }

  public Map<String, RequestHandlerHolder> getGetMapping() {
    return getMapping;
  }

  public Map<String, RequestHandlerHolder> getPostMapping() {
    return postMapping;
  }

  public Map<String, RequestHandlerHolder> getPutMapping() {
    return putMapping;
  }

  public Map<String, RequestHandlerHolder> getDeleteMapping() {
    return deleteMapping;
  }

  public Map<String, RequestHandlerHolder> getTraceMapping() {
    return traceMapping;
  }

  public Map<String, RequestHandlerHolder> getHeadMapping() {
    return headMapping;
  }

  public Map<String, RequestHandlerHolder> getOptionsMapping() {
    return optionsMapping;
  }

  public String[] getUrlPatterns() {
    return this.urlPatterns;
  }

  public boolean isAsyncSupported() {
    return asyncSupported;
  }

  public MultipartConfigElement getMultipartConfig() {
    return multipartConfig;
  }

  public void setUrlPatterns(String... urlPatterns) {
    this.urlPatterns = urlPatterns;
  }

  public void setAsyncSupported(boolean supportAsync) {
    this.asyncSupported = supportAsync;
  }

  public void setMultipartConfig(String location) {
    this.multipartConfig = new MultipartConfigElement(location);
  }

  public void setMultipartConfig(
      String location, long maxFileSize, long maxRequestSize, int fileSizeThreshold) {
    this.multipartConfig =
        new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
  }

  public void setInitParameter(String name, String value) {
    initParameters.put(name, value);
  }

  public Map<String, String> getInitParameters() {
    return initParameters;
  }

  private String stripSlashes(String path) {
    int start = path.startsWith("/") ? 1 : 0;
    if (path.length() == 1) {
      return path.substring(start);
    }
    int end = path.endsWith("/") ? path.length() - 1 : path.length();
    return path.substring(start, end);
  }
}
