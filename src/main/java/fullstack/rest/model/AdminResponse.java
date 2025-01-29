package fullstack.rest.model;

public class AdminResponse extends UserResponse {
    private String role;

    public AdminResponse(String name, String surname, String email, String phone, String role) {
        super(name, surname, email, phone);
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
