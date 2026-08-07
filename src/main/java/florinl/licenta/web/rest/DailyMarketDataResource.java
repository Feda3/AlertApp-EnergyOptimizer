package florinl.licenta.web.rest;

import florinl.licenta.domain.DailyMarketData;
import florinl.licenta.repository.DailyMarketDataRepository;
import florinl.licenta.security.AuthoritiesConstants;
import florinl.licenta.service.DailyMarketDataService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link florinl.licenta.domain.DailyMarketData}.
 */
@RestController
@RequestMapping("/api/daily-market-data")
public class DailyMarketDataResource {

    private static final Logger LOG = LoggerFactory.getLogger(DailyMarketDataResource.class);

    private static final String ENTITY_NAME = "dailyMarketData";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DailyMarketDataService dailyMarketDataService;

    private final DailyMarketDataRepository dailyMarketDataRepository;

    public DailyMarketDataResource(DailyMarketDataService dailyMarketDataService, DailyMarketDataRepository dailyMarketDataRepository) {
        this.dailyMarketDataService = dailyMarketDataService;
        this.dailyMarketDataRepository = dailyMarketDataRepository;
    }

    /**
     * {@code POST  /daily-market-data} : Create a new dailyMarketData.
     *
     * @param dailyMarketData the dailyMarketData to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dailyMarketData, or with status {@code 400 (Bad Request)} if the dailyMarketData has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<DailyMarketData> createDailyMarketData(@Valid @RequestBody DailyMarketData dailyMarketData)
        throws URISyntaxException {
        LOG.debug("REST request to save DailyMarketData : {}", dailyMarketData);
        if (dailyMarketData.getId() != null) {
            throw new BadRequestAlertException("A new dailyMarketData cannot already have an ID", ENTITY_NAME, "idexists");
        }
        dailyMarketData = dailyMarketDataService.save(dailyMarketData);
        return ResponseEntity.created(new URI("/api/daily-market-data/" + dailyMarketData.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, dailyMarketData.getId().toString()))
            .body(dailyMarketData);
    }

    /**
     * {@code PUT  /daily-market-data/:id} : Updates an existing dailyMarketData.
     *
     * @param id the id of the dailyMarketData to save.
     * @param dailyMarketData the dailyMarketData to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dailyMarketData,
     * or with status {@code 400 (Bad Request)} if the dailyMarketData is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dailyMarketData couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<DailyMarketData> updateDailyMarketData(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DailyMarketData dailyMarketData
    ) throws URISyntaxException {
        LOG.debug("REST request to update DailyMarketData : {}, {}", id, dailyMarketData);
        if (dailyMarketData.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dailyMarketData.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dailyMarketDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        dailyMarketData = dailyMarketDataService.update(dailyMarketData);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dailyMarketData.getId().toString()))
            .body(dailyMarketData);
    }

    /**
     * {@code PATCH  /daily-market-data/:id} : Partial updates given fields of an existing dailyMarketData, field will ignore if it is null
     *
     * @param id the id of the dailyMarketData to save.
     * @param dailyMarketData the dailyMarketData to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dailyMarketData,
     * or with status {@code 400 (Bad Request)} if the dailyMarketData is not valid,
     * or with status {@code 404 (Not Found)} if the dailyMarketData is not found,
     * or with status {@code 500 (Internal Server Error)} if the dailyMarketData couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<DailyMarketData> partialUpdateDailyMarketData(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DailyMarketData dailyMarketData
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DailyMarketData partially : {}, {}", id, dailyMarketData);
        if (dailyMarketData.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dailyMarketData.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dailyMarketDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DailyMarketData> result = dailyMarketDataService.partialUpdate(dailyMarketData);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dailyMarketData.getId().toString())
        );
    }

    /**
     * {@code GET  /daily-market-data} : get all the dailyMarketData.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dailyMarketData in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DailyMarketData>> getAllDailyMarketData(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of DailyMarketData");
        Page<DailyMarketData> page = dailyMarketDataService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /daily-market-data/:id} : get the "id" dailyMarketData.
     *
     * @param id the id of the dailyMarketData to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dailyMarketData, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DailyMarketData> getDailyMarketData(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DailyMarketData : {}", id);
        Optional<DailyMarketData> dailyMarketData = dailyMarketDataService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dailyMarketData);
    }

    /**
     * {@code DELETE  /daily-market-data/:id} : delete the "id" dailyMarketData.
     *
     * @param id the id of the dailyMarketData to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteDailyMarketData(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DailyMarketData : {}", id);
        dailyMarketDataService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
