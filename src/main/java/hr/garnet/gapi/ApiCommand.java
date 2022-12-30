package hr.garnet.gapi;

import hr.garnet.gapi.internal.ApiServletConfigurer;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Consumer;

/**
 * GAPI library is centered around command execution by the underlying {@link
 * jakarta.servlet.http.HttpServlet} implementation. In GAPI case, that implementation is provided
 * by {@link hr.garnet.gapi.internal.ApiServlet}. Basically, your API will consist of many instances
 * of this class and each will be mapped to a URI pattern. Mapping is done in classes extending
 * {@link Api} by calling {@link Api#serve(ApiServletConfigurer, String...)} or {@link
 * Api#serve(Consumer, String...)} method.
 *
 * @author vedransmid@gmail.com
 */
public interface ApiCommand {

  /**
   * Implement this method if you wish to gain access to the init parameters of configured {@link
   * hr.garnet.gapi.internal.ApiServlet} otherwise, you can ignore it.
   *
   * @param servletConfig {@link ServletConfig}
   */
  default void setServletConfig(ServletConfig servletConfig) {}

  /**
   * Method which will be executed by the {@link hr.garnet.gapi.internal.ApiServlet} if incoming
   * request matches mapping for this command. For more details on how matching is done, see {@link
   * hr.garnet.gapi.internal.ApiServlet}.
   *
   * @param req Wrapper around {@link HttpServletRequest} with additional, convenient methods
   * @param resp Wrapper around {@link HttpServletResponse} with additional, convenient methods
   */
  void execute(ApiRequest req, ApiResponse resp);
}
