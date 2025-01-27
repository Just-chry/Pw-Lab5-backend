package fullstack.service;

import fullstack.persistence.UserRepository;
import fullstack.persistence.model.User;
import fullstack.rest.model.CreateUserRequest;
import fullstack.service.exception.UserCreationException;
import fullstack.util.Validation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.security.SecureRandom;
import java.util.UUID;

@ApplicationScoped
public class AuthenticationService {
    private final UserRepository userRepository;
    private final HashCalculator hashCalculator;

    @Inject
    public AuthenticationService(UserRepository userRepository, HashCalculator hashCalculator) {
        this.userRepository = userRepository;
        this.hashCalculator = hashCalculator;
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
        } else if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            user.setTokenPhone(generateOtp());
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
                throw new UserCreationException("L'email è già in uso.");
            }
        }

        if (phone != null && !phone.trim().isEmpty()) {
            boolean phoneInUse = userRepository.findByPhone(phone).isPresent();
            if (phoneInUse) {
                throw new UserCreationException("Il numero di telefono è già in uso.");
            }
        }
    }
}
