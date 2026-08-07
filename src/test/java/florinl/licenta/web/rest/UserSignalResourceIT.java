package florinl.licenta.web.rest;

import static florinl.licenta.domain.UserSignalAsserts.*;
import static florinl.licenta.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import florinl.licenta.IntegrationTest;
import florinl.licenta.domain.UserSignal;
import florinl.licenta.domain.enumeration.AlertAction;
import florinl.licenta.repository.UserRepository;
import florinl.licenta.repository.UserSignalRepository;
import florinl.licenta.service.UserSignalService;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link UserSignalResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserSignalResourceIT {

    private static final LocalDate DEFAULT_SIGNAL_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SIGNAL_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final AlertAction DEFAULT_ACTION = AlertAction.BUY;
    private static final AlertAction UPDATED_ACTION = AlertAction.SELL;

    private static final String DEFAULT_SUMMARY_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_SUMMARY_MESSAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-signals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserSignalRepository userSignalRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private UserSignalRepository userSignalRepositoryMock;

    @Mock
    private UserSignalService userSignalServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserSignalMockMvc;

    private UserSignal userSignal;

    private UserSignal insertedUserSignal;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserSignal createEntity() {
        return new UserSignal().signalDate(DEFAULT_SIGNAL_DATE).action(DEFAULT_ACTION).summaryMessage(DEFAULT_SUMMARY_MESSAGE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserSignal createUpdatedEntity() {
        return new UserSignal().signalDate(UPDATED_SIGNAL_DATE).action(UPDATED_ACTION).summaryMessage(UPDATED_SUMMARY_MESSAGE);
    }

    @BeforeEach
    void initTest() {
        userSignal = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUserSignal != null) {
            userSignalRepository.delete(insertedUserSignal);
            insertedUserSignal = null;
        }
    }

    @Test
    @Transactional
    void createUserSignal() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserSignal
        var returnedUserSignal = om.readValue(
            restUserSignalMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userSignal)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserSignal.class
        );

        // Validate the UserSignal in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertUserSignalUpdatableFieldsEquals(returnedUserSignal, getPersistedUserSignal(returnedUserSignal));

        insertedUserSignal = returnedUserSignal;
    }

    @Test
    @Transactional
    void createUserSignalWithExistingId() throws Exception {
        // Create the UserSignal with an existing ID
        userSignal.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserSignalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userSignal)))
            .andExpect(status().isBadRequest());

        // Validate the UserSignal in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSignalDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userSignal.setSignalDate(null);

        // Create the UserSignal, which fails.

        restUserSignalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userSignal)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userSignal.setAction(null);

        // Create the UserSignal, which fails.

        restUserSignalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userSignal)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserSignals() throws Exception {
        // Initialize the database
        insertedUserSignal = userSignalRepository.saveAndFlush(userSignal);

        // Get all the userSignalList
        restUserSignalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userSignal.getId().intValue())))
            .andExpect(jsonPath("$.[*].signalDate").value(hasItem(DEFAULT_SIGNAL_DATE.toString())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION.toString())))
            .andExpect(jsonPath("$.[*].summaryMessage").value(hasItem(DEFAULT_SUMMARY_MESSAGE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserSignalsWithEagerRelationshipsIsEnabled() throws Exception {
        when(userSignalServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserSignalMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(userSignalServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserSignalsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(userSignalServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserSignalMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(userSignalRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getUserSignal() throws Exception {
        // Initialize the database
        insertedUserSignal = userSignalRepository.saveAndFlush(userSignal);

        // Get the userSignal
        restUserSignalMockMvc
            .perform(get(ENTITY_API_URL_ID, userSignal.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userSignal.getId().intValue()))
            .andExpect(jsonPath("$.signalDate").value(DEFAULT_SIGNAL_DATE.toString()))
            .andExpect(jsonPath("$.action").value(DEFAULT_ACTION.toString()))
            .andExpect(jsonPath("$.summaryMessage").value(DEFAULT_SUMMARY_MESSAGE));
    }

    @Test
    @Transactional
    void getNonExistingUserSignal() throws Exception {
        // Get the userSignal
        restUserSignalMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserSignal() throws Exception {
        // Initialize the database
        insertedUserSignal = userSignalRepository.saveAndFlush(userSignal);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userSignal
        UserSignal updatedUserSignal = userSignalRepository.findById(userSignal.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserSignal are not directly saved in db
        em.detach(updatedUserSignal);
        updatedUserSignal.signalDate(UPDATED_SIGNAL_DATE).action(UPDATED_ACTION).summaryMessage(UPDATED_SUMMARY_MESSAGE);

        restUserSignalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserSignal.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedUserSignal))
            )
            .andExpect(status().isOk());

        // Validate the UserSignal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserSignalToMatchAllProperties(updatedUserSignal);
    }

    @Test
    @Transactional
    void putNonExistingUserSignal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userSignal.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserSignalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userSignal.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userSignal))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserSignal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserSignal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userSignal.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserSignalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userSignal))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserSignal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserSignal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userSignal.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserSignalMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userSignal)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserSignal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserSignalWithPatch() throws Exception {
        // Initialize the database
        insertedUserSignal = userSignalRepository.saveAndFlush(userSignal);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userSignal using partial update
        UserSignal partialUpdatedUserSignal = new UserSignal();
        partialUpdatedUserSignal.setId(userSignal.getId());

        partialUpdatedUserSignal.signalDate(UPDATED_SIGNAL_DATE).summaryMessage(UPDATED_SUMMARY_MESSAGE);

        restUserSignalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserSignal.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserSignal))
            )
            .andExpect(status().isOk());

        // Validate the UserSignal in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserSignalUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserSignal, userSignal),
            getPersistedUserSignal(userSignal)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserSignalWithPatch() throws Exception {
        // Initialize the database
        insertedUserSignal = userSignalRepository.saveAndFlush(userSignal);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userSignal using partial update
        UserSignal partialUpdatedUserSignal = new UserSignal();
        partialUpdatedUserSignal.setId(userSignal.getId());

        partialUpdatedUserSignal.signalDate(UPDATED_SIGNAL_DATE).action(UPDATED_ACTION).summaryMessage(UPDATED_SUMMARY_MESSAGE);

        restUserSignalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserSignal.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserSignal))
            )
            .andExpect(status().isOk());

        // Validate the UserSignal in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserSignalUpdatableFieldsEquals(partialUpdatedUserSignal, getPersistedUserSignal(partialUpdatedUserSignal));
    }

    @Test
    @Transactional
    void patchNonExistingUserSignal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userSignal.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserSignalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userSignal.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userSignal))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserSignal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserSignal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userSignal.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserSignalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userSignal))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserSignal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserSignal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userSignal.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserSignalMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userSignal)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserSignal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserSignal() throws Exception {
        // Initialize the database
        insertedUserSignal = userSignalRepository.saveAndFlush(userSignal);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userSignal
        restUserSignalMockMvc
            .perform(delete(ENTITY_API_URL_ID, userSignal.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userSignalRepository.count();
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

    protected UserSignal getPersistedUserSignal(UserSignal userSignal) {
        return userSignalRepository.findById(userSignal.getId()).orElseThrow();
    }

    protected void assertPersistedUserSignalToMatchAllProperties(UserSignal expectedUserSignal) {
        assertUserSignalAllPropertiesEquals(expectedUserSignal, getPersistedUserSignal(expectedUserSignal));
    }

    protected void assertPersistedUserSignalToMatchUpdatableProperties(UserSignal expectedUserSignal) {
        assertUserSignalAllUpdatablePropertiesEquals(expectedUserSignal, getPersistedUserSignal(expectedUserSignal));
    }
}
