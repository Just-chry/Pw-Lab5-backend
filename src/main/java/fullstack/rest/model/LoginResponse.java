package fullstack.rest.model;

public class LoginResponse {
    private String name;
    private String sessionId;
    private String message;


    public LoginResponse(String name, String sessionId, String message) {
        this.name = name;
        this.sessionId = sessionId;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
