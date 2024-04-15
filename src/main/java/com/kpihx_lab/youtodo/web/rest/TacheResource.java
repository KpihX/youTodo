package com.kpihx_lab.youtodo.web.rest;

import com.kpihx_lab.youtodo.domain.Tache;
import com.kpihx_lab.youtodo.repository.TacheRepository;
import com.kpihx_lab.youtodo.service.TacheService;
import com.kpihx_lab.youtodo.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.kpihx_lab.youtodo.domain.Tache}.
 */
@RestController
@RequestMapping("/api/taches")
public class TacheResource {

    private final Logger log = LoggerFactory.getLogger(TacheResource.class);

    private static final String ENTITY_NAME = "tache";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TacheService tacheService;

    private final TacheRepository tacheRepository;

    public TacheResource(TacheService tacheService, TacheRepository tacheRepository) {
        this.tacheService = tacheService;
        this.tacheRepository = tacheRepository;
    }

    /**
     * {@code POST  /taches} : Create a new tache.
     *
     * @param tache the tache to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tache, or with status {@code 400 (Bad Request)} if the tache has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Tache>> createTache(@Valid @RequestBody Tache tache) throws URISyntaxException {
        log.debug("REST request to save Tache : {}", tache);
        if (tache.getId() != null) {
            throw new BadRequestAlertException("A new tache cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return tacheService
            .save(tache)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/taches/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /taches/:id} : Updates an existing tache.
     *
     * @param id the id of the tache to save.
     * @param tache the tache to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tache,
     * or with status {@code 400 (Bad Request)} if the tache is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tache couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Tache>> updateTache(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Tache tache
    ) throws URISyntaxException {
        log.debug("REST request to update Tache : {}, {}", id, tache);
        if (tache.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tache.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tacheRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return tacheService
                    .update(tache)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(
                        result ->
                            ResponseEntity.ok()
                                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                                .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /taches/:id} : Partial updates given fields of an existing tache, field will ignore if it is null
     *
     * @param id the id of the tache to save.
     * @param tache the tache to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tache,
     * or with status {@code 400 (Bad Request)} if the tache is not valid,
     * or with status {@code 404 (Not Found)} if the tache is not found,
     * or with status {@code 500 (Internal Server Error)} if the tache couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Tache>> partialUpdateTache(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Tache tache
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tache partially : {}, {}", id, tache);
        if (tache.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tache.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tacheRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Tache> result = tacheService.partialUpdate(tache);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(
                        res ->
                            ResponseEntity.ok()
                                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                                .body(res)
                    );
            });
    }

    /**
     * {@code GET  /taches} : get all the taches.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taches in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Tache>>> getAllTaches(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false) Long userId,
        ServerHttpRequest request,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Taches");
        return tacheService
            .countAll()
            .zipWith(tacheService.findAll(pageable, userId).collectList())
            .map(
                countWithEntities ->
                    ResponseEntity.ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                            )
                        )
                        .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /taches/:id} : get the "id" tache.
     *
     * @param id the id of the tache to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tache, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Tache>> getTache(@PathVariable("id") Long id) {
        log.debug("REST request to get Tache : {}", id);
        Mono<Tache> tache = tacheService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tache);
    }

    /**
     * {@code DELETE  /taches/:id} : delete the "id" tache.
     *
     * @param id the id of the tache to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTache(@PathVariable("id") Long id) {
        log.debug("REST request to delete Tache : {}", id);
        return tacheService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
