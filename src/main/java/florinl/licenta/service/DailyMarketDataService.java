package florinl.licenta.service;

import florinl.licenta.domain.DailyMarketData;
import florinl.licenta.repository.DailyMarketDataRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link florinl.licenta.domain.DailyMarketData}.
 */
@Service
@Transactional
public class DailyMarketDataService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyMarketDataService.class);

    private final DailyMarketDataRepository dailyMarketDataRepository;

    public DailyMarketDataService(DailyMarketDataRepository dailyMarketDataRepository) {
        this.dailyMarketDataRepository = dailyMarketDataRepository;
    }

    /**
     * Save a dailyMarketData.
     *
     * @param dailyMarketData the entity to save.
     * @return the persisted entity.
     */
    public DailyMarketData save(DailyMarketData dailyMarketData) {
        LOG.debug("Request to save DailyMarketData : {}", dailyMarketData);
        return dailyMarketDataRepository.save(dailyMarketData);
    }

    /**
     * Update a dailyMarketData.
     *
     * @param dailyMarketData the entity to save.
     * @return the persisted entity.
     */
    public DailyMarketData update(DailyMarketData dailyMarketData) {
        LOG.debug("Request to update DailyMarketData : {}", dailyMarketData);
        return dailyMarketDataRepository.save(dailyMarketData);
    }

    /**
     * Partially update a dailyMarketData.
     *
     * @param dailyMarketData the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DailyMarketData> partialUpdate(DailyMarketData dailyMarketData) {
        LOG.debug("Request to partially update DailyMarketData : {}", dailyMarketData);

        return dailyMarketDataRepository
            .findById(dailyMarketData.getId())
            .map(existingDailyMarketData -> {
                if (dailyMarketData.getFetchDate() != null) {
                    existingDailyMarketData.setFetchDate(dailyMarketData.getFetchDate());
                }
                if (dailyMarketData.getSymbol() != null) {
                    existingDailyMarketData.setSymbol(dailyMarketData.getSymbol());
                }
                if (dailyMarketData.getMetricValue() != null) {
                    existingDailyMarketData.setMetricValue(dailyMarketData.getMetricValue());
                }

                return existingDailyMarketData;
            })
            .map(dailyMarketDataRepository::save);
    }

    /**
     * Get all the dailyMarketData.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DailyMarketData> findAll(Pageable pageable) {
        LOG.debug("Request to get all DailyMarketData");
        return dailyMarketDataRepository.findAll(pageable);
    }

    /**
     * Get one dailyMarketData by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DailyMarketData> findOne(Long id) {
        LOG.debug("Request to get DailyMarketData : {}", id);
        return dailyMarketDataRepository.findById(id);
    }

    /**
     * Delete the dailyMarketData by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete DailyMarketData : {}", id);
        dailyMarketDataRepository.deleteById(id);
    }
}
