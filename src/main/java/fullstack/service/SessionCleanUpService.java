package fullstack.service;

import fullstack.persistence.UserSessionRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

@ApplicationScoped
public class SessionCleanUpService {

    @Inject
    UserSessionRepository userSessionRepository;

    @Transactional
    @Scheduled(every = "1h")
    public void cleanupExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        userSessionRepository.streamAll()
                .filter(session -> session.getExpiresAt().isBefore(now))
                .forEach(userSessionRepository::delete);
    }
}
