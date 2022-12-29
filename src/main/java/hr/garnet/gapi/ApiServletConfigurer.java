package hr.garnet.gapi;

import jakarta.servlet.MultipartConfigElement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** @author vedransmid@gmail.com */
public class ApiServletConfigurer {

  private final Map<String, ApiCommandHolder> postMapping = new ConcurrentHashMap<>();
  private final Map<String, ApiCommandHolder> putMapping = new ConcurrentHashMap<>();
  private final Map<String, ApiCommandHolder> getMapping = new ConcurrentHashMap<>();
  private final Map<String, ApiCommandHolder> deleteMapping = new ConcurrentHashMap<>();
  private final Map<String, ApiCommandHolder> traceMapping = new ConcurrentHashMap<>();
  private final Map<String, ApiCommandHolder> headMapping = new ConcurrentHashMap<>();
  private final Map<String, ApiCommandHolder> optionsMapping = new ConcurrentHashMap<>();

  private String[] urlPatterns;
  private boolean asyncSupported;
  private MultipartConfigElement multipartConfig;
  private Map<String, String> initParameters = new HashMap<>();

  public void get(String path, Class<? extends ApiCommand> command) {
    getMapping.put(stripSlashes(path), new ApiCommandHolder(command));
  }

  public void get(String path, ApiCommand command) {
    getMapping.put(stripSlashes(path), new ApiCommandHolder(command));
  }

  public void post(String path, Class<? extends ApiCommand> command) {
    postMapping.put(stripSlashes(path), new ApiCommandHolder(command));
  }

  public void post(String path, ApiCommand command) {
    postMapping.put(stripSlashes(path), new ApiCommandHolder(command));
  }

  public void put(String path, Class<? extends ApiCommand> command) {
    putMapping.put(stripSlashes(path), new ApiCommandHolder(command));
  }

  public void put(String path, ApiCommand command) {
    putMapping.put(stripSlashes(path), new ApiCommandHolder(command));
  }

  public void delete(String path, Class<? extends ApiCommand> command) {
    deleteMapping.put(stripSlashes(path), new ApiCommandHolder(command));
  }

  public void delete(String path, ApiCommand command) {
    deleteMapping.put(stripSlashes(path), new ApiCommandHolder(command));
  }

  public void trace(String path, Class<? extends ApiCommand> command) {
    traceMapping.put(stripSlashes(path), new ApiCommandHolder(command));
  }

  public void trace(String path, ApiCommand command) {
    traceMapping.put(stripSlashes(path), new ApiCommandHolder(command));
  }

  public void head(String path, Class<? extends ApiCommand> command) {
    headMapping.put(stripSlashes(path), new ApiCommandHolder(command));
  }

  public void head(String path, ApiCommand command) {
    headMapping.put(stripSlashes(path), new ApiCommandHolder(command));
  }

  public void options(String path, Class<? extends ApiCommand> command) {
    optionsMapping.put(stripSlashes(path), new ApiCommandHolder(command));
  }

  public void options(String path, ApiCommand command) {
    optionsMapping.put(stripSlashes(path), new ApiCommandHolder(command));
  }

  public Map<String, ApiCommandHolder> getGetMapping() {
    return getMapping;
  }

  public Map<String, ApiCommandHolder> getPostMapping() {
    return postMapping;
  }

  public Map<String, ApiCommandHolder> getPutMapping() {
    return putMapping;
  }

  public Map<String, ApiCommandHolder> getDeleteMapping() {
    return deleteMapping;
  }

  public Map<String, ApiCommandHolder> getTraceMapping() {
    return traceMapping;
  }

  public Map<String, ApiCommandHolder> getHeadMapping() {
    return headMapping;
  }

  public Map<String, ApiCommandHolder> getOptionsMapping() {
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
