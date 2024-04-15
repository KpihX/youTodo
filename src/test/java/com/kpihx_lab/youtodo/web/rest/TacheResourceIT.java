package com.kpihx_lab.youtodo.web.rest;

import static com.kpihx_lab.youtodo.domain.TacheAsserts.*;
import static com.kpihx_lab.youtodo.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpihx_lab.youtodo.IntegrationTest;
import com.kpihx_lab.youtodo.domain.Tache;
import com.kpihx_lab.youtodo.domain.enumeration.Priorite;
import com.kpihx_lab.youtodo.domain.enumeration.StatutTache;
import com.kpihx_lab.youtodo.repository.EntityManager;
import com.kpihx_lab.youtodo.repository.TacheRepository;
import com.kpihx_lab.youtodo.service.TacheService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link TacheResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TacheResourceIT {

    private static final String DEFAULT_TITRE = "AAAAAAAAAA";
    private static final String UPDATED_TITRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_ECHEANCE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_ECHEANCE = LocalDate.now(ZoneId.systemDefault());

    private static final Priorite DEFAULT_PRIORITE = Priorite.BASSE;
    private static final Priorite UPDATED_PRIORITE = Priorite.MOYENNE;

    private static final StatutTache DEFAULT_STATUT = StatutTache.OUVERT;
    private static final StatutTache UPDATED_STATUT = StatutTache.EN_COURS;

    private static final String ENTITY_API_URL = "/api/taches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TacheRepository tacheRepository;

    @Mock
    private TacheRepository tacheRepositoryMock;

    @Mock
    private TacheService tacheServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Tache tache;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tache createEntity(EntityManager em) {
        Tache tache = new Tache()
            .titre(DEFAULT_TITRE)
            .description(DEFAULT_DESCRIPTION)
            .dateEcheance(DEFAULT_DATE_ECHEANCE)
            .priorite(DEFAULT_PRIORITE)
            .statut(DEFAULT_STATUT);
        return tache;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tache createUpdatedEntity(EntityManager em) {
        Tache tache = new Tache()
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .dateEcheance(UPDATED_DATE_ECHEANCE)
            .priorite(UPDATED_PRIORITE)
            .statut(UPDATED_STATUT);
        return tache;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Tache.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        tache = createEntity(em);
    }

    @Test
    void createTache() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Tache
        var returnedTache = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tache))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Tache.class)
            .returnResult()
            .getResponseBody();

        // Validate the Tache in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTacheUpdatableFieldsEquals(returnedTache, getPersistedTache(returnedTache));
    }

    @Test
    void createTacheWithExistingId() throws Exception {
        // Create the Tache with an existing ID
        tache.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tache))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tache in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tache.setTitre(null);

        // Create the Tache, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tache))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDateEcheanceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tache.setDateEcheance(null);

        // Create the Tache, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tache))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPrioriteIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tache.setPriorite(null);

        // Create the Tache, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tache))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tache.setStatut(null);

        // Create the Tache, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tache))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTaches() {
        // Initialize the database
        tacheRepository.save(tache).block();

        // Get all the tacheList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(tache.getId().intValue()))
            .jsonPath("$.[*].titre")
            .value(hasItem(DEFAULT_TITRE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].dateEcheance")
            .value(hasItem(DEFAULT_DATE_ECHEANCE.toString()))
            .jsonPath("$.[*].priorite")
            .value(hasItem(DEFAULT_PRIORITE.toString()))
            .jsonPath("$.[*].statut")
            .value(hasItem(DEFAULT_STATUT.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTachesWithEagerRelationshipsIsEnabled() {
        when(tacheServiceMock.findAllWithEagerRelationships(any(), any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(tacheServiceMock, times(1)).findAllWithEagerRelationships(any(), any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTachesWithEagerRelationshipsIsNotEnabled() {
        when(tacheServiceMock.findAllWithEagerRelationships(any(), any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(tacheRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getTache() {
        // Initialize the database
        tacheRepository.save(tache).block();

        // Get the tache
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tache.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(tache.getId().intValue()))
            .jsonPath("$.titre")
            .value(is(DEFAULT_TITRE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.dateEcheance")
            .value(is(DEFAULT_DATE_ECHEANCE.toString()))
            .jsonPath("$.priorite")
            .value(is(DEFAULT_PRIORITE.toString()))
            .jsonPath("$.statut")
            .value(is(DEFAULT_STATUT.toString()));
    }

    @Test
    void getNonExistingTache() {
        // Get the tache
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTache() throws Exception {
        // Initialize the database
        tacheRepository.save(tache).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tache
        Tache updatedTache = tacheRepository.findById(tache.getId()).block();
        updatedTache
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .dateEcheance(UPDATED_DATE_ECHEANCE)
            .priorite(UPDATED_PRIORITE)
            .statut(UPDATED_STATUT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTache.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedTache))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tache in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTacheToMatchAllProperties(updatedTache);
    }

    @Test
    void putNonExistingTache() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tache.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tache.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tache))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tache in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTache() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tache.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tache))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tache in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTache() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tache.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tache))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tache in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTacheWithPatch() throws Exception {
        // Initialize the database
        tacheRepository.save(tache).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tache using partial update
        Tache partialUpdatedTache = new Tache();
        partialUpdatedTache.setId(tache.getId());

        partialUpdatedTache.dateEcheance(UPDATED_DATE_ECHEANCE).statut(UPDATED_STATUT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTache.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTache))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tache in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTacheUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTache, tache), getPersistedTache(tache));
    }

    @Test
    void fullUpdateTacheWithPatch() throws Exception {
        // Initialize the database
        tacheRepository.save(tache).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tache using partial update
        Tache partialUpdatedTache = new Tache();
        partialUpdatedTache.setId(tache.getId());

        partialUpdatedTache
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .dateEcheance(UPDATED_DATE_ECHEANCE)
            .priorite(UPDATED_PRIORITE)
            .statut(UPDATED_STATUT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTache.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTache))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tache in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTacheUpdatableFieldsEquals(partialUpdatedTache, getPersistedTache(partialUpdatedTache));
    }

    @Test
    void patchNonExistingTache() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tache.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tache.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tache))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tache in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTache() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tache.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tache))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tache in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTache() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tache.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tache))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tache in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTache() {
        // Initialize the database
        tacheRepository.save(tache).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tache
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tache.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tacheRepository.count().block();
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

    protected Tache getPersistedTache(Tache tache) {
        return tacheRepository.findById(tache.getId()).block();
    }

    protected void assertPersistedTacheToMatchAllProperties(Tache expectedTache) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTacheAllPropertiesEquals(expectedTache, getPersistedTache(expectedTache));
        assertTacheUpdatableFieldsEquals(expectedTache, getPersistedTache(expectedTache));
    }

    protected void assertPersistedTacheToMatchUpdatableProperties(Tache expectedTache) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTacheAllUpdatablePropertiesEquals(expectedTache, getPersistedTache(expectedTache));
        assertTacheUpdatableFieldsEquals(expectedTache, getPersistedTache(expectedTache));
    }
}
