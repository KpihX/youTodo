package com.kpihx_lab.youtodo.repository;

import com.kpihx_lab.youtodo.domain.Tache;
import com.kpihx_lab.youtodo.repository.rowmapper.CategorieRowMapper;
import com.kpihx_lab.youtodo.repository.rowmapper.TacheRowMapper;
import com.kpihx_lab.youtodo.repository.rowmapper.UserRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Tache entity.
 */
@SuppressWarnings("unused")
class TacheRepositoryInternalImpl extends SimpleR2dbcRepository<Tache, Long> implements TacheRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CategorieRowMapper categorieMapper;
    private final UserRowMapper userMapper;
    private final TacheRowMapper tacheMapper;

    private static final Table entityTable = Table.aliased("tache", EntityManager.ENTITY_ALIAS);
    private static final Table categorieTable = Table.aliased("categorie", "categorie");
    private static final Table userTable = Table.aliased("jhi_user", "e_user");

    public TacheRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CategorieRowMapper categorieMapper,
        UserRowMapper userMapper,
        TacheRowMapper tacheMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Tache.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.categorieMapper = categorieMapper;
        this.userMapper = userMapper;
        this.tacheMapper = tacheMapper;
    }

    @Override
    public Flux<Tache> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Tache> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TacheSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CategorieSqlHelper.getColumns(categorieTable, "categorie"));
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(categorieTable)
            .on(Column.create("categorie_id", entityTable))
            .equals(Column.create("id", categorieTable))
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Tache.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Tache> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Tache> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Tache> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Tache> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Tache> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Tache process(Row row, RowMetadata metadata) {
        Tache entity = tacheMapper.apply(row, "e");
        entity.setCategorie(categorieMapper.apply(row, "categorie"));
        entity.setUser(userMapper.apply(row, "user"));
        return entity;
    }

    @Override
    public <S extends Tache> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
