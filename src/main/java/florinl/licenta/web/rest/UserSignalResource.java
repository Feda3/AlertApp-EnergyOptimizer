package florinl.licenta.web.rest;

import florinl.licenta.domain.UserSignal;
import florinl.licenta.repository.UserSignalRepository;
import florinl.licenta.security.AuthoritiesConstants;
import florinl.licenta.security.SecurityUtils;
import florinl.licenta.service.UserSignalService;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link florinl.licenta.domain.UserSignal}.
 */
@RestController
@RequestMapping("/api/user-signals")
public class UserSignalResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserSignalResource.class);

    private static final String ENTITY_NAME = "userSignal";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserSignalService userSignalService;

    private final UserSignalRepository userSignalRepository;

    public UserSignalResource(UserSignalService userSignalService, UserSignalRepository userSignalRepository) {
        this.userSignalService = userSignalService;
        this.userSignalRepository = userSignalRepository;
    }

    /**
     * {@code POST  /user-signals} : Create a new userSignal.
     *
     * @param userSignal the userSignal to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userSignal, or with status {@code 400 (Bad Request)} if the userSignal has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<UserSignal> createUserSignal(@Valid @RequestBody UserSignal userSignal) throws URISyntaxException {
        LOG.debug("REST request to save UserSignal : {}", userSignal);
        if (userSignal.getId() != null) {
            throw new BadRequestAlertException("A new userSignal cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userSignal = userSignalService.save(userSignal);
        return ResponseEntity.created(new URI("/api/user-signals/" + userSignal.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, userSignal.getId().toString()))
            .body(userSignal);
    }

    /**
     * {@code PUT  /user-signals/:id} : Updates an existing userSignal.
     *
     * @param id the id of the userSignal to save.
     * @param userSignal the userSignal to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userSignal,
     * or with status {@code 400 (Bad Request)} if the userSignal is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userSignal couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<UserSignal> updateUserSignal(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserSignal userSignal
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserSignal : {}, {}", id, userSignal);
        if (userSignal.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userSignal.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userSignalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userSignal = userSignalService.update(userSignal);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userSignal.getId().toString()))
            .body(userSignal);
    }

    /**
     * {@code PATCH  /user-signals/:id} : Partial updates given fields of an existing userSignal, field will ignore if it is null
     *
     * @param id the id of the userSignal to save.
     * @param userSignal the userSignal to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userSignal,
     * or with status {@code 400 (Bad Request)} if the userSignal is not valid,
     * or with status {@code 404 (Not Found)} if the userSignal is not found,
     * or with status {@code 500 (Internal Server Error)} if the userSignal couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<UserSignal> partialUpdateUserSignal(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserSignal userSignal
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserSignal partially : {}, {}", id, userSignal);
        if (userSignal.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userSignal.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userSignalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserSignal> result = userSignalService.partialUpdate(userSignal);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userSignal.getId().toString())
        );
    }

    /**
     * {@code GET  /user-signals} : get all the userSignals.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userSignals in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UserSignal>> getAllUserSignals(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of UserSignals");
        Page<UserSignal> page;

        // 1. Admins see the full paginated list
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            // Note: If your app uses userSignalService here, keep it as userSignalService.findAll(pageable)
            page = userSignalRepository.findAll(pageable);
        }
        // 2. Regular users strictly hit the isolated query
        else {
            page = userSignalRepository.findByUserIsCurrentUser(pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-signals/:id} : get the "id" userSignal.
     *
     * @param id the id of the userSignal to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userSignal, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserSignal> getUserSignal(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserSignal : {}", id);

        // Change userSignalService to userSignalRepository if you aren't using a Service layer here!
        Optional<UserSignal> userSignal = userSignalService.findOne(id);

        // --- IDOR PROTECTION ---
        if (userSignal.isPresent() && !SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            String currentLogin = SecurityUtils.getCurrentUserLogin().orElse("");

            String ownerLogin = "";
            if (userSignal.get().getUser() != null) {
                ownerLogin = userSignal.get().getUser().getLogin();
            }

            // If the user doesn't own it, wipe it out so it returns a 404
            if (!currentLogin.equals(ownerLogin)) {
                LOG.warn("IDOR attempt: User {} tried to view signal {} belonging to {}", currentLogin, id, ownerLogin);
                userSignal = Optional.empty();
            }
        }
        // -----------------------

        return ResponseUtil.wrapOrNotFound(userSignal);
    }

    /**
     * {@code DELETE  /user-signals/:id} : delete the "id" userSignal.
     *
     * @param id the id of the userSignal to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserSignal(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserSignal : {}", id);

        // --- IDOR PROTECTION ---
        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            // Retrieve the signal from the database first to check ownership
            Optional<UserSignal> userSignal = userSignalService.findOne(id);

            if (userSignal.isPresent()) {
                String currentLogin = SecurityUtils.getCurrentUserLogin().orElse("");

                String ownerLogin = "";
                if (userSignal.get().getUser() != null) {
                    ownerLogin = userSignal.get().getUser().getLogin();
                }

                // If they don't match, block the deletion completely!
                if (!currentLogin.equals(ownerLogin)) {
                    LOG.warn("CRITICAL: User {} attempted to delete signal {} belonging to {}", currentLogin, id, ownerLogin);
                    throw new AccessDeniedException("You do not have permission to delete this signal.");
                }
            }
        }
        // -----------------------

        // If they pass the check (or if they are an Admin), proceed with deletion
        userSignalService.delete(id);

        return ResponseEntity.noContent()
            // Keep whatever HeaderUtil your JHipster generated here:
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
