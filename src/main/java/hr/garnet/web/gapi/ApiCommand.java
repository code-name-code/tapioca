package hr.garnet.web.gapi;

public interface ApiCommand {

  void execute(ApiRequest req, ApiResponse resp);
}
