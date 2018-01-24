package spatial.crowdsourcing.galsalgorithm.shell.helper;

/**
 * Created by Monjur-E-Morshed on 24-Jan-18.
 */
public class WorkerTaskDistance {
  private Long workerId;

  private Long locationId;

  private Double distance;

  public WorkerTaskDistance() {
  }

  public WorkerTaskDistance(Long pWorkerId, Long pLocationId, Double pDistance) {
    workerId = pWorkerId;
    locationId = pLocationId;
    distance = pDistance;
  }

  public Long getWorkerId() {
    return workerId;
  }

  public void setWorkerId(Long pWorkerId) {
    workerId = pWorkerId;
  }

  public Long getLocationId() {
    return locationId;
  }

  public void setLocationId(Long pLocationId) {
    locationId = pLocationId;
  }

  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double pDistance) {
    distance = pDistance;
  }
}
