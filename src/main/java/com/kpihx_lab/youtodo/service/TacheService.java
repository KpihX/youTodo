package com.kpihx_lab.youtodo.service;

import com.kpihx_lab.youtodo.domain.Tache;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.kpihx_lab.youtodo.domain.Tache}.
 */
public interface TacheService {
    /**
     * Save a tache.
     *
     * @param tache the entity to save.
     * @return the persisted entity.
     */
    Mono<Tache> save(Tache tache);

    /**
     * Updates a tache.
     *
     * @param tache the entity to update.
     * @return the persisted entity.
     */
    Mono<Tache> update(Tache tache);

    /**
     * Partially updates a tache.
     *
     * @param tache the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Tache> partialUpdate(Tache tache);

    /**
     * Get all the taches.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Tache> findAll(Pageable pageable);

    /**
     * Get all the taches with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Tache> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of taches available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" tache.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Tache> findOne(Long id);

    /**
     * Delete the "id" tache.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
