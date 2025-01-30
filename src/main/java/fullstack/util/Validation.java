package fullstack.util;

import fullstack.rest.model.CreateUserRequest;
import fullstack.rest.model.LoginRequest;
import fullstack.service.exception.UserCreationException;

public class Validation {

    public static void validateUserRequest(CreateUserRequest request) throws UserCreationException {
        if (request == null) {
            throw new UserCreationException(Messages.CONTACT_REQUIRED);
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new UserCreationException(Messages.NAME_REQUIRED);
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new UserCreationException(Messages.PASSWORD_REQUIRED);
        }

        if (request.getPasswordConfirmation() == null || request.getPasswordConfirmation().trim().isEmpty()) {
            throw new UserCreationException(Messages.PASSWORD_CONFIRMATION_REQUIRED);
        }

        if (!request.getPasswordConfirmation().equals(request.getPassword())) {
            throw new UserCreationException(Messages.PASSWORD_MISMATCH);
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (!ContactValidator.isValidEmail(request.getEmail())) {
                throw new UserCreationException(Messages.INVALID_EMAIL);
            }
        } else if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            request.setPhone(ContactValidator.formatPhone(request.getPhone()));
            if (!ContactValidator.isValidPhone(request.getPhone())) {
                throw new UserCreationException(Messages.INVALID_PHONE);
            }
        } else {
            throw new UserCreationException(Messages.CONTACT_REQUIRED);
        }
    }

    public static void validateLoginRequest(LoginRequest request) throws IllegalArgumentException {
        if (request == null) {
            throw new IllegalArgumentException(Messages.CONTACT_REQUIRED);
        }
        if (request.getEmailOrPhone() == null || request.getEmailOrPhone().isEmpty()) {
            throw new IllegalArgumentException(Messages.CONTACT_REQUIRED);
        }

        if (request.getEmailOrPhone().matches("\\d+")) {
            request.setEmailOrPhone(ContactValidator.formatPhone(request.getEmailOrPhone()));
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException(Messages.PASSWORD_REQUIRED);
        }
    }

    public static void validateEmail(String email) throws IllegalArgumentException {
        if (!ContactValidator.isValidEmail(email)) {
            throw new IllegalArgumentException(Messages.INVALID_EMAIL);
        }
    }
    public static void validatePhone(String phone) throws IllegalArgumentException {
        phone = ContactValidator.formatPhone(phone);
        if (!ContactValidator.isValidPhone(phone)) {
            throw new IllegalArgumentException(Messages.INVALID_PHONE);
        }
    }
}
