package fullstack.service;

import fullstack.service.exception.SmsSendingException;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class NotificationService {

    private final Mailer mailer;
    private final SmsService smsService;

    @Inject
    public NotificationService(Mailer mailer, SmsService smsService) {
        this.mailer = mailer;
        this.smsService = smsService;
    }

    public void sendVerificationEmail(User user, String verificationLink) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("L'utente non ha un'email valida.");
        }
        mailer.send(Mail.withHtml(user.getEmail(),
                "Conferma la tua registrazione",
                "<h1>Benvenuto " + user.getName() + " " + user.getSurname() + "!</h1>" +
                        "<p>Per favore, clicca sul link seguente per verificare il tuo indirizzo email:</p>" +
                        "<a href=\"" + verificationLink + "\">Verifica la tua email</a>"));
        System.out.println("Email inviata correttamente a: " + user.getEmail());
    }

    public void sendVerificationSms(User user, String otp) {
        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            throw new IllegalArgumentException("L'utente non ha un numero di telefono valido.");
        }
        try {
            smsService.sendSms(user.getPhone(), "Il tuo codice OTP è: " + otp);
        } catch (SmsSendingException e) {
            throw new RuntimeException("Errore durante l'invio dell'SMS: " + e.getMessage(), e);
        }
    }
}
