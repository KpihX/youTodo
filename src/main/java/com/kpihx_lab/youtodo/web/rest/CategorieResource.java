package com.kpihx_lab.youtodo.web.rest;

import com.kpihx_lab.youtodo.domain.Categorie;
import com.kpihx_lab.youtodo.repository.CategorieRepository;
import com.kpihx_lab.youtodo.service.CategorieService;
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
 * REST controller for managing {@link com.kpihx_lab.youtodo.domain.Categorie}.
 */
@RestController
@RequestMapping("/api/categories")
public class CategorieResource {

    private final Logger log = LoggerFactory.getLogger(CategorieResource.class);

    private static final String ENTITY_NAME = "categorie";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CategorieService categorieService;

    private final CategorieRepository categorieRepository;

    public CategorieResource(CategorieService categorieService, CategorieRepository categorieRepository) {
        this.categorieService = categorieService;
        this.categorieRepository = categorieRepository;
    }

    /**
     * {@code POST  /categories} : Create a new categorie.
     *
     * @param categorie the categorie to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new categorie, or with status {@code 400 (Bad Request)} if the categorie has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Categorie>> createCategorie(@Valid @RequestBody Categorie categorie) throws URISyntaxException {
        log.debug("REST request to save Categorie : {}", categorie);
        if (categorie.getId() != null) {
            throw new BadRequestAlertException("A new categorie cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return categorieService
            .save(categorie)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/categories/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /categories/:id} : Updates an existing categorie.
     *
     * @param id the id of the categorie to save.
     * @param categorie the categorie to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categorie,
     * or with status {@code 400 (Bad Request)} if the categorie is not valid,
     * or with status {@code 500 (Internal Server Error)} if the categorie couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Categorie>> updateCategorie(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Categorie categorie
    ) throws URISyntaxException {
        log.debug("REST request to update Categorie : {}, {}", id, categorie);
        if (categorie.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, categorie.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return categorieRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return categorieService
                    .update(categorie)
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
     * {@code PATCH  /categories/:id} : Partial updates given fields of an existing categorie, field will ignore if it is null
     *
     * @param id the id of the categorie to save.
     * @param categorie the categorie to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categorie,
     * or with status {@code 400 (Bad Request)} if the categorie is not valid,
     * or with status {@code 404 (Not Found)} if the categorie is not found,
     * or with status {@code 500 (Internal Server Error)} if the categorie couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Categorie>> partialUpdateCategorie(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Categorie categorie
    ) throws URISyntaxException {
        log.debug("REST request to partial update Categorie partially : {}, {}", id, categorie);
        if (categorie.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, categorie.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return categorieRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Categorie> result = categorieService.partialUpdate(categorie);

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
     * {@code GET  /categories} : get all the categories.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of categories in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Categorie>>> getAllCategories(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Categories");
        return categorieService
            .countAll()
            .zipWith(categorieService.findAll(pageable).collectList())
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
     * {@code GET  /categories/:id} : get the "id" categorie.
     *
     * @param id the id of the categorie to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the categorie, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Categorie>> getCategorie(@PathVariable("id") Long id) {
        log.debug("REST request to get Categorie : {}", id);
        Mono<Categorie> categorie = categorieService.findOne(id);
        return ResponseUtil.wrapOrNotFound(categorie);
    }

    /**
     * {@code DELETE  /categories/:id} : delete the "id" categorie.
     *
     * @param id the id of the categorie to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCategorie(@PathVariable("id") Long id) {
        log.debug("REST request to delete Categorie : {}", id);
        return categorieService
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
