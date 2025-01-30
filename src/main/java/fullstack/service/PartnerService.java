package fullstack.service;

import fullstack.persistence.repository.PartnerRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import fullstack.persistence.model.Partner;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PartnerService {
    @Inject
    PartnerRepository partnerRepository;

    public List<Partner> getAllPartners() {
        return partnerRepository.listAll();
    }

    public Partner findById(String id) {
        return partnerRepository.findById(id);
    }

    @Transactional
    public Partner save(Partner partner) {
        partner.setId(UUID.randomUUID().toString());
        partnerRepository.persist(partner);
        return partner;
    }

    @Transactional
    public void deleteById(String id) {
        partnerRepository.deleteById(id);
    }

    @Transactional
    public int update(String id, Partner partner) {
        return partnerRepository.update(id, partner);
    }

    public List<Partner> findByValue(String value) {
        return partnerRepository.findByValue(value);
    }
}
