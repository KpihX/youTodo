package com.kpihx_lab.youtodo.repository;

import com.kpihx_lab.youtodo.domain.Tache;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Tache entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TacheRepository extends ReactiveCrudRepository<Tache, Long>, TacheRepositoryInternal {
    Flux<Tache> findAllBy(Pageable pageable, Long userId);

    @Override
    Mono<Tache> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Tache> findAllWithEagerRelationships(Long userId);

    @Override
    Flux<Tache> findAllWithEagerRelationships(Pageable page, Long userId);

    @Query("SELECT * FROM tache entity WHERE entity.categorie_id = :id")
    Flux<Tache> findByCategorie(Long id);

    @Query("SELECT * FROM tache entity WHERE entity.categorie_id IS NULL")
    Flux<Tache> findAllWhereCategorieIsNull();

    @Query("SELECT * FROM tache entity WHERE entity.user_id = :id")
    Flux<Tache> findByUser(Pageable pageable, Long id);

    @Query("SELECT * FROM tache entity WHERE entity.user_id IS NULL")
    Flux<Tache> findAllWhereUserIsNull();

    @Override
    <S extends Tache> Mono<S> save(S entity);

    @Override
    Flux<Tache> findAll(Long userId);

    @Override
    Mono<Tache> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TacheRepositoryInternal {
    <S extends Tache> Mono<S> save(S entity);

    Flux<Tache> findAllBy(Pageable pageable, Long userId);

    Flux<Tache> findAll(Long userId);

    Mono<Tache> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Tache> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Tache> findOneWithEagerRelationships(Long id);

    Flux<Tache> findAllWithEagerRelationships(Long userId);

    Flux<Tache> findAllWithEagerRelationships(Pageable page, Long userId);

    Mono<Void> deleteById(Long id);
}
