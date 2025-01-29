package fullstack.service;

import fullstack.persistence.repository.UserRepository;
import fullstack.persistence.repository.UserSessionRepository;
import fullstack.persistence.model.Role;
import fullstack.persistence.model.User;
import fullstack.persistence.model.UserSession;
import fullstack.rest.model.AdminResponse;
import fullstack.rest.model.UserResponse;
import fullstack.service.exception.AdminAccessException;
import fullstack.service.exception.UserNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static fullstack.util.Messages.USER_NOT_FOUND;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;
    @Inject
    NotificationService notificationService;
    @Inject
    UserSessionRepository userSessionRepository;

    @Transactional
    public void deleteUser(String userId) throws UserNotFoundException {
        User user = getUserById(userId);
        userRepository.delete(user);
    }

    public List<AdminResponse> listUsers(String sessionId) throws AdminAccessException, UserNotFoundException {
        if (!isAdmin(sessionId)) {
            throw new AdminAccessException("Accesso negato. Solo gli amministratori possono visualizzare tutti gli utenti.");
        }
        return userRepository.listAll().stream()
                .map(user -> new AdminResponse(user.getName(), user.getSurname(), user.getEmail(), user.getPhone(), user.getRole().name()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void promoteUserToAdmin(String userId, String sessionId) throws UserNotFoundException {
        if (!isAdmin(sessionId)) {
            throw new AdminAccessException("Accesso negato. Solo gli amministratori possono promuovere gli utenti.");
        }
        User user = getUserById(userId);
        user.setRole(Role.ADMIN);
        userRepository.persist(user);
    }


    public boolean isAdmin(String sessionId) throws UserNotFoundException {
        Optional<UserSession> session = userSessionRepository.findBySessionId(sessionId);
        if (session.isEmpty()) {
            throw new UserNotFoundException("Sessione non trovata.");
        }
        User user = session.get().getUser();
        return user.getRole() == Role.ADMIN;
    }

    public User getUserById(String userId) throws UserNotFoundException {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
        return user;
    }

    public UserResponse getUserResponseById(String userId) throws UserNotFoundException {
        User user = getUserById(userId);
        return new UserResponse(user.getName(), user.getSurname(), user.getEmail(), user.getPhone());
    }
}