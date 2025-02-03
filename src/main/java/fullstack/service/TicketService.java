package fullstack.service;

import fullstack.persistence.model.Ticket;
import fullstack.persistence.repository.TicketRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TicketService {

    @Inject
    TicketRepository ticketRepository;

    @Transactional
    public void createTicketsForEvent(String eventId, int maxParticipants) {
        for (int i = 0; i < maxParticipants; i++) {
            Ticket ticket = new Ticket();
            ticket.setEventId(eventId);
            ticket.setUserId(null);
            ticketRepository.persist(ticket);
        }
    }
}
