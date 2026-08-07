package florinl.licenta.repository;

import florinl.licenta.domain.DailyMarketData;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DailyMarketData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DailyMarketDataRepository extends JpaRepository<DailyMarketData, Long> {}
