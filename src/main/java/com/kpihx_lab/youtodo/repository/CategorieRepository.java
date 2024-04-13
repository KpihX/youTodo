package com.kpihx_lab.youtodo.repository;

import com.kpihx_lab.youtodo.domain.Categorie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Categorie entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategorieRepository extends ReactiveCrudRepository<Categorie, Long>, CategorieRepositoryInternal {
    Flux<Categorie> findAllBy(Pageable pageable);

    @Override
    <S extends Categorie> Mono<S> save(S entity);

    @Override
    Flux<Categorie> findAll();

    @Override
    Mono<Categorie> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CategorieRepositoryInternal {
    <S extends Categorie> Mono<S> save(S entity);

    Flux<Categorie> findAllBy(Pageable pageable);

    Flux<Categorie> findAll();

    Mono<Categorie> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Categorie> findAllBy(Pageable pageable, Criteria criteria);
}
