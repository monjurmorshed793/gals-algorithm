package spatial.crowdsourcing.galsalgorithm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spatial.crowdsourcing.galsalgorithm.model.SpatialData;

import java.util.List;

public interface SpatialDataRepository extends JpaRepository<SpatialData, Long>{
    @Query("select distinct locationId from SpatialData s")
    List<Long> findDistinctByLocationId();
    @Query("select s from SpatialData  s where s.locationId in (select sl.locationId from SpatialData  sl)")
    List<SpatialData> findByDistinctAndLocationId();
    @Query("select distinct userId from SpatialData  s")
    List<Long> findDistinctUserList();

    List<SpatialData> findByUserIdIn(List<Long> userIds);

  @Query("delete from SpatialData s")
  void deleteAllSpatialData();
}
