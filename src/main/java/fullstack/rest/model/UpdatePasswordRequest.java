package fullstack.rest.model;

public class UpdatePasswordRequest {
    private String newPassword;
    private String repeatNewPassword;

    public UpdatePasswordRequest() {}

    public UpdatePasswordRequest(String newPassword, String repeatNewPassword) {
        this.newPassword = newPassword;
        this.repeatNewPassword = repeatNewPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRepeatNewPassword() {
        return repeatNewPassword;
    }

    public void setRepeatNewPassword(String repeatNewPassword) {
        this.repeatNewPassword = repeatNewPassword;
    }
}
