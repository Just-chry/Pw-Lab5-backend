package fullstack.persistence.repository;

import fullstack.persistence.model.Ticket;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TicketRepository implements PanacheRepository<Ticket> {

    public Ticket findAvailableTicketForEvent(String eventId) {
        return find("eventId = ?1 and userId is null", eventId)
                .firstResult();
    }
}