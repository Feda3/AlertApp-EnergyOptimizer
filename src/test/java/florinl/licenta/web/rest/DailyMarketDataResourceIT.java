package florinl.licenta.web.rest;

import static florinl.licenta.domain.DailyMarketDataAsserts.*;
import static florinl.licenta.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import florinl.licenta.IntegrationTest;
import florinl.licenta.domain.DailyMarketData;
import florinl.licenta.repository.DailyMarketDataRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DailyMarketDataResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DailyMarketDataResourceIT {

    private static final LocalDate DEFAULT_FETCH_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FETCH_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_SYMBOL = "AAAAAAAAAA";
    private static final String UPDATED_SYMBOL = "BBBBBBBBBB";

    private static final Double DEFAULT_METRIC_VALUE = 1D;
    private static final Double UPDATED_METRIC_VALUE = 2D;

    private static final String ENTITY_API_URL = "/api/daily-market-data";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DailyMarketDataRepository dailyMarketDataRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDailyMarketDataMockMvc;

    private DailyMarketData dailyMarketData;

    private DailyMarketData insertedDailyMarketData;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DailyMarketData createEntity() {
        return new DailyMarketData().fetchDate(DEFAULT_FETCH_DATE).symbol(DEFAULT_SYMBOL).metricValue(DEFAULT_METRIC_VALUE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DailyMarketData createUpdatedEntity() {
        return new DailyMarketData().fetchDate(UPDATED_FETCH_DATE).symbol(UPDATED_SYMBOL).metricValue(UPDATED_METRIC_VALUE);
    }

    @BeforeEach
    void initTest() {
        dailyMarketData = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDailyMarketData != null) {
            dailyMarketDataRepository.delete(insertedDailyMarketData);
            insertedDailyMarketData = null;
        }
    }

    @Test
    @Transactional
    void createDailyMarketData() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DailyMarketData
        var returnedDailyMarketData = om.readValue(
            restDailyMarketDataMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyMarketData)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DailyMarketData.class
        );

        // Validate the DailyMarketData in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDailyMarketDataUpdatableFieldsEquals(returnedDailyMarketData, getPersistedDailyMarketData(returnedDailyMarketData));

        insertedDailyMarketData = returnedDailyMarketData;
    }

    @Test
    @Transactional
    void createDailyMarketDataWithExistingId() throws Exception {
        // Create the DailyMarketData with an existing ID
        dailyMarketData.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDailyMarketDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyMarketData)))
            .andExpect(status().isBadRequest());

        // Validate the DailyMarketData in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFetchDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dailyMarketData.setFetchDate(null);

        // Create the DailyMarketData, which fails.

        restDailyMarketDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyMarketData)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSymbolIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dailyMarketData.setSymbol(null);

        // Create the DailyMarketData, which fails.

        restDailyMarketDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyMarketData)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMetricValueIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dailyMarketData.setMetricValue(null);

        // Create the DailyMarketData, which fails.

        restDailyMarketDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyMarketData)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDailyMarketData() throws Exception {
        // Initialize the database
        insertedDailyMarketData = dailyMarketDataRepository.saveAndFlush(dailyMarketData);

        // Get all the dailyMarketDataList
        restDailyMarketDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dailyMarketData.getId().intValue())))
            .andExpect(jsonPath("$.[*].fetchDate").value(hasItem(DEFAULT_FETCH_DATE.toString())))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL)))
            .andExpect(jsonPath("$.[*].metricValue").value(hasItem(DEFAULT_METRIC_VALUE)));
    }

    @Test
    @Transactional
    void getDailyMarketData() throws Exception {
        // Initialize the database
        insertedDailyMarketData = dailyMarketDataRepository.saveAndFlush(dailyMarketData);

        // Get the dailyMarketData
        restDailyMarketDataMockMvc
            .perform(get(ENTITY_API_URL_ID, dailyMarketData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dailyMarketData.getId().intValue()))
            .andExpect(jsonPath("$.fetchDate").value(DEFAULT_FETCH_DATE.toString()))
            .andExpect(jsonPath("$.symbol").value(DEFAULT_SYMBOL))
            .andExpect(jsonPath("$.metricValue").value(DEFAULT_METRIC_VALUE));
    }

    @Test
    @Transactional
    void getNonExistingDailyMarketData() throws Exception {
        // Get the dailyMarketData
        restDailyMarketDataMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDailyMarketData() throws Exception {
        // Initialize the database
        insertedDailyMarketData = dailyMarketDataRepository.saveAndFlush(dailyMarketData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dailyMarketData
        DailyMarketData updatedDailyMarketData = dailyMarketDataRepository.findById(dailyMarketData.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDailyMarketData are not directly saved in db
        em.detach(updatedDailyMarketData);
        updatedDailyMarketData.fetchDate(UPDATED_FETCH_DATE).symbol(UPDATED_SYMBOL).metricValue(UPDATED_METRIC_VALUE);

        restDailyMarketDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDailyMarketData.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedDailyMarketData))
            )
            .andExpect(status().isOk());

        // Validate the DailyMarketData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDailyMarketDataToMatchAllProperties(updatedDailyMarketData);
    }

    @Test
    @Transactional
    void putNonExistingDailyMarketData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyMarketData.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDailyMarketDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dailyMarketData.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(dailyMarketData))
            )
            .andExpect(status().isBadRequest());

        // Validate the DailyMarketData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDailyMarketData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyMarketData.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDailyMarketDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(dailyMarketData))
            )
            .andExpect(status().isBadRequest());

        // Validate the DailyMarketData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDailyMarketData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyMarketData.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDailyMarketDataMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dailyMarketData)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DailyMarketData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDailyMarketDataWithPatch() throws Exception {
        // Initialize the database
        insertedDailyMarketData = dailyMarketDataRepository.saveAndFlush(dailyMarketData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dailyMarketData using partial update
        DailyMarketData partialUpdatedDailyMarketData = new DailyMarketData();
        partialUpdatedDailyMarketData.setId(dailyMarketData.getId());

        partialUpdatedDailyMarketData.symbol(UPDATED_SYMBOL);

        restDailyMarketDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDailyMarketData.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDailyMarketData))
            )
            .andExpect(status().isOk());

        // Validate the DailyMarketData in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDailyMarketDataUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDailyMarketData, dailyMarketData),
            getPersistedDailyMarketData(dailyMarketData)
        );
    }

    @Test
    @Transactional
    void fullUpdateDailyMarketDataWithPatch() throws Exception {
        // Initialize the database
        insertedDailyMarketData = dailyMarketDataRepository.saveAndFlush(dailyMarketData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dailyMarketData using partial update
        DailyMarketData partialUpdatedDailyMarketData = new DailyMarketData();
        partialUpdatedDailyMarketData.setId(dailyMarketData.getId());

        partialUpdatedDailyMarketData.fetchDate(UPDATED_FETCH_DATE).symbol(UPDATED_SYMBOL).metricValue(UPDATED_METRIC_VALUE);

        restDailyMarketDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDailyMarketData.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDailyMarketData))
            )
            .andExpect(status().isOk());

        // Validate the DailyMarketData in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDailyMarketDataUpdatableFieldsEquals(
            partialUpdatedDailyMarketData,
            getPersistedDailyMarketData(partialUpdatedDailyMarketData)
        );
    }

    @Test
    @Transactional
    void patchNonExistingDailyMarketData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyMarketData.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDailyMarketDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dailyMarketData.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(dailyMarketData))
            )
            .andExpect(status().isBadRequest());

        // Validate the DailyMarketData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDailyMarketData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyMarketData.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDailyMarketDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(dailyMarketData))
            )
            .andExpect(status().isBadRequest());

        // Validate the DailyMarketData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDailyMarketData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dailyMarketData.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDailyMarketDataMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(dailyMarketData)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DailyMarketData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDailyMarketData() throws Exception {
        // Initialize the database
        insertedDailyMarketData = dailyMarketDataRepository.saveAndFlush(dailyMarketData);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the dailyMarketData
        restDailyMarketDataMockMvc
            .perform(delete(ENTITY_API_URL_ID, dailyMarketData.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return dailyMarketDataRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected DailyMarketData getPersistedDailyMarketData(DailyMarketData dailyMarketData) {
        return dailyMarketDataRepository.findById(dailyMarketData.getId()).orElseThrow();
    }

    protected void assertPersistedDailyMarketDataToMatchAllProperties(DailyMarketData expectedDailyMarketData) {
        assertDailyMarketDataAllPropertiesEquals(expectedDailyMarketData, getPersistedDailyMarketData(expectedDailyMarketData));
    }

    protected void assertPersistedDailyMarketDataToMatchUpdatableProperties(DailyMarketData expectedDailyMarketData) {
        assertDailyMarketDataAllUpdatablePropertiesEquals(expectedDailyMarketData, getPersistedDailyMarketData(expectedDailyMarketData));
    }
}
