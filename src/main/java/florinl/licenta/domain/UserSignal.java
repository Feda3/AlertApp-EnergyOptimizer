package florinl.licenta.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import florinl.licenta.domain.enumeration.AlertAction;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UserSignal.
 */
@Entity
@Table(name = "user_signal")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserSignal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "signal_date", nullable = false)
    private LocalDate signalDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private AlertAction action;

    @Column(name = "summary_message")
    private String summaryMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private UserAlertSettings setting;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserSignal id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getSignalDate() {
        return this.signalDate;
    }

    public UserSignal signalDate(LocalDate signalDate) {
        this.setSignalDate(signalDate);
        return this;
    }

    public void setSignalDate(LocalDate signalDate) {
        this.signalDate = signalDate;
    }

    public AlertAction getAction() {
        return this.action;
    }

    public UserSignal action(AlertAction action) {
        this.setAction(action);
        return this;
    }

    public void setAction(AlertAction action) {
        this.action = action;
    }

    public String getSummaryMessage() {
        return this.summaryMessage;
    }

    public UserSignal summaryMessage(String summaryMessage) {
        this.setSummaryMessage(summaryMessage);
        return this;
    }

    public void setSummaryMessage(String summaryMessage) {
        this.summaryMessage = summaryMessage;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserSignal user(User user) {
        this.setUser(user);
        return this;
    }

    public UserAlertSettings getSetting() {
        return this.setting;
    }

    public void setSetting(UserAlertSettings userAlertSettings) {
        this.setting = userAlertSettings;
    }

    public UserSignal setting(UserAlertSettings userAlertSettings) {
        this.setSetting(userAlertSettings);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserSignal)) {
            return false;
        }
        return getId() != null && getId().equals(((UserSignal) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserSignal{" +
            "id=" + getId() +
            ", signalDate='" + getSignalDate() + "'" +
            ", action='" + getAction() + "'" +
            ", summaryMessage='" + getSummaryMessage() + "'" +
            "}";
    }
}
