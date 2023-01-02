package hr.garnet.gapi.internal;

import hr.garnet.gapi.WebMethod;
import java.util.Objects;

/** @author vedransmid@gmail.com */
public class WebMethodHolder {
  private Class<? extends WebMethod> webMethodClass;
  private WebMethod webMethodImpl;

  private WebMethodHolder() {}

  public WebMethodHolder(Class<? extends WebMethod> webMethodClass) {
    this.webMethodClass = webMethodClass;
  }

  public WebMethodHolder(WebMethod webMethodImpl) {
    this.webMethodImpl = webMethodImpl;
  }

  public boolean containsImplementation() {
    return Objects.nonNull(webMethodImpl);
  }

  public WebMethod getWebMethodImpl() {
    return webMethodImpl;
  }

  public Class<? extends WebMethod> getWebMethodClass() {
    return webMethodClass;
  }
}
