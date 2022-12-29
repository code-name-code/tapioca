package hr.garnet.gapi;

import jakarta.servlet.Filter;
import java.util.Map;

public record ApiFilterDef(
    Filter filter,
    boolean asyncSupported,
    Map<String, String> initParameters,
    String... urlPatterns) {

  public ApiFilterDef(Filter filter, String... urlPatterns) {
    this(filter, false, null, urlPatterns);
  }

  public ApiFilterDef(Filter filter, boolean isAsyncSupported, String... urlPatterns) {
    this(filter, isAsyncSupported, null, urlPatterns);
  }

  public ApiFilterDef(Filter filter, Map<String, String> initParameters, String... urlPatterns) {
    this(filter, false, initParameters, urlPatterns);
  }
}
