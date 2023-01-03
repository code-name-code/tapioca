package hr.codenamecode.tapioca.internal;

import hr.codenamecode.tapioca.WebMethod;
import jakarta.servlet.MultipartConfigElement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** @author vedransmid@gmail.com */
public class ServletConfigurer {

  private final Map<String, WebMethodHolder> postMapping = new ConcurrentHashMap<>();
  private final Map<String, WebMethodHolder> putMapping = new ConcurrentHashMap<>();
  private final Map<String, WebMethodHolder> getMapping = new ConcurrentHashMap<>();
  private final Map<String, WebMethodHolder> deleteMapping = new ConcurrentHashMap<>();
  private final Map<String, WebMethodHolder> traceMapping = new ConcurrentHashMap<>();
  private final Map<String, WebMethodHolder> headMapping = new ConcurrentHashMap<>();
  private final Map<String, WebMethodHolder> optionsMapping = new ConcurrentHashMap<>();

  private final Map<String, String> initParameters = new HashMap<>();

  private String[] urlPatterns;
  private boolean asyncSupported;
  private MultipartConfigElement multipartConfig;

  public void get(String path, Class<? extends WebMethod> method) {
    getMapping.put(stripSlashes(path), new WebMethodHolder(method));
  }

  public void get(Class<? extends WebMethod> method) {
    getMapping.put("", new WebMethodHolder(method));
  }

  public void get(String path, WebMethod method) {
    getMapping.put(stripSlashes(path), new WebMethodHolder(method));
  }

  public void get(WebMethod method) {
    getMapping.put("", new WebMethodHolder(method));
  }

  public void post(String path, Class<? extends WebMethod> method) {
    postMapping.put(stripSlashes(path), new WebMethodHolder(method));
  }

  public void post(Class<? extends WebMethod> method) {
    postMapping.put("", new WebMethodHolder(method));
  }

  public void post(String path, WebMethod method) {
    postMapping.put(stripSlashes(path), new WebMethodHolder(method));
  }

  public void post(WebMethod method) {
    postMapping.put("", new WebMethodHolder(method));
  }

  public void put(String path, Class<? extends WebMethod> method) {
    putMapping.put(stripSlashes(path), new WebMethodHolder(method));
  }

  public void put(Class<? extends WebMethod> method) {
    putMapping.put("", new WebMethodHolder(method));
  }

  public void put(String path, WebMethod method) {
    putMapping.put(stripSlashes(path), new WebMethodHolder(method));
  }

  public void put(WebMethod method) {
    putMapping.put("", new WebMethodHolder(method));
  }

  public void delete(String path, Class<? extends WebMethod> method) {
    deleteMapping.put(stripSlashes(path), new WebMethodHolder(method));
  }

  public void delete(Class<? extends WebMethod> method) {
    deleteMapping.put("", new WebMethodHolder(method));
  }

  public void delete(String path, WebMethod method) {
    deleteMapping.put(stripSlashes(path), new WebMethodHolder(method));
  }

  public void delete(WebMethod method) {
    deleteMapping.put("", new WebMethodHolder(method));
  }

  public void trace(String path, Class<? extends WebMethod> method) {
    traceMapping.put(stripSlashes(path), new WebMethodHolder(method));
  }

  public void trace(Class<? extends WebMethod> method) {
    traceMapping.put("", new WebMethodHolder(method));
  }

  public void trace(String path, WebMethod method) {
    traceMapping.put(stripSlashes(path), new WebMethodHolder(method));
  }

  public void trace(WebMethod method) {
    traceMapping.put("", new WebMethodHolder(method));
  }

  public void head(String path, Class<? extends WebMethod> method) {
    headMapping.put(stripSlashes(path), new WebMethodHolder(method));
  }

  public void head(Class<? extends WebMethod> method) {
    headMapping.put("", new WebMethodHolder(method));
  }

  public void head(String path, WebMethod method) {
    headMapping.put(stripSlashes(path), new WebMethodHolder(method));
  }

  public void head(WebMethod method) {
    headMapping.put("", new WebMethodHolder(method));
  }

  public void options(String path, Class<? extends WebMethod> method) {
    optionsMapping.put(stripSlashes(path), new WebMethodHolder(method));
  }

  public void options(Class<? extends WebMethod> method) {
    optionsMapping.put("", new WebMethodHolder(method));
  }

  public void options(String path, WebMethod method) {
    optionsMapping.put(stripSlashes(path), new WebMethodHolder(method));
  }

  public void options(WebMethod method) {
    optionsMapping.put("", new WebMethodHolder(method));
  }

  public Map<String, WebMethodHolder> getGetMapping() {
    return getMapping;
  }

  public Map<String, WebMethodHolder> getPostMapping() {
    return postMapping;
  }

  public Map<String, WebMethodHolder> getPutMapping() {
    return putMapping;
  }

  public Map<String, WebMethodHolder> getDeleteMapping() {
    return deleteMapping;
  }

  public Map<String, WebMethodHolder> getTraceMapping() {
    return traceMapping;
  }

  public Map<String, WebMethodHolder> getHeadMapping() {
    return headMapping;
  }

  public Map<String, WebMethodHolder> getOptionsMapping() {
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
