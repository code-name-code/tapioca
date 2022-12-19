package hr.garnet.gapi;

public interface ApiCommand {

  void execute(ApiRequest req, ApiResponse resp);
}
