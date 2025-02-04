package fullstack.service;

import fullstack.persistence.model.Event;
import fullstack.persistence.model.User;
import fullstack.service.exception.ContactException;
import fullstack.service.exception.SmsSendingException;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static fullstack.util.Messages.*;

@ApplicationScoped
public class NotificationService {

    @Inject
    Mailer mailer;
    @Inject
    SmsService smsService;

    public void sendVerificationEmail(User user, String verificationLink) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException(INVALID_EMAIL);
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
            throw new IllegalArgumentException(INVALID_PHONE);
        }
        try {
            smsService.sendSms(user.getPhone(), "Il tuo codice OTP è: " + otp);
        } catch (SmsSendingException e) {
            throw new SmsSendingException(SMS_ERROR + e.getMessage());
        }
    }

    public void sendPasswordResetEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException(INVALID_EMAIL);
        }
        mailer.send(Mail.withHtml(user.getEmail(),
                "Reimposta la tua password",
                "<h1>Ciao " + user.getName() + " " + user.getSurname() + "!</h1>" +
                        "<p>Per reimpostare la tua password, utilizza il seguente codice: " + user.getTokenPassword() + "</p>"));
        System.out.println("Email inviata correttamente a: " + user.getEmail());
    }

    public void sendPasswordResetSms(User user) {
        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            throw new ContactException(INVALID_PHONE);
        }
        try {
            smsService.sendSms(user.getPhone(), "Il tuo codice per reimpostare la password è: " + user.getTokenPassword());
        } catch (SmsSendingException e) {
            throw new SmsSendingException(SMS_ERROR + e.getMessage());
        }
    }


    public void sendBookingConfirmationEmail(User user, Event event) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException(INVALID_EMAIL);
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
            throw new ContactException(INVALID_PHONE);
        }
        try {
            smsService.sendSms(user.getPhone(), "La tua prenotazione per l'evento \"" + event.getTitle() + "\" è stata confermata.");
        } catch (SmsSendingException e) {
            throw new RuntimeException(SMS_ERROR+ e.getMessage(), e);
        }
    }

    public void sendBookingCancellationEmail(User user, Event event) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new ContactException(INVALID_EMAIL);
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
            throw new ContactException(INVALID_PHONE);
        }
        try {
            smsService.sendSms(user.getPhone(), "La tua prenotazione per l'evento \"" + event.getTitle() + "\" è stata cancellata.");
        } catch (SmsSendingException e) {
            throw new SmsSendingException(SMS_ERROR + e.getMessage());
        }
    }
}
