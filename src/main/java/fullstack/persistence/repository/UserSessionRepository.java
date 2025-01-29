package fullstack.persistence.repository;

import fullstack.persistence.model.UserSession;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserSessionRepository implements PanacheRepository<UserSession> {

    public Optional<UserSession> findBySessionId(String sessionId) {
        System.out.println("sessionId: " + sessionId);
        return find("sessionId", sessionId).firstResultOptional();
    }

    public Optional<UserSession> findByUserId(String userId) {
        return find("user.id", userId).firstResultOptional();
    }
}
