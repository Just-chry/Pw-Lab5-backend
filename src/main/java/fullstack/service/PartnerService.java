package fullstack.service;

import fullstack.persistence.repository.PartnerRepository;
import fullstack.service.exception.AdminAccessException;
import fullstack.service.exception.UserNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import fullstack.persistence.model.Partner;
import jakarta.ws.rs.core.NoContentException;
import org.hibernate.SessionException;

import java.util.List;
import java.util.UUID;

import static fullstack.util.Messages.ADMIN_REQUIRED;
import static fullstack.util.Messages.PARTNER_NOT_FOUND;

@ApplicationScoped
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final UserService userService;
    @Inject
    public PartnerService(PartnerRepository partnerRepository, UserService userService) {
        this.partnerRepository = partnerRepository;
        this.userService = userService;
    }

    public List<Partner> getAllPartners() throws NoContentException {
        List<Partner> partners = partnerRepository.listAll();
        if (partners.isEmpty()) {
            throw new NoContentException(PARTNER_NOT_FOUND);
        }
        return partners;
    }

    public Partner findById(String id) throws NoContentException {
        Partner partner = partnerRepository.findById(id);
        if (partner == null) {
            throw new NoContentException(PARTNER_NOT_FOUND);
        }
        return partner;
    }

    @Transactional
    public Partner save(String sessionId, Partner partner) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        partner.setId(UUID.randomUUID().toString());
        partnerRepository.persist(partner);
        return partner;
    }

    @Transactional
    public void deleteById(String sessionId, String id) throws SessionException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        partnerRepository.deleteById(id);
    }

    @Transactional
    public int update(String sessionId, String id, Partner partner) throws SessionException, NoContentException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        int updated = partnerRepository.update(id, partner);
        if (updated == 0) {
            throw new NoContentException(PARTNER_NOT_FOUND);
        }
        return updated;
    }

    public List<Partner> findByValue(String value) throws NoContentException {
        List<Partner> partners = partnerRepository.findByValue(value);
        if (partners.isEmpty()) {
            throw new NoContentException(PARTNER_NOT_FOUND);
        }
        return partners;
    }
}
