package florinl.licenta.web.rest;

import static florinl.licenta.domain.UserAlertSettingsAsserts.*;
import static florinl.licenta.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import florinl.licenta.IntegrationTest;
import florinl.licenta.domain.UserAlertSettings;
import florinl.licenta.domain.enumeration.AlertAction;
import florinl.licenta.repository.UserAlertSettingsRepository;
import florinl.licenta.repository.UserRepository;
import florinl.licenta.service.UserAlertSettingsService;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UserAlertSettingsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserAlertSettingsResourceIT {

    private static final String DEFAULT_SYMBOL = "AAAAAAAAAA";
    private static final String UPDATED_SYMBOL = "BBBBBBBBBB";

    private static final Double DEFAULT_THRESHOLD = 1D;
    private static final Double UPDATED_THRESHOLD = 2D;

    private static final Boolean DEFAULT_TRIGGER_IF_GREATER = false;
    private static final Boolean UPDATED_TRIGGER_IF_GREATER = true;

    private static final AlertAction DEFAULT_ACTION = AlertAction.BUY;
    private static final AlertAction UPDATED_ACTION = AlertAction.SELL;

    private static final String DEFAULT_START_TIME = "AAAAAAAAAA";
    private static final String UPDATED_START_TIME = "BBBBBBBBBB";

    private static final String DEFAULT_END_TIME = "AAAAAAAAAA";
    private static final String UPDATED_END_TIME = "BBBBBBBBBB";

    private static final Integer DEFAULT_MIN_DURATION_MINUTES = 1;
    private static final Integer UPDATED_MIN_DURATION_MINUTES = 2;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/user-alert-settings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserAlertSettingsRepository userAlertSettingsRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private UserAlertSettingsRepository userAlertSettingsRepositoryMock;

    @Mock
    private UserAlertSettingsService userAlertSettingsServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserAlertSettingsMockMvc;

    private UserAlertSettings userAlertSettings;

    private UserAlertSettings insertedUserAlertSettings;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAlertSettings createEntity() {
        return new UserAlertSettings()
            .symbol(DEFAULT_SYMBOL)
            .threshold(DEFAULT_THRESHOLD)
            .triggerIfGreater(DEFAULT_TRIGGER_IF_GREATER)
            .action(DEFAULT_ACTION)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .minDurationMinutes(DEFAULT_MIN_DURATION_MINUTES)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAlertSettings createUpdatedEntity() {
        return new UserAlertSettings()
            .symbol(UPDATED_SYMBOL)
            .threshold(UPDATED_THRESHOLD)
            .triggerIfGreater(UPDATED_TRIGGER_IF_GREATER)
            .action(UPDATED_ACTION)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .minDurationMinutes(UPDATED_MIN_DURATION_MINUTES)
            .isActive(UPDATED_IS_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        userAlertSettings = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUserAlertSettings != null) {
            userAlertSettingsRepository.delete(insertedUserAlertSettings);
            insertedUserAlertSettings = null;
        }
    }

    @Test
    @Transactional
    void createUserAlertSettings() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserAlertSettings
        var returnedUserAlertSettings = om.readValue(
            restUserAlertSettingsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAlertSettings)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserAlertSettings.class
        );

        // Validate the UserAlertSettings in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertUserAlertSettingsUpdatableFieldsEquals(returnedUserAlertSettings, getPersistedUserAlertSettings(returnedUserAlertSettings));

        insertedUserAlertSettings = returnedUserAlertSettings;
    }

    @Test
    @Transactional
    void createUserAlertSettingsWithExistingId() throws Exception {
        // Create the UserAlertSettings with an existing ID
        userAlertSettings.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserAlertSettingsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAlertSettings)))
            .andExpect(status().isBadRequest());

        // Validate the UserAlertSettings in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSymbolIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userAlertSettings.setSymbol(null);

        // Create the UserAlertSettings, which fails.

        restUserAlertSettingsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAlertSettings)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkThresholdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userAlertSettings.setThreshold(null);

        // Create the UserAlertSettings, which fails.

        restUserAlertSettingsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAlertSettings)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTriggerIfGreaterIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userAlertSettings.setTriggerIfGreater(null);

        // Create the UserAlertSettings, which fails.

        restUserAlertSettingsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAlertSettings)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userAlertSettings.setAction(null);

        // Create the UserAlertSettings, which fails.

        restUserAlertSettingsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAlertSettings)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserAlertSettings() throws Exception {
        // Initialize the database
        insertedUserAlertSettings = userAlertSettingsRepository.saveAndFlush(userAlertSettings);

        // Get all the userAlertSettingsList
        restUserAlertSettingsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userAlertSettings.getId().intValue())))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL)))
            .andExpect(jsonPath("$.[*].threshold").value(hasItem(DEFAULT_THRESHOLD)))
            .andExpect(jsonPath("$.[*].triggerIfGreater").value(hasItem(DEFAULT_TRIGGER_IF_GREATER)))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME)))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME)))
            .andExpect(jsonPath("$.[*].minDurationMinutes").value(hasItem(DEFAULT_MIN_DURATION_MINUTES)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserAlertSettingsWithEagerRelationshipsIsEnabled() throws Exception {
        when(userAlertSettingsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserAlertSettingsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(userAlertSettingsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserAlertSettingsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(userAlertSettingsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserAlertSettingsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(userAlertSettingsRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getUserAlertSettings() throws Exception {
        // Initialize the database
        insertedUserAlertSettings = userAlertSettingsRepository.saveAndFlush(userAlertSettings);

        // Get the userAlertSettings
        restUserAlertSettingsMockMvc
            .perform(get(ENTITY_API_URL_ID, userAlertSettings.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userAlertSettings.getId().intValue()))
            .andExpect(jsonPath("$.symbol").value(DEFAULT_SYMBOL))
            .andExpect(jsonPath("$.threshold").value(DEFAULT_THRESHOLD))
            .andExpect(jsonPath("$.triggerIfGreater").value(DEFAULT_TRIGGER_IF_GREATER))
            .andExpect(jsonPath("$.action").value(DEFAULT_ACTION.toString()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME))
            .andExpect(jsonPath("$.minDurationMinutes").value(DEFAULT_MIN_DURATION_MINUTES))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE));
    }

    @Test
    @Transactional
    void getNonExistingUserAlertSettings() throws Exception {
        // Get the userAlertSettings
        restUserAlertSettingsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserAlertSettings() throws Exception {
        // Initialize the database
        insertedUserAlertSettings = userAlertSettingsRepository.saveAndFlush(userAlertSettings);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userAlertSettings
        UserAlertSettings updatedUserAlertSettings = userAlertSettingsRepository.findById(userAlertSettings.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserAlertSettings are not directly saved in db
        em.detach(updatedUserAlertSettings);
        updatedUserAlertSettings
            .symbol(UPDATED_SYMBOL)
            .threshold(UPDATED_THRESHOLD)
            .triggerIfGreater(UPDATED_TRIGGER_IF_GREATER)
            .action(UPDATED_ACTION)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .minDurationMinutes(UPDATED_MIN_DURATION_MINUTES)
            .isActive(UPDATED_IS_ACTIVE);

        restUserAlertSettingsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserAlertSettings.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedUserAlertSettings))
            )
            .andExpect(status().isOk());

        // Validate the UserAlertSettings in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserAlertSettingsToMatchAllProperties(updatedUserAlertSettings);
    }

    @Test
    @Transactional
    void putNonExistingUserAlertSettings() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAlertSettings.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserAlertSettingsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userAlertSettings.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userAlertSettings))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAlertSettings in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserAlertSettings() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAlertSettings.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAlertSettingsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userAlertSettings))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAlertSettings in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserAlertSettings() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAlertSettings.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAlertSettingsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAlertSettings)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserAlertSettings in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserAlertSettingsWithPatch() throws Exception {
        // Initialize the database
        insertedUserAlertSettings = userAlertSettingsRepository.saveAndFlush(userAlertSettings);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userAlertSettings using partial update
        UserAlertSettings partialUpdatedUserAlertSettings = new UserAlertSettings();
        partialUpdatedUserAlertSettings.setId(userAlertSettings.getId());

        partialUpdatedUserAlertSettings
            .symbol(UPDATED_SYMBOL)
            .action(UPDATED_ACTION)
            .endTime(UPDATED_END_TIME)
            .minDurationMinutes(UPDATED_MIN_DURATION_MINUTES);

        restUserAlertSettingsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserAlertSettings.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserAlertSettings))
            )
            .andExpect(status().isOk());

        // Validate the UserAlertSettings in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserAlertSettingsUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserAlertSettings, userAlertSettings),
            getPersistedUserAlertSettings(userAlertSettings)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserAlertSettingsWithPatch() throws Exception {
        // Initialize the database
        insertedUserAlertSettings = userAlertSettingsRepository.saveAndFlush(userAlertSettings);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userAlertSettings using partial update
        UserAlertSettings partialUpdatedUserAlertSettings = new UserAlertSettings();
        partialUpdatedUserAlertSettings.setId(userAlertSettings.getId());

        partialUpdatedUserAlertSettings
            .symbol(UPDATED_SYMBOL)
            .threshold(UPDATED_THRESHOLD)
            .triggerIfGreater(UPDATED_TRIGGER_IF_GREATER)
            .action(UPDATED_ACTION)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .minDurationMinutes(UPDATED_MIN_DURATION_MINUTES)
            .isActive(UPDATED_IS_ACTIVE);

        restUserAlertSettingsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserAlertSettings.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserAlertSettings))
            )
            .andExpect(status().isOk());

        // Validate the UserAlertSettings in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserAlertSettingsUpdatableFieldsEquals(
            partialUpdatedUserAlertSettings,
            getPersistedUserAlertSettings(partialUpdatedUserAlertSettings)
        );
    }

    @Test
    @Transactional
    void patchNonExistingUserAlertSettings() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAlertSettings.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserAlertSettingsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userAlertSettings.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userAlertSettings))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAlertSettings in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserAlertSettings() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAlertSettings.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAlertSettingsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userAlertSettings))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAlertSettings in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserAlertSettings() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAlertSettings.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAlertSettingsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userAlertSettings)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserAlertSettings in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserAlertSettings() throws Exception {
        // Initialize the database
        insertedUserAlertSettings = userAlertSettingsRepository.saveAndFlush(userAlertSettings);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userAlertSettings
        restUserAlertSettingsMockMvc
            .perform(delete(ENTITY_API_URL_ID, userAlertSettings.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userAlertSettingsRepository.count();
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

    protected UserAlertSettings getPersistedUserAlertSettings(UserAlertSettings userAlertSettings) {
        return userAlertSettingsRepository.findById(userAlertSettings.getId()).orElseThrow();
    }

    protected void assertPersistedUserAlertSettingsToMatchAllProperties(UserAlertSettings expectedUserAlertSettings) {
        assertUserAlertSettingsAllPropertiesEquals(expectedUserAlertSettings, getPersistedUserAlertSettings(expectedUserAlertSettings));
    }

    protected void assertPersistedUserAlertSettingsToMatchUpdatableProperties(UserAlertSettings expectedUserAlertSettings) {
        assertUserAlertSettingsAllUpdatablePropertiesEquals(
            expectedUserAlertSettings,
            getPersistedUserAlertSettings(expectedUserAlertSettings)
        );
    }
}
