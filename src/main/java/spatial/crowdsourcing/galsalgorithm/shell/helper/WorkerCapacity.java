package spatial.crowdsourcing.galsalgorithm.shell.helper;

/**
 * Created by Monjur-E-Morshed on 24-Jan-18.
 */
public class WorkerCapacity {
  private Long workerId;

  private int capacity;

  public WorkerCapacity() {
  }

  public Long getWorkerId() {
    return workerId;
  }

  public void setWorkerId(Long pWorkerId) {
    workerId = pWorkerId;
  }

  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int pCapacity) {
    capacity = pCapacity;
  }
}
