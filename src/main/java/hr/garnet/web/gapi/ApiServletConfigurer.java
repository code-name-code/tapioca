package hr.garnet.web.gapi;

import jakarta.servlet.MultipartConfigElement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApiServletConfigurer {

  private final Map<String, Class<? extends ApiCommand>> postMapping = new ConcurrentHashMap<>();
  private final Map<String, Class<? extends ApiCommand>> putMapping = new ConcurrentHashMap<>();
  private final Map<String, Class<? extends ApiCommand>> getMapping = new ConcurrentHashMap<>();
  private final Map<String, Class<? extends ApiCommand>> deleteMapping = new ConcurrentHashMap<>();
  private final Map<String, Class<? extends ApiCommand>> traceMapping = new ConcurrentHashMap<>();
  private final Map<String, Class<? extends ApiCommand>> headMapping = new ConcurrentHashMap<>();
  private final Map<String, Class<? extends ApiCommand>> optionsMapping = new ConcurrentHashMap<>();

  private String[] urlPatterns;
  private boolean asyncSupported;
  private MultipartConfigElement multipartConfig;

  public void get(String path, Class<? extends ApiCommand> command) {
    getMapping.put(stripSlashes(path), command);
  }

  public void post(String path, Class<? extends ApiCommand> command) {
    postMapping.put(stripSlashes(path), command);
  }

  public void put(String path, Class<? extends ApiCommand> command) {
    putMapping.put(stripSlashes(path), command);
  }

  public void delete(String path, Class<? extends ApiCommand> command) {
    deleteMapping.put(stripSlashes(path), command);
  }

  public void trace(String path, Class<? extends ApiCommand> command) {
    traceMapping.put(stripSlashes(path), command);
  }

  public void head(String path, Class<? extends ApiCommand> command) {
    headMapping.put(stripSlashes(path), command);
  }

  public void options(String path, Class<? extends ApiCommand> command) {
    optionsMapping.put(stripSlashes(path), command);
  }

  public Map<String, Class<? extends ApiCommand>> getGetMapping() {
    return getMapping;
  }

  public Map<String, Class<? extends ApiCommand>> getPostMapping() {
    return postMapping;
  }

  public Map<String, Class<? extends ApiCommand>> getPutMapping() {
    return putMapping;
  }

  public Map<String, Class<? extends ApiCommand>> getDeleteMapping() {
    return deleteMapping;
  }

  public Map<String, Class<? extends ApiCommand>> getTraceMapping() {
    return traceMapping;
  }

  public Map<String, Class<? extends ApiCommand>> getHeadMapping() {
    return headMapping;
  }

  public Map<String, Class<? extends ApiCommand>> getOptionsMapping() {
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

  private String stripSlashes(String path) {
    int start = path.startsWith("/") ? 1 : 0;
    if (path.length() == 1) {
      return path.substring(start);
    }
    int end = path.endsWith("/") ? path.length() - 1 : path.length();
    return path.substring(start, end);
  }
}
