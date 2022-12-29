package hr.garnet.gapi;

/**
 * @author vedransmid@gmail.com
 */
public interface ApiCommand {

  void execute(ApiRequest req, ApiResponse resp);
}
