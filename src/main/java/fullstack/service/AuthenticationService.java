package fullstack.service;

import fullstack.persistence.UserRepository;
import fullstack.persistence.model.User;
import fullstack.rest.model.CreateUserRequest;
import fullstack.service.exception.SmsSendingException;
import fullstack.service.exception.UserCreationException;
import fullstack.util.Validation;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AuthenticationService {
    private final UserRepository userRepository;
    private final HashCalculator hashCalculator;
    private final Mailer mailer;
    private final SmsService smsService;

    @Inject
    public AuthenticationService(UserRepository userRepository, HashCalculator hashCalculator, Mailer mailer, SmsService smsService) {
        this.userRepository = userRepository;
        this.hashCalculator = hashCalculator;
        this.mailer = mailer;
        this.smsService = smsService;
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

    public void sendVerificationEmail(User user, String verificationLink) {
        mailer.send(Mail.withHtml(user.getEmail(),
                "Conferma la tua registrazione",
                "<h1>Benvenuto " + user.getName() + " " + user.getSurname() + "!</h1>" +
                        "<p>Per favore, clicca sul link seguente per verificare il tuo indirizzo email:</p>" +
                        "<a href=\"" + verificationLink + "\">Verifica la tua email</a>"));
        System.out.println("Email inviata correttamente a: " + user.getEmail());

    }

    public void sendVerificationSms(User user) throws UserCreationException {
        String otp = user.getTokenPhone();
        try {
            smsService.sendSms(user.getPhone(), "Il tuo codice OTP è: " + otp);
        } catch (SmsSendingException e) {
            throw new UserCreationException("Errore durante l'invio dell'OTP: " + e.getMessage());
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
        Optional<User> userOpt = userRepository.findByPhone(phone);
        if (userOpt.isEmpty()) {
            throw new UserCreationException("Utente non trovato.");
        }

        User user = userOpt.get();
        if (user.getTokenPhone() == null || !user.getTokenPhone().equals(token)) {
            throw new UserCreationException("Token di verifica non valido.");
        }

        user.setPhoneVerified(true);
        user.setTokenPhone(null);
        userRepository.persist(user);
    }
}
