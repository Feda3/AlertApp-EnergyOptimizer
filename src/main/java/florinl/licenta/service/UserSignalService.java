package florinl.licenta.service;

import florinl.licenta.domain.UserSignal;
import florinl.licenta.repository.UserSignalRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link florinl.licenta.domain.UserSignal}.
 */
@Service
@Transactional
public class UserSignalService {

    private static final Logger LOG = LoggerFactory.getLogger(UserSignalService.class);

    private final UserSignalRepository userSignalRepository;

    public UserSignalService(UserSignalRepository userSignalRepository) {
        this.userSignalRepository = userSignalRepository;
    }

    /**
     * Save a userSignal.
     *
     * @param userSignal the entity to save.
     * @return the persisted entity.
     */
    public UserSignal save(UserSignal userSignal) {
        LOG.debug("Request to save UserSignal : {}", userSignal);
        return userSignalRepository.save(userSignal);
    }

    /**
     * Update a userSignal.
     *
     * @param userSignal the entity to save.
     * @return the persisted entity.
     */
    public UserSignal update(UserSignal userSignal) {
        LOG.debug("Request to update UserSignal : {}", userSignal);
        return userSignalRepository.save(userSignal);
    }

    /**
     * Partially update a userSignal.
     *
     * @param userSignal the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserSignal> partialUpdate(UserSignal userSignal) {
        LOG.debug("Request to partially update UserSignal : {}", userSignal);

        return userSignalRepository
            .findById(userSignal.getId())
            .map(existingUserSignal -> {
                if (userSignal.getSignalDate() != null) {
                    existingUserSignal.setSignalDate(userSignal.getSignalDate());
                }
                if (userSignal.getAction() != null) {
                    existingUserSignal.setAction(userSignal.getAction());
                }
                if (userSignal.getSummaryMessage() != null) {
                    existingUserSignal.setSummaryMessage(userSignal.getSummaryMessage());
                }

                return existingUserSignal;
            })
            .map(userSignalRepository::save);
    }

    /**
     * Get all the userSignals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UserSignal> findAll(Pageable pageable) {
        LOG.debug("Request to get all UserSignals");
        return userSignalRepository.findAll(pageable);
    }

    /**
     * Get all the userSignals with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<UserSignal> findAllWithEagerRelationships(Pageable pageable) {
        return userSignalRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one userSignal by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserSignal> findOne(Long id) {
        LOG.debug("Request to get UserSignal : {}", id);
        return userSignalRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the userSignal by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete UserSignal : {}", id);
        userSignalRepository.deleteById(id);
    }
}
