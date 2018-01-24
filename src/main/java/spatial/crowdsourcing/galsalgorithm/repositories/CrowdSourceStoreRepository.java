package spatial.crowdsourcing.galsalgorithm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spatial.crowdsourcing.galsalgorithm.model.CrowdSourceStore;

/**
 * Created by Monjur-E-Morshed on 24-Jan-18.
 */
public interface CrowdSourceStoreRepository extends JpaRepository<CrowdSourceStore, Long> {
}
