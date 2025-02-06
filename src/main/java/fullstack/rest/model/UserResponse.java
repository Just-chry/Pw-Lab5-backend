package fullstack.rest.model;

import fullstack.persistence.model.Role;

public class UserResponse {
    private String name;
    private String surname;
    private String email;
    private String phone;
    private Role role;

    public UserResponse(String name, String surname, String email, String phone, Role role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public UserResponse(Role userRole) {
        this.role = userRole;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
