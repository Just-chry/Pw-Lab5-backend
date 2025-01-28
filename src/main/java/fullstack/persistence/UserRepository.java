package fullstack.persistence;

import fullstack.persistence.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, String> {

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<User> findByPhone(String phone) {
        return find("phone", phone).firstResultOptional();
    }

    public Optional<User> findByEmailOrPhone(String emailOrPhone) {
        return find("email = ?1 OR phone = ?1", emailOrPhone).firstResultOptional();
    }

    public User findBySessionId(String sessionId) {
        return find("sessionId", sessionId).firstResult();
    }
}
