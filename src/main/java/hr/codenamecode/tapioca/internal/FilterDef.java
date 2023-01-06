package hr.codenamecode.tapioca.internal;

import jakarta.servlet.Filter;
import java.util.Map;

/**
 * @author vedransmid@gmail.com
 */
public record FilterDef(
    Filter filter,
    boolean asyncSupported,
    Map<String, String> initParameters,
    String... urlPatterns) {

  public FilterDef(Filter filter, String... urlPatterns) {
    this(filter, false, null, urlPatterns);
  }

  public FilterDef(Filter filter, boolean isAsyncSupported, String... urlPatterns) {
    this(filter, isAsyncSupported, null, urlPatterns);
  }

  public FilterDef(Filter filter, Map<String, String> initParameters, String... urlPatterns) {
    this(filter, false, initParameters, urlPatterns);
  }
}
