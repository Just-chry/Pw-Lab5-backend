package fullstack.persistence.repository;

import fullstack.persistence.model.Partner;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class PartnerRepository implements PanacheRepository<Partner> {

    public Partner findById(String id) {
        return find("id", id).firstResult();
    }

    public void deleteById(String id) {
        delete("id", id);
    }

    public int update(String id, Partner partner) {
        return update("name = ?1, description = ?2, place = ?3, website = ?4, email = ?5, image = ?6, value = ?7 where id = ?8",
                partner.getName(), partner.getDescription(), partner.getPlace(),
                partner.getWebsite(), partner.getEmail(), partner.getImage(),
                partner.getValue(), id);
    }

    public List<Partner> findByValue(String value) {
        return list("value", value);
    }
}
