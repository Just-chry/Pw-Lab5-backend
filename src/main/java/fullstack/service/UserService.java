package fullstack.service;

import fullstack.persistence.repository.UserRepository;
import fullstack.persistence.repository.UserSessionRepository;
import fullstack.persistence.model.Role;
import fullstack.persistence.model.User;
import fullstack.persistence.model.UserSession;
import fullstack.service.exception.UserNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    NotificationService notificationService;
    private final UserSessionRepository userSessionRepository;

    public UserService(UserSessionRepository userSessionRepository) {
        this.userSessionRepository = userSessionRepository;
    }


    public User getUserById(String userId) throws UserNotFoundException {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException("Utente non trovato.");
        }
        return user;
    }

    @Transactional
    public void updateProfile(String userId, User updatedUser) throws UserNotFoundException {
        User user = getUserById(userId);

        user.setName(updatedUser.getName());
        user.setSurname(updatedUser.getSurname());

        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
            user.setEmail(updatedUser.getEmail());
            user.setEmailVerified(false);
            user.setTokenEmail(UUID.randomUUID().toString());
            String verificationLink = "http://localhost:8080/auth/verifyEmail?token=" + user.getTokenEmail() + "&contact=" + user.getEmail();
            notificationService.sendVerificationEmail(user, verificationLink);
        }

        if (updatedUser.getPhone() != null && !updatedUser.getPhone().equals(user.getPhone())) {
            user.setPhone(updatedUser.getPhone());
            user.setPhoneVerified(false);
            user.setTokenPhone(generateOtp());
            notificationService.sendVerificationSms(user, user.getTokenPhone());
        }

        userRepository.persist(user);
    }

    @Transactional
    public void deleteUser(String userId) throws UserNotFoundException {
        User user = getUserById(userId);
        userRepository.delete(user);
    }

    public List<User> listUsers() {
        return userRepository.listAll();
    }

    @Transactional
    public void promoteUserToAdmin(String userId) throws UserNotFoundException {
        User user = getUserById(userId);
        user.setRole(Role.admin);
        userRepository.persist(user);
    }

    @Transactional
    public void demoteUserToUser(String userId) throws UserNotFoundException {
        User user = getUserById(userId);
        user.setRole(Role.user);
        userRepository.persist(user);
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public Role getUserRoleBySessionId(String sessionId) throws UserNotFoundException {
        Optional<UserSession> userSessionOptional = userSessionRepository.findBySessionId(sessionId);
        if (userSessionOptional.isEmpty()) {
            throw new UserNotFoundException("Session not found or expired.");
        }
        UserSession userSession = userSessionOptional.get();
        if (userSession.getUser() == null) {
            throw new UserNotFoundException("User associated with the session not found.");
        }
        return userSession.getUser().getRole();
    }
}