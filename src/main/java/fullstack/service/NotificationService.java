package fullstack.service;

import fullstack.persistence.model.Event;
import fullstack.persistence.model.User;
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
                "Conferma la tua email",
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


    public void sendBookingConfirmationEmail(User user, Event event) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("L'utente non ha un'email valida.");
        }
        String emailContent = "<h1>Conferma Prenotazione</h1>" +
                "<p>Ciao " + user.getName() + " " + user.getSurname() + ",</p>" +
                "<p>La tua prenotazione per l'evento \"" + event.getTitle() + "\" è stata confermata.</p>" +
                "<p>Dettagli dell'evento:</p>" +
                "<li><strong>Data:</strong> " + event.getDate() + "</li>" +
                "<li><strong>Descrizione:</strong> " + event.getDescription() + "</li>";

        mailer.send(Mail.withHtml(user.getEmail(), "Conferma Prenotazione Evento", emailContent));
        System.out.println("Email inviata correttamente a: " + user.getEmail());
    }


    public void sendBookingConfirmationSms(User user, Event event) {
        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            throw new IllegalArgumentException("L'utente non ha un numero di telefono valido.");
        }
        try {
            smsService.sendSms(user.getPhone(), "La tua prenotazione per l'evento \"" + event.getTitle() + "\" è stata confermata.");
        } catch (SmsSendingException e) {
            throw new RuntimeException("Errore durante l'invio dell'SMS: " + e.getMessage(), e);
        }
    }

    public void sendBookingCancellationEmail(User user, Event event) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("L'utente non ha un'email valida.");
        }
        String emailContent = "<h1>Cancellazione Prenotazione</h1>" +
                "<p>Ciao " + user.getName() + " " + user.getSurname() + ",</p>" +
                "<p>La tua prenotazione per l'evento \"" + event.getTitle() + "\" è stata cancellata.</p>" +
                "<p>Dettagli dell'evento:</p>" +
                "<li><strong>Data:</strong> " + event.getDate() + "</li>" +
                "<li><strong>Descrizione:</strong> " + event.getDescription() + "</li>" ;

        mailer.send(Mail.withHtml(user.getEmail(), "Cancellazione Prenotazione Evento", emailContent));
        System.out.println("Email inviata correttamente a: " + user.getEmail());
    }

    public void sendBookingCancellationSms(User user, Event event) {
        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            throw new IllegalArgumentException("L'utente non ha un numero di telefono valido.");
        }
        try {
            smsService.sendSms(user.getPhone(), "La tua prenotazione per l'evento \"" + event.getTitle() + "\" è stata cancellata.");
        } catch (SmsSendingException e) {
            throw new RuntimeException("Errore durante l'invio dell'SMS: " + e.getMessage(), e);
        }
    }
}
