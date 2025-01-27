package fullstack.persistence;

import fullstack.persistence.model.UserSession;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserSessionRepository implements PanacheRepository<UserSession> {

    public Optional<UserSession> findBySessionId(String sessionId) {
        return find("sessionId", sessionId).firstResultOptional();
    }
    public Optional<UserSession> findByUserId(String userId) {
        return find("user.id", userId).firstResultOptional();
    }

}
