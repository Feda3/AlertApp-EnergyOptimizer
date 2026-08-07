package florinl.licenta.domain;

import florinl.licenta.domain.enumeration.AlertAction;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UserAlertSettings.
 */
@Entity
@Table(name = "user_alert_settings")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserAlertSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "symbol", nullable = false)
    private String symbol;

    @NotNull
    @Column(name = "threshold", nullable = false)
    private Double threshold;

    @NotNull
    @Column(name = "trigger_if_greater", nullable = false)
    private Boolean triggerIfGreater;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private AlertAction action;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @Column(name = "min_duration_minutes")
    private Integer minDurationMinutes;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserAlertSettings id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public UserAlertSettings symbol(String symbol) {
        this.setSymbol(symbol);
        return this;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getThreshold() {
        return this.threshold;
    }

    public UserAlertSettings threshold(Double threshold) {
        this.setThreshold(threshold);
        return this;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public Boolean getTriggerIfGreater() {
        return this.triggerIfGreater;
    }

    public UserAlertSettings triggerIfGreater(Boolean triggerIfGreater) {
        this.setTriggerIfGreater(triggerIfGreater);
        return this;
    }

    public void setTriggerIfGreater(Boolean triggerIfGreater) {
        this.triggerIfGreater = triggerIfGreater;
    }

    public AlertAction getAction() {
        return this.action;
    }

    public UserAlertSettings action(AlertAction action) {
        this.setAction(action);
        return this;
    }

    public void setAction(AlertAction action) {
        this.action = action;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public UserAlertSettings startTime(String startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public UserAlertSettings endTime(String endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getMinDurationMinutes() {
        return this.minDurationMinutes;
    }

    public UserAlertSettings minDurationMinutes(Integer minDurationMinutes) {
        this.setMinDurationMinutes(minDurationMinutes);
        return this;
    }

    public void setMinDurationMinutes(Integer minDurationMinutes) {
        this.minDurationMinutes = minDurationMinutes;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public UserAlertSettings isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAlertSettings user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAlertSettings)) {
            return false;
        }
        return getId() != null && getId().equals(((UserAlertSettings) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserAlertSettings{" +
            "id=" + getId() +
            ", symbol='" + getSymbol() + "'" +
            ", threshold=" + getThreshold() +
            ", triggerIfGreater='" + getTriggerIfGreater() + "'" +
            ", action='" + getAction() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", minDurationMinutes=" + getMinDurationMinutes() +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
