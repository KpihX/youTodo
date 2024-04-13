package com.kpihx_lab.youtodo.web.rest;

import static com.kpihx_lab.youtodo.domain.CategorieAsserts.*;
import static com.kpihx_lab.youtodo.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpihx_lab.youtodo.IntegrationTest;
import com.kpihx_lab.youtodo.domain.Categorie;
import com.kpihx_lab.youtodo.repository.CategorieRepository;
import com.kpihx_lab.youtodo.repository.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link CategorieResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CategorieResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CategorieRepository categorieRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Categorie categorie;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Categorie createEntity(EntityManager em) {
        Categorie categorie = new Categorie().nom(DEFAULT_NOM).description(DEFAULT_DESCRIPTION);
        return categorie;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Categorie createUpdatedEntity(EntityManager em) {
        Categorie categorie = new Categorie().nom(UPDATED_NOM).description(UPDATED_DESCRIPTION);
        return categorie;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Categorie.class).block();
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
        categorie = createEntity(em);
    }

    @Test
    void createCategorie() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Categorie
        var returnedCategorie = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(categorie))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Categorie.class)
            .returnResult()
            .getResponseBody();

        // Validate the Categorie in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCategorieUpdatableFieldsEquals(returnedCategorie, getPersistedCategorie(returnedCategorie));
    }

    @Test
    void createCategorieWithExistingId() throws Exception {
        // Create the Categorie with an existing ID
        categorie.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(categorie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Categorie in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        categorie.setNom(null);

        // Create the Categorie, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(categorie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllCategories() {
        // Initialize the database
        categorieRepository.save(categorie).block();

        // Get all the categorieList
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
            .value(hasItem(categorie.getId().intValue()))
            .jsonPath("$.[*].nom")
            .value(hasItem(DEFAULT_NOM))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getCategorie() {
        // Initialize the database
        categorieRepository.save(categorie).block();

        // Get the categorie
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, categorie.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(categorie.getId().intValue()))
            .jsonPath("$.nom")
            .value(is(DEFAULT_NOM))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getNonExistingCategorie() {
        // Get the categorie
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCategorie() throws Exception {
        // Initialize the database
        categorieRepository.save(categorie).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the categorie
        Categorie updatedCategorie = categorieRepository.findById(categorie.getId()).block();
        updatedCategorie.nom(UPDATED_NOM).description(UPDATED_DESCRIPTION);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCategorie.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedCategorie))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Categorie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCategorieToMatchAllProperties(updatedCategorie);
    }

    @Test
    void putNonExistingCategorie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categorie.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, categorie.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(categorie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Categorie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCategorie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categorie.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(categorie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Categorie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCategorie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categorie.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(categorie))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Categorie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCategorieWithPatch() throws Exception {
        // Initialize the database
        categorieRepository.save(categorie).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the categorie using partial update
        Categorie partialUpdatedCategorie = new Categorie();
        partialUpdatedCategorie.setId(categorie.getId());

        partialUpdatedCategorie.nom(UPDATED_NOM).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCategorie.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCategorie))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Categorie in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCategorieUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCategorie, categorie),
            getPersistedCategorie(categorie)
        );
    }

    @Test
    void fullUpdateCategorieWithPatch() throws Exception {
        // Initialize the database
        categorieRepository.save(categorie).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the categorie using partial update
        Categorie partialUpdatedCategorie = new Categorie();
        partialUpdatedCategorie.setId(categorie.getId());

        partialUpdatedCategorie.nom(UPDATED_NOM).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCategorie.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCategorie))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Categorie in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCategorieUpdatableFieldsEquals(partialUpdatedCategorie, getPersistedCategorie(partialUpdatedCategorie));
    }

    @Test
    void patchNonExistingCategorie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categorie.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, categorie.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(categorie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Categorie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCategorie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categorie.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(categorie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Categorie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCategorie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categorie.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(categorie))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Categorie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCategorie() {
        // Initialize the database
        categorieRepository.save(categorie).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the categorie
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, categorie.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return categorieRepository.count().block();
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

    protected Categorie getPersistedCategorie(Categorie categorie) {
        return categorieRepository.findById(categorie.getId()).block();
    }

    protected void assertPersistedCategorieToMatchAllProperties(Categorie expectedCategorie) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCategorieAllPropertiesEquals(expectedCategorie, getPersistedCategorie(expectedCategorie));
        assertCategorieUpdatableFieldsEquals(expectedCategorie, getPersistedCategorie(expectedCategorie));
    }

    protected void assertPersistedCategorieToMatchUpdatableProperties(Categorie expectedCategorie) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCategorieAllUpdatablePropertiesEquals(expectedCategorie, getPersistedCategorie(expectedCategorie));
        assertCategorieUpdatableFieldsEquals(expectedCategorie, getPersistedCategorie(expectedCategorie));
    }
}
