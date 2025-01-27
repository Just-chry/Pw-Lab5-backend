package fullstack.service;

import fullstack.persistence.UserRepository;
import fullstack.persistence.model.Role;
import fullstack.persistence.model.User;
import fullstack.service.exception.UserNotFoundException;
import fullstack.util.ErrorMessages;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    NotificationService notificationService;

    public User getUserById(String userId) throws UserNotFoundException {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException("Utente non trovato.");
        }
        return user;
    }

    // Aggiorna il profilo utente
    @Transactional
    public void updateProfile(String userId, User updatedUser) throws UserNotFoundException {
        User user = getUserById(userId);

        // Aggiorna nome e cognome
        user.setName(updatedUser.getName());
        user.setSurname(updatedUser.getSurname());

        // Se l'email è stata modificata, richiedi una nuova verifica
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

    // Lista di tutti gli utenti
    public List<User> listUsers() {
        return userRepository.listAll();
    }

    // Promuovi un utente a admin
    @Transactional
    public void promoteUserToAdmin(String userId) throws UserNotFoundException {
        User user = getUserById(userId);
        user.setRole(Role.ADMIN);
        userRepository.persist(user);
    }

    // Retrocedi un utente a user
    @Transactional
    public void demoteUserToUser(String userId) throws UserNotFoundException {
        User user = getUserById(userId);
        user.setRole(Role.USER);
        userRepository.persist(user);
    }

    // Genera un OTP per la verifica del telefono
    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}