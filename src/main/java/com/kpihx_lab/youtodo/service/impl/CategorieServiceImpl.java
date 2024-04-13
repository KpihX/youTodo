package com.kpihx_lab.youtodo.service.impl;

import com.kpihx_lab.youtodo.domain.Categorie;
import com.kpihx_lab.youtodo.repository.CategorieRepository;
import com.kpihx_lab.youtodo.service.CategorieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.kpihx_lab.youtodo.domain.Categorie}.
 */
@Service
@Transactional
public class CategorieServiceImpl implements CategorieService {

    private final Logger log = LoggerFactory.getLogger(CategorieServiceImpl.class);

    private final CategorieRepository categorieRepository;

    public CategorieServiceImpl(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    @Override
    public Mono<Categorie> save(Categorie categorie) {
        log.debug("Request to save Categorie : {}", categorie);
        return categorieRepository.save(categorie);
    }

    @Override
    public Mono<Categorie> update(Categorie categorie) {
        log.debug("Request to update Categorie : {}", categorie);
        return categorieRepository.save(categorie);
    }

    @Override
    public Mono<Categorie> partialUpdate(Categorie categorie) {
        log.debug("Request to partially update Categorie : {}", categorie);

        return categorieRepository
            .findById(categorie.getId())
            .map(existingCategorie -> {
                if (categorie.getNom() != null) {
                    existingCategorie.setNom(categorie.getNom());
                }
                if (categorie.getDescription() != null) {
                    existingCategorie.setDescription(categorie.getDescription());
                }

                return existingCategorie;
            })
            .flatMap(categorieRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Categorie> findAll(Pageable pageable) {
        log.debug("Request to get all Categories");
        return categorieRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return categorieRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Categorie> findOne(Long id) {
        log.debug("Request to get Categorie : {}", id);
        return categorieRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Categorie : {}", id);
        return categorieRepository.deleteById(id);
    }
}
