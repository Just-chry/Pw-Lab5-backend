package fullstack.service;

import fullstack.persistence.repository.PartnerRepository;
import fullstack.service.exception.AdminAccessException;
import fullstack.service.exception.UserNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import fullstack.persistence.model.Partner;
import java.util.List;
import java.util.UUID;

import static fullstack.util.Messages.ADMIN_REQUIRED;

@ApplicationScoped
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final UserService userService;
    @Inject
    public PartnerService(PartnerRepository partnerRepository, UserService userService) {
        this.partnerRepository = partnerRepository;
        this.userService = userService;
    }

    public List<Partner> getAllPartners() {
        return partnerRepository.listAll();
    }

    public Partner findById(String id) {
        return partnerRepository.findById(id);
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
    public void deleteById(String sessionId, String id) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        partnerRepository.deleteById(id);
    }

    @Transactional
    public int update(String sessionId, String id, Partner partner) throws UserNotFoundException {
        if (userService.isAdmin(sessionId)) {
            throw new AdminAccessException(ADMIN_REQUIRED);
        }
        return partnerRepository.update(id, partner);
    }

    public List<Partner> findByValue(String value) {
        return partnerRepository.findByValue(value);
    }
}
