package hr.garnet.gapi;

public interface ApiExceptionHandler {
    void handleException(Exception e, ApiRequest req, ApiResponse resp);
}
