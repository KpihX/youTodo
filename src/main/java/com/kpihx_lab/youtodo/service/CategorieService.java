package com.kpihx_lab.youtodo.service;

import com.kpihx_lab.youtodo.domain.Categorie;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.kpihx_lab.youtodo.domain.Categorie}.
 */
public interface CategorieService {
    /**
     * Save a categorie.
     *
     * @param categorie the entity to save.
     * @return the persisted entity.
     */
    Mono<Categorie> save(Categorie categorie);

    /**
     * Updates a categorie.
     *
     * @param categorie the entity to update.
     * @return the persisted entity.
     */
    Mono<Categorie> update(Categorie categorie);

    /**
     * Partially updates a categorie.
     *
     * @param categorie the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Categorie> partialUpdate(Categorie categorie);

    /**
     * Get all the categories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Categorie> findAll(Pageable pageable);

    /**
     * Returns the number of categories available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" categorie.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Categorie> findOne(Long id);

    /**
     * Delete the "id" categorie.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
