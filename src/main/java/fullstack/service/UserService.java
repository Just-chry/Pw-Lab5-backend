package fullstack.service;

import fullstack.persistence.repository.UserRepository;
import fullstack.persistence.repository.UserSessionRepository;
import fullstack.persistence.model.Role;
import fullstack.persistence.model.User;
import fullstack.persistence.model.UserSession;
import fullstack.service.exception.AdminAccessException;
import fullstack.service.exception.UserNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fullstack.util.Messages.USER_NOT_FOUND;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;
    @Inject
    NotificationService notificationService;
    @Inject
    UserSessionRepository userSessionRepository;

    public User getUserById(String userId) throws UserNotFoundException {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
        return user;
    }

    // Aggiorna il profilo utente
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

        // Se il telefono è stato modificato, richiedi una nuova verifica
        if (updatedUser.getPhone() != null && !updatedUser.getPhone().equals(user.getPhone())) {
            user.setPhone(updatedUser.getPhone());
            user.setPhoneVerified(false);
            user.setTokenPhone(generateOtp());
            notificationService.sendVerificationSms(user, user.getTokenPhone());
        }

        userRepository.persist(user);
    }

    // Elimina un utente
    @Transactional
    public void deleteUser(String userId) throws UserNotFoundException {
        User user = getUserById(userId);
        userRepository.delete(user);
    }

    public List<User> listUsers(String sessionId) throws AdminAccessException, UserNotFoundException {
        if (!isAdmin(sessionId)) {
            throw new AdminAccessException("Accesso negato. Solo gli amministratori possono visualizzare tutti gli utenti.");
        }
        return userRepository.listAll();
    }

    @Transactional
    public void promoteUserToAdmin(String userId) throws UserNotFoundException {
        User user = getUserById(userId);
        user.setRole(Role.admin);
        userRepository.persist(user);
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public boolean isAdmin(String sessionId) throws UserNotFoundException {
        Optional<UserSession> session = userSessionRepository.findBySessionId(sessionId);
        if (session.isEmpty()) {
            throw new UserNotFoundException("Sessione non trovata.");
        }
        User user = session.get().getUser();
        return user.getRole() == Role.admin;
    }

    public String getUserIdBySessionId(String sessionId) throws UserNotFoundException {
        Optional<UserSession> session = userSessionRepository.findBySessionId(sessionId);
        if (session.isEmpty()) {
            throw new UserNotFoundException("Sessione non trovata.");
        }
        User user = session.get().getUser();
        return user.getId();
    }
}
