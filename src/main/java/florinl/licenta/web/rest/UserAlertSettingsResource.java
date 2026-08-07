package florinl.licenta.web.rest;

import florinl.licenta.domain.UserAlertSettings;
import florinl.licenta.repository.UserAlertSettingsRepository;
import florinl.licenta.repository.UserRepository;
import florinl.licenta.security.AuthoritiesConstants;
import florinl.licenta.security.SecurityUtils;
import florinl.licenta.service.UserAlertSettingsService;
import florinl.licenta.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link florinl.licenta.domain.UserAlertSettings}.
 */
@RestController
@RequestMapping("/api/user-alert-settings")
public class UserAlertSettingsResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserAlertSettingsResource.class);

    private static final String ENTITY_NAME = "userAlertSettings";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserAlertSettingsService userAlertSettingsService;
    private final UserAlertSettingsRepository userAlertSettingsRepository;

    private final UserRepository userRepository;

    public UserAlertSettingsResource(
        UserAlertSettingsService userAlertSettingsService,
        UserAlertSettingsRepository userAlertSettingsRepository,
        UserRepository userRepository
    ) {
        this.userAlertSettingsService = userAlertSettingsService;
        this.userAlertSettingsRepository = userAlertSettingsRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /user-alert-settings} : Create a new userAlertSettings.
     *
     * @param userAlertSettings the userAlertSettings to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userAlertSettings, or with status {@code 400 (Bad Request)} if the userAlertSettings has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserAlertSettings> createUserAlertSettings(@RequestBody UserAlertSettings userAlertSettings)
        throws URISyntaxException {
        LOG.debug("REST request to save UserAlertSettings : {}", userAlertSettings);
        if (userAlertSettings.getId() != null) {
            throw new BadRequestAlertException("A new userAlertSettings cannot already have an ID", ENTITY_NAME, "idexists");
        }

        // --- NEW SECURITY LOGIC: Stop ID Spoofing ---
        // If it's a regular user, forcefully override the 'user' attached to the request
        // with their actual, cryptographically verified logged-in profile.
        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).ifPresent(userAlertSettings::setUser);
        }
        // --------------------------------------------

        // Save using your Service layer instead of the Repository
        UserAlertSettings result = userAlertSettingsService.save(userAlertSettings);

        return ResponseEntity.created(
            new URI("/api/user-alert-settings/" + result.getId())
        )// Note: HeaderUtil might be named slightly differently depending on your JHipster version,
        // leave your existing return statement exactly as JHipster generated it!
        .body(result);
    }

    /**
     * {@code PUT  /user-alert-settings/:id} : Updates an existing userAlertSettings.
     *
     * @param id the id of the userAlertSettings to save.
     * @param userAlertSettings the userAlertSettings to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userAlertSettings,
     * or with status {@code 400 (Bad Request)} if the userAlertSettings is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userAlertSettings couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserAlertSettings> updateUserAlertSettings(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserAlertSettings userAlertSettings
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserAlertSettings : {}, {}", id, userAlertSettings);
        if (userAlertSettings.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userAlertSettings.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userAlertSettingsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userAlertSettings = userAlertSettingsService.update(userAlertSettings);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userAlertSettings.getId().toString()))
            .body(userAlertSettings);
    }

    /**
     * {@code PATCH  /user-alert-settings/:id} : Partial updates given fields of an existing userAlertSettings, field will ignore if it is null
     *
     * @param id the id of the userAlertSettings to save.
     * @param userAlertSettings the userAlertSettings to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userAlertSettings,
     * or with status {@code 400 (Bad Request)} if the userAlertSettings is not valid,
     * or with status {@code 404 (Not Found)} if the userAlertSettings is not found,
     * or with status {@code 500 (Internal Server Error)} if the userAlertSettings couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserAlertSettings> partialUpdateUserAlertSettings(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserAlertSettings userAlertSettings
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserAlertSettings partially : {}, {}", id, userAlertSettings);
        if (userAlertSettings.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userAlertSettings.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userAlertSettingsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserAlertSettings> result = userAlertSettingsService.partialUpdate(userAlertSettings);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userAlertSettings.getId().toString())
        );
    }

    /**
     * {@code GET  /user-alert-settings} : get all the userAlertSettings.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userAlertSettings in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UserAlertSettings>> getAllUserAlertSettings(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of UserAlertSettings");
        Page<UserAlertSettings> page;

        // 1. If it's an ADMIN, run the default JHipster logic to get everything
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            if (eagerload) {
                page = userAlertSettingsService.findAllWithEagerRelationships(pageable);
            } else {
                page = userAlertSettingsService.findAll(pageable);
            }
        }
        // 2. If it's a REGULAR USER, fetch their specific data using our new paginated query
        else {
            page = userAlertSettingsRepository.findByUserIsCurrentUser(pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-alert-settings/:id} : get the "id" userAlertSettings.
     *
     * @param id the id of the userAlertSettings to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userAlertSettings, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserAlertSettings> getUserAlertSettings(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserAlertSettings : {}", id);
        Optional<UserAlertSettings> userAlertSettings = userAlertSettingsService.findOne(id);

        // --- SECURITY LOGIC: IDOR Protection ---
        if (userAlertSettings.isPresent() && !SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            String currentLogin = SecurityUtils.getCurrentUserLogin().orElse("");

            // Get the owner of the setting safely
            String ownerLogin = "";
            if (userAlertSettings.get().getUser() != null) {
                ownerLogin = userAlertSettings.get().getUser().getLogin();
            }

            // If the logged-in user doesn't own it, wipe it out so it returns a 404
            if (!currentLogin.equals(ownerLogin)) {
                LOG.warn("User {} tried to access setting {} belonging to {}", currentLogin, id, ownerLogin);
                userAlertSettings = Optional.empty();
            }
        }
        // -------------------------------------------

        return ResponseUtil.wrapOrNotFound(userAlertSettings);
    }

    /**
     * {@code DELETE  /user-alert-settings/:id} : delete the "id" userAlertSettings.
     *
     * @param id the id of the userAlertSettings to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserAlertSettings(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserAlertSettings : {}", id);
        userAlertSettingsService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
