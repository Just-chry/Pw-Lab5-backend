package fullstack.service;

import fullstack.persistence.repository.UserRepository;
import fullstack.persistence.repository.UserSessionRepository;
import fullstack.persistence.model.User;
import fullstack.persistence.model.UserSession;
import fullstack.rest.model.CreateUserRequest;
import fullstack.rest.model.LoginRequest;
import fullstack.rest.model.LoginResponse;
import fullstack.service.exception.*;
import fullstack.util.ContactValidator;
import fullstack.util.Validation;
import fullstack.util.Messages;
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
    private final UserRepository userRepository;
    private final HashCalculator hashCalculator;
    private final UserSessionRepository userSessionRepository;
    private final NotificationService notificationService;

    @Inject
    public AuthenticationService(UserRepository userRepository, HashCalculator hashCalculator, UserSessionRepository userSessionRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.hashCalculator = hashCalculator;
        this.userSessionRepository = userSessionRepository;
        this.notificationService = notificationService;

    }

    @Transactional
    public User register(CreateUserRequest request) throws UserCreationException {
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

    private void checkIfEmailOrPhoneExists(CreateUserRequest request) throws UserCreationException {
        String email = request.getEmail();
        String phone = request.getPhone();

        if (email != null && !email.trim().isEmpty()) {
            boolean emailInUse = userRepository.findByEmail(email).isPresent();
            if (emailInUse) {
                throw new UserCreationException(Messages.EMAIL_ALREADY_USED);
            }
        }

        if (phone != null && !phone.trim().isEmpty()) {
            phone = ContactValidator.formatPhone(phone);
            boolean phoneInUse = userRepository.findByPhone(phone).isPresent();
            if (phoneInUse) {
                throw new UserCreationException(Messages.PHONE_ALREADY_USED);
            }
        }
    }

    @Transactional
    public void verifyEmail(String token, String email) throws UserCreationException {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UserCreationException("Utente non trovato.");
        }

        User user = userOpt.get();
        if (user.getTokenEmail() == null || !user.getTokenEmail().equals(token)) {
            throw new UserCreationException("Token di verifica non valido.");
        }

        user.setEmailVerified(true);
        user.setTokenEmail(null);
        userRepository.persist(user);
    }

    @Transactional
    public void verifyPhone(String token, String phone) throws UserCreationException {
        Optional<User> optionalUser = userRepository.findByPhone(phone);
        User user = optionalUser.orElseThrow(() -> new UserCreationException(USER_NOT_FOUND));

        if (user.getTokenPhone() == null || !user.getTokenPhone().equals(token)) {
            throw new UserCreationException(INVALID_TOKEN);
        }

        user.setPhoneVerified(true);
        user.setTokenPhone(null);
        userRepository.persist(user);
    }

    @Transactional
    public LoginResponse authenticate(LoginRequest request, Boolean rememberMe) throws UserNotFoundException, WrongPasswordException, SessionAlreadyExistsException {
        Validation.validateLoginRequest(request);

        Optional<User> optionalUser = userRepository.findByEmailOrPhone(request.getEmailOrPhone());
        User user = optionalUser.orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        if (!user.getEmailVerified() && !user.getPhoneVerified()) {
            throw new UnAuthorizedAccessException("Contatto non verificato. Verifica il tuo indirizzo email o il tuo numero di telefono.");
        }

        String hashedProvidedPassword = hashPassword(request.getPassword());
        if (!verifyPassword(user.getPassword(), hashedProvidedPassword)) {
            throw new WrongPasswordException("Password errata.");
        }

        Optional<UserSession> existingSession = userSessionRepository.findByUserId(user.getId());
        if (existingSession.isPresent()) {
            UserSession session = existingSession.get();
            if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
                userSessionRepository.delete(session);
            } else {
                throw new SessionAlreadyExistsException("Utente ha già una sessione attiva.");
            }
        }

        checkIfSessionExists(user.getId());

        if (Boolean.TRUE.equals(rememberMe)) {
            String sessionId = createSessionLong(user);
            return new LoginResponse(user.getName(), sessionId, "Login avvenuto con successo");
        } else {
            String sessionId = createSession(user);
            return new LoginResponse(user.getName(), sessionId, "Login avvenuto con successo");
        }
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
            throw new SessionAlreadyExistsException("Utente ha già una sessione attiva.");
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
