package hr.codenamecode.tapioca;

import hr.codenamecode.tapioca.internal.Processor;
import hr.codenamecode.tapioca.internal.ServletConfigurer;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Consumer;

/**
 * Tapioca library is centered around request handler execution by the underlying {@link
 * jakarta.servlet.http.HttpServlet} implementation. In Tapioca case, that implementation is
 * provided by {@link Processor}. Basically, your API will consist of many instances of this class
 * and each will be mapped to a URI pattern. Mapping is done in classes extending {@link Api} by
 * calling {@link Api#serve(ServletConfigurer, String...)} or {@link Api#serve(Consumer, String...)}
 * method.
 *
 * @author vedransmid@gmail.com
 */
public interface RequestHandler {

  /**
   * Implement this method if you wish to gain access to the init parameters of configured {@link
   * Processor} otherwise, you can ignore it.
   *
   * @param servletConfig {@link ServletConfig}
   */
  default void setServletConfig(ServletConfig servletConfig) {}

  /**
   * Method which will be executed by the {@link Processor} if incoming request matches mapping for
   * this request handler. For more details on how matching is done, see {@link Processor}.
   *
   * @param req Wrapper around {@link HttpServletRequest} with additional, convenient methods
   * @param resp Wrapper around {@link HttpServletResponse} with additional, convenient methods
   */
  void handle(Request req, Response resp);
}
