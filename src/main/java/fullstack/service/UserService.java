package fullstack.service;

import fullstack.persistence.repository.UserRepository;
import fullstack.persistence.repository.UserSessionRepository;
import fullstack.persistence.model.Role;
import fullstack.persistence.model.User;
import fullstack.persistence.model.UserSession;
import fullstack.rest.model.*;
import fullstack.service.exception.*;
import fullstack.util.Messages;
import fullstack.util.Validation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hibernate.SessionException;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static fullstack.util.Messages.*;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;
    @Inject
    NotificationService notificationService;
    @Inject
    UserSessionRepository userSessionRepository;
    @Inject
    HashCalculator hashCalculator;

    @Transactional
    public void deleteUser(String userId) throws UserNotFoundException {
        User user = getUserBySessionId(userId);
        userRepository.delete(user);
    }

    public List<AdminResponse> listUsers(String sessionId) throws AdminAccessException, SessionException {
        if (isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        return userRepository.listAll().stream()
                .map(user -> new AdminResponse(user.getName(), user.getSurname(), user.getEmail(), user.getPhone(), user.getRole().name()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void promoteUserToAdmin(String userId, String sessionId) throws SessionException {
        if (isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        Optional<User> userOpt = userRepository.findUserById(userId);
        if (userOpt.isEmpty()) {
            throw new SessionException(USER_NOT_FOUND);
        }

        User user = userOpt.get();
        user.setRole(Role.ADMIN);
        userRepository.persist(user);
    }


    public boolean isAdmin(String sessionId) throws SessionException {
        Optional<UserSession> session = userSessionRepository.findBySessionId(sessionId);
        if (session.isEmpty()) {
            throw new SessionException(SESSION_NOT_FOUND);
        }
        User user = session.get().getUser();
        return user.getRole() != Role.ADMIN;
    }

    public User getUserBySessionId(String sessionId) throws UserNotFoundException {
        Optional<UserSession> session = userSessionRepository.findBySessionId(sessionId);
        if (session.isEmpty()) {
            throw new UserNotFoundException(SESSION_NOT_FOUND);
        }
        return session.get().getUser();
    }

    public UserResponse getUserResponseById(String userId) throws UserNotFoundException {
        User user = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        return new UserResponse(user.getName(), user.getSurname(), user.getEmail(), user.getPhone());
    }



    @Transactional
    public void updateEmail(String sessionId, ModifyEmailRequest newEmail) throws UserNotFoundException, UserCreationException {
        Validation.validateEmail(newEmail.getEmail());
        checkEmail(newEmail.getEmail());
        User user = getUserBySessionId(sessionId);
        user.setEmail(newEmail.getEmail());
        user.setEmailVerified(false);
        user.setTokenEmail(UUID.randomUUID().toString());
        String verificationLink = "http://localhost:8080/auth/verifyEmail?token=" + user.getTokenEmail() + "&contact=" + user.getEmail();
        notificationService.sendVerificationEmail(user, verificationLink);

        userRepository.persist(user);
    }


    private void checkEmail(String newEmail) throws UserCreationException {
        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new UserCreationException(Messages.EMAIL_ALREADY_USED);
        }
    }


    @Transactional
    public void updatePhone(String sessionId, ModifyPhoneRquest newPhoneRequest) throws UserNotFoundException, UserCreationException {
        String newPhone = newPhoneRequest.getPhone();
        Validation.validatePhone(newPhone);
        checkPhone(newPhone);
        User user = getUserBySessionId(sessionId);
        user.setPhone(newPhone);
        user.setPhoneVerified(false);
        user.setTokenPhone(generateOtp());
        notificationService.sendVerificationSms(user, user.getTokenPhone());

        userRepository.persist(user);
    }


    private void checkPhone(String newPhone) throws UserCreationException {
        if (userRepository.findByPhone(newPhone).isPresent()) {
            throw new UserCreationException(Messages.PHONE_ALREADY_USED);
        }
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @Transactional
    public void updateName(String sessionId, ModifyNameRequest newName) throws UserNotFoundException {
        User user = getUserBySessionId(sessionId);
        user.setName(newName.getName());
        userRepository.persist(user);
    }

    @Transactional
    public void updateSurname(String sessionId, ModifySurnameRequest newSurname) throws UserNotFoundException {
        User user = getUserBySessionId(sessionId);
        user.setSurname(newSurname.getSurname());
        userRepository.persist(user);
    }

    @Transactional
    public void updatePassword(String sessionId, ModifyPasswordRequest newPasswordRequest) throws UserNotFoundException, UserCreationException {
        User user = getUserBySessionId(sessionId);
        String hashedOldPassword = hashCalculator.calculateHash(newPasswordRequest.getOldPassword());

        if (!user.getPassword().equals(hashedOldPassword)) {
            throw new PasswordException(OLD_PASSWORD_NOT_MATCH);
        }

        if (!newPasswordRequest.getNewPassword().equals(newPasswordRequest.getRepeatNewPassword())) {
            throw new PasswordException(NEW_PASSWORD_NOT_MATCH);
        }

        user.setPassword(hashCalculator.calculateHash(newPasswordRequest.getNewPassword()));
        userRepository.persist(user);
    }

    @Transactional
    public void forgottenPassword(String emailOrPhone) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmailOrPhone(emailOrPhone);
        User user = optionalUser.orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        String verificationCode = generateOtp();
        user.setTokenPassword(verificationCode);
        userRepository.persist(user);

        if (emailOrPhone.contains("@")) {
            notificationService.sendPasswordResetEmail(user);
        } else {
            notificationService.sendPasswordResetSms(user);
        }
    }

    @Transactional
    public void verifyCode(String emailOrPhone, String verificationCode) throws TokenException {
        Optional<User> optionalUser = userRepository.findByEmailOrPhone(emailOrPhone);
        User user = optionalUser.orElseThrow(() -> new TokenException("Utente non trovato."));

        if (!user.getTokenPassword().equals(verificationCode)) {
            throw new TokenException("Codice di verifica non valido.");
        }
    }

    @Transactional
    public void updatePasswordWithCode(String emailOrPhone, String newPassword, String repeatNewPassword)
            throws UserNotFoundException, PasswordException {
        Optional<User> optionalUser = userRepository.findByEmailOrPhone(emailOrPhone);
        User user = optionalUser.orElseThrow(() -> new UserNotFoundException("Utente non trovato."));

        if (!newPassword.equals(repeatNewPassword)) {
            throw new PasswordException("Le password non coincidono.");
        }

        user.setPassword(hashCalculator.calculateHash(newPassword));
        user.setTokenPassword(null);
        userRepository.persist(user);
    }
}
