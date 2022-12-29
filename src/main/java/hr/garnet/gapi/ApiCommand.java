package hr.garnet.gapi;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author vedransmid@gmail.com
 */
public interface ApiCommand {

  default void setServletConfig(ServletConfig servletConfig) {}

  /**
   * Method which will be executed by the {@link ApiServlet} if incoming request matches mapping for
   * this command.
   *
   * @param req This wrapper around {@link HttpServletRequest} with additional methods
   * @param resp This wrapper around {@link HttpServletResponse} with additional methods
   */
  void execute(ApiRequest req, ApiResponse resp);
}
