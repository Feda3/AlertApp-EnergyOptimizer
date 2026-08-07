package florinl.licenta.service;

import florinl.licenta.domain.UserAlertSettings;
import florinl.licenta.repository.UserAlertSettingsRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link florinl.licenta.domain.UserAlertSettings}.
 */
@Service
@Transactional
public class UserAlertSettingsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserAlertSettingsService.class);

    private final UserAlertSettingsRepository userAlertSettingsRepository;

    public UserAlertSettingsService(UserAlertSettingsRepository userAlertSettingsRepository) {
        this.userAlertSettingsRepository = userAlertSettingsRepository;
    }

    /**
     * Save a userAlertSettings.
     *
     * @param userAlertSettings the entity to save.
     * @return the persisted entity.
     */
    public UserAlertSettings save(UserAlertSettings userAlertSettings) {
        LOG.debug("Request to save UserAlertSettings : {}", userAlertSettings);
        return userAlertSettingsRepository.save(userAlertSettings);
    }

    /**
     * Update a userAlertSettings.
     *
     * @param userAlertSettings the entity to save.
     * @return the persisted entity.
     */
    public UserAlertSettings update(UserAlertSettings userAlertSettings) {
        LOG.debug("Request to update UserAlertSettings : {}", userAlertSettings);
        return userAlertSettingsRepository.save(userAlertSettings);
    }

    /**
     * Partially update a userAlertSettings.
     *
     * @param userAlertSettings the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserAlertSettings> partialUpdate(UserAlertSettings userAlertSettings) {
        LOG.debug("Request to partially update UserAlertSettings : {}", userAlertSettings);

        return userAlertSettingsRepository
            .findById(userAlertSettings.getId())
            .map(existingUserAlertSettings -> {
                if (userAlertSettings.getSymbol() != null) {
                    existingUserAlertSettings.setSymbol(userAlertSettings.getSymbol());
                }
                if (userAlertSettings.getThreshold() != null) {
                    existingUserAlertSettings.setThreshold(userAlertSettings.getThreshold());
                }
                if (userAlertSettings.getTriggerIfGreater() != null) {
                    existingUserAlertSettings.setTriggerIfGreater(userAlertSettings.getTriggerIfGreater());
                }
                if (userAlertSettings.getAction() != null) {
                    existingUserAlertSettings.setAction(userAlertSettings.getAction());
                }
                if (userAlertSettings.getStartTime() != null) {
                    existingUserAlertSettings.setStartTime(userAlertSettings.getStartTime());
                }
                if (userAlertSettings.getEndTime() != null) {
                    existingUserAlertSettings.setEndTime(userAlertSettings.getEndTime());
                }
                if (userAlertSettings.getMinDurationMinutes() != null) {
                    existingUserAlertSettings.setMinDurationMinutes(userAlertSettings.getMinDurationMinutes());
                }
                if (userAlertSettings.getIsActive() != null) {
                    existingUserAlertSettings.setIsActive(userAlertSettings.getIsActive());
                }

                return existingUserAlertSettings;
            })
            .map(userAlertSettingsRepository::save);
    }

    /**
     * Get all the userAlertSettings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UserAlertSettings> findAll(Pageable pageable) {
        LOG.debug("Request to get all UserAlertSettings");
        return userAlertSettingsRepository.findAll(pageable);
    }

    /**
     * Get all the userAlertSettings with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<UserAlertSettings> findAllWithEagerRelationships(Pageable pageable) {
        return userAlertSettingsRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one userAlertSettings by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserAlertSettings> findOne(Long id) {
        LOG.debug("Request to get UserAlertSettings : {}", id);
        return userAlertSettingsRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the userAlertSettings by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete UserAlertSettings : {}", id);
        userAlertSettingsRepository.deleteById(id);
    }
}
