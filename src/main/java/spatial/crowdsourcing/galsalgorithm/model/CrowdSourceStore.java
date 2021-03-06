package spatial.crowdsourcing.galsalgorithm.model;

import javax.persistence.*;

/**
 * Created by Monjur-E-Morshed on 24-Jan-18.
 */
@Entity
@Table(name = "crowd_source_store")
public class CrowdSourceStore {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String date;

  private Long userId;

  private Long taskId;

  public CrowdSourceStore() {
  }

  public String getDate() {
    return date;
  }

  public void setDate(String pDate) {
    date = pDate;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long pUserId) {
    userId = pUserId;
  }

  public Long getTaskId() {
    return taskId;
  }

  public void setTaskId(Long pTaskId) {
    taskId = pTaskId;
  }
}
