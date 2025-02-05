package fullstack.service;

import fullstack.persistence.model.SanitationUtil;
import fullstack.persistence.repository.UserRepository;
import fullstack.persistence.repository.UserSessionRepository;
import fullstack.persistence.model.User;
import fullstack.persistence.model.UserSession;
import fullstack.persistence.repository.UserRepository;
import fullstack.persistence.repository.UserSessionRepository;
import fullstack.rest.model.CreateUserRequest;
import fullstack.rest.model.LoginRequest;
import fullstack.rest.model.LoginResponse;
import fullstack.service.exception.*;
import fullstack.util.ContactValidator;
import fullstack.util.Messages;
import fullstack.util.Validation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static fullstack.util.Messages.*;

@ApplicationScoped
public class AuthenticationService {

    @Inject
    HashCalculator hashCalculator;
    @Inject
    UserRepository userRepository;
    @Inject
    UserSessionRepository userSessionRepository;
    @Inject
    NotificationService notificationService;

    @Transactional
    public User register(CreateUserRequest request) throws ContactException, UserCreationException {
        request.setName(SanitationUtil.sanitize(request.getName()));
        request.setSurname(SanitationUtil.sanitize(request.getSurname()));
        request.setEmail(SanitationUtil.sanitize(request.getEmail()));
        request.setPhone(SanitationUtil.sanitize(request.getPhone()));
        request.setPassword(SanitationUtil.sanitize(request.getPassword()));

        Validation.validateUserRequest(request);
        checkIfEmailOrPhoneExists(request);

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setPassword(hashPassword(request.getPassword()));
        user.setEmail(request.getEmail());
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            user.setPhone(request.getPhone());
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            user.setTokenEmail(UUID.randomUUID().toString());
            String verificationLink = "http://localhost:8080/auth/verifyEmail?token=" + user.getTokenEmail() + "&contact=" + user.getEmail();
            notificationService.sendVerificationEmail(user, verificationLink);
        } else if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            user.setTokenPhone(generateOtp());
            notificationService.sendVerificationSms(user, user.getTokenPhone());
        }

        userRepository.persist(user);
        return user;
    }

    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public String hashPassword(String password) {
        return hashCalculator.calculateHash(password);
    }

    private void checkIfEmailOrPhoneExists(CreateUserRequest request) throws ContactException {
        String email = request.getEmail();
        String phone = request.getPhone();

        if (email != null && !email.trim().isEmpty()) {
            boolean emailInUse = userRepository.findByEmail(email).isPresent();
            if (emailInUse) {
                throw new ContactException(Messages.EMAIL_ALREADY_USED);
            }
        }

        if (phone != null && !phone.trim().isEmpty()) {
            phone = ContactValidator.formatPhone(phone);
            boolean phoneInUse = userRepository.findByPhone(phone).isPresent();
            if (phoneInUse) {
                throw new ContactException(Messages.PHONE_ALREADY_USED);
            }
        }
    }

    @Transactional
    public void verifyEmail(String token, String email) throws TokenException, UserCreationException {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UserCreationException(USER_NOT_FOUND);
        }

        User user = userOpt.get();
        if (user.getTokenEmail() == null || !user.getTokenEmail().equals(token)) {
            throw new TokenException(INVALID_TOKEN);
        }

        user.setEmailVerified(true);
        user.setTokenEmail(null);
        userRepository.persist(user);
    }

    @Transactional
    public void verifyPhone(String token, String phone) throws TokenException, UserCreationException {
        Optional<User> optionalUser = userRepository.findByPhone(phone);
        User user = optionalUser.orElseThrow(() -> new UserCreationException(USER_NOT_FOUND));

        if (user.getTokenPhone() == null || !user.getTokenPhone().equals(token)) {
            throw new TokenException(INVALID_TOKEN);
        }

        user.setPhoneVerified(true);
        user.setTokenPhone(null);
        userRepository.persist(user);
    }

    @Transactional
    public LoginResponse authenticate(LoginRequest request, Boolean rememberMe) throws UserNotFoundException, WrongPasswordException {


        Validation.validateLoginRequest(request);

        Optional<User> optionalUser = userRepository.findByEmailOrPhone(request.getEmailOrPhone());
        User user = optionalUser.orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        if (!user.getEmailVerified() && !user.getPhoneVerified()) {
            throw new ContactException(UNVERIFIED_CONTACT);
        }

        String hashedProvidedPassword = hashPassword(request.getPassword());
        if (!verifyPassword(user.getPassword(), hashedProvidedPassword)) {
            throw new WrongPasswordException(WRONG_PASSWORD);
        }

        Optional<UserSession> existingSession = userSessionRepository.findByUserId(user.getId());
        existingSession.ifPresent(userSessionRepository::delete);

        String sessionId;
        if (Boolean.TRUE.equals(rememberMe)) {
            sessionId = createSessionLong(user);
        } else {
            sessionId = createSession(user);
        }

        return new LoginResponse(user.getName(), sessionId, LOGIN_SUCCESS);
    }

    private String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        UserSession userSession = new UserSession();
        userSession.setSessionId(sessionId);
        userSession.setUser(user);
        userSession.setExpiresAt(LocalDateTime.now().plusHours(24));
        userSessionRepository.persist(userSession);
        return sessionId;
    }

    private String createSessionLong(User user) {
        String sessionId = UUID.randomUUID().toString();
        UserSession userSession = new UserSession();
        userSession.setSessionId(sessionId);
        userSession.setUser(user);
        userSession.setExpiresAt(LocalDateTime.now().plusDays(30));
        userSessionRepository.persist(userSession);
        return sessionId;
    }

    private void checkIfSessionExists(String userId) throws SessionAlreadyExistsException {
        Optional<UserSession> existingSession = userSessionRepository.findByUserId(userId);
        if (existingSession.isPresent()) {
            throw new SessionAlreadyExistsException(SESSION_ALREADY_EXISTS);
        }
    }

    public boolean verifyPassword(String actualPassword, String providedPassword) {
        return actualPassword != null && actualPassword.equals(providedPassword);
    }

    @Transactional
    public void logout(String sessionId) throws UserSessionNotFoundException {
        Optional<UserSession> optionalSession = userSessionRepository.findBySessionId(sessionId);
        if (optionalSession.isEmpty()) {
            throw new UserSessionNotFoundException(SESSION_NOT_FOUND);
        }
        userSessionRepository.delete(optionalSession.get());
    }
}
