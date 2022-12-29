package hr.garnet.gapi;

import jakarta.servlet.ServletConfig;

/** @author vedransmid@gmail.com */
public interface ApiCommand {

  default void setServletConfig(ServletConfig servletConfig) {}

  void execute(ApiRequest req, ApiResponse resp);
}
