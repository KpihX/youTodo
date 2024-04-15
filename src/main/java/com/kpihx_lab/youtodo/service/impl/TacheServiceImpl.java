package com.kpihx_lab.youtodo.service.impl;

import com.kpihx_lab.youtodo.domain.Tache;
import com.kpihx_lab.youtodo.repository.TacheRepository;
import com.kpihx_lab.youtodo.service.TacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.kpihx_lab.youtodo.domain.Tache}.
 */
@Service
@Transactional
public class TacheServiceImpl implements TacheService {

    private final Logger log = LoggerFactory.getLogger(TacheServiceImpl.class);

    private final TacheRepository tacheRepository;

    public TacheServiceImpl(TacheRepository tacheRepository) {
        this.tacheRepository = tacheRepository;
    }

    @Override
    public Mono<Tache> save(Tache tache) {
        log.debug("Request to save Tache : {}", tache);
        return tacheRepository.save(tache);
    }

    @Override
    public Mono<Tache> update(Tache tache) {
        log.debug("Request to update Tache : {}", tache);
        return tacheRepository.save(tache);
    }

    @Override
    public Mono<Tache> partialUpdate(Tache tache) {
        log.debug("Request to partially update Tache : {}", tache);

        return tacheRepository
            .findById(tache.getId())
            .map(existingTache -> {
                if (tache.getTitre() != null) {
                    existingTache.setTitre(tache.getTitre());
                }
                if (tache.getDescription() != null) {
                    existingTache.setDescription(tache.getDescription());
                }
                if (tache.getDateEcheance() != null) {
                    existingTache.setDateEcheance(tache.getDateEcheance());
                }
                if (tache.getPriorite() != null) {
                    existingTache.setPriorite(tache.getPriorite());
                }
                if (tache.getStatut() != null) {
                    existingTache.setStatut(tache.getStatut());
                }

                return existingTache;
            })
            .flatMap(tacheRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Tache> findAll(Pageable pageable, Long userId) {
        log.debug("Request to get all Taches");
        return tacheRepository.findAllBy(pageable, userId);
    }

    public Flux<Tache> findAllWithEagerRelationships(Pageable pageable, Long userId) {
        return tacheRepository.findAllWithEagerRelationships(pageable, userId);
    }

    // @Override
    // @Transactional(readOnly = true)
    // public Flux<Tache> findByUser(Pageable pageable, Long userId) {
    //     log.debug("Request to get all Taches by User : {}", userId);
    //     return tacheRepository.findByUser(pageable, userId);
    // }

    public Mono<Long> countAll() {
        return tacheRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Tache> findOne(Long id) {
        log.debug("Request to get Tache : {}", id);
        return tacheRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Tache : {}", id);
        return tacheRepository.deleteById(id);
    }
}
