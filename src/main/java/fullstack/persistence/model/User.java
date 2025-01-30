package fullstack.persistence.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(columnDefinition = "CHAR(36)")
    private String id;
    private String email;
    private String password;
    private String name;
    private String surname;
    private String phone;
    @Column(name = "email_verified")
    private Boolean emailVerified;
    @Column(name = "phone_verified")
    private Boolean phoneVerified;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    @Column(name = "token_email")
    private String tokenEmail;
    @Column(name = "token_phone")
    private String tokenPhone;
    @Column(name = "token_password")
    private String tokenPassword;

    public User() {
        this.id = UUID.randomUUID().toString();
        this.emailVerified = false;
        this.phoneVerified = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getTokenEmail() {
        return tokenEmail;
    }

    public void setTokenEmail(String tokenEmail) {
        this.tokenEmail = tokenEmail;
    }

    public String getTokenPhone() {
        return tokenPhone;
    }

    public void setTokenPhone(String tokenPhone) {
        this.tokenPhone = tokenPhone;
    }

    public String getTokenPassword() {
        return tokenPassword;
    }

    public void setTokenPassword(String tokenPassword) {
        this.tokenPassword = tokenPassword;
    }
}
