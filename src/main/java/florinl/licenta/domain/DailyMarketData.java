package florinl.licenta.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A DailyMarketData.
 */
@Entity
@Table(name = "daily_market_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DailyMarketData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "fetch_date", nullable = false)
    private LocalDate fetchDate;

    @NotNull
    @Column(name = "symbol", nullable = false)
    private String symbol;

    @NotNull
    @Column(name = "metric_value", nullable = false)
    private Double metricValue;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DailyMarketData id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFetchDate() {
        return this.fetchDate;
    }

    public DailyMarketData fetchDate(LocalDate fetchDate) {
        this.setFetchDate(fetchDate);
        return this;
    }

    public void setFetchDate(LocalDate fetchDate) {
        this.fetchDate = fetchDate;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public DailyMarketData symbol(String symbol) {
        this.setSymbol(symbol);
        return this;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getMetricValue() {
        return this.metricValue;
    }

    public DailyMarketData metricValue(Double metricValue) {
        this.setMetricValue(metricValue);
        return this;
    }

    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DailyMarketData)) {
            return false;
        }
        return getId() != null && getId().equals(((DailyMarketData) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DailyMarketData{" +
            "id=" + getId() +
            ", fetchDate='" + getFetchDate() + "'" +
            ", symbol='" + getSymbol() + "'" +
            ", metricValue=" + getMetricValue() +
            "}";
    }
}
