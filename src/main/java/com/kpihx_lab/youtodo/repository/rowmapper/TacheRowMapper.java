package com.kpihx_lab.youtodo.repository.rowmapper;

import com.kpihx_lab.youtodo.domain.Tache;
import com.kpihx_lab.youtodo.domain.enumeration.Priorite;
import com.kpihx_lab.youtodo.domain.enumeration.StatutTache;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Tache}, with proper type conversions.
 */
@Service
public class TacheRowMapper implements BiFunction<Row, String, Tache> {

    private final ColumnConverter converter;

    public TacheRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Tache} stored in the database.
     */
    @Override
    public Tache apply(Row row, String prefix) {
        Tache entity = new Tache();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitre(converter.fromRow(row, prefix + "_titre", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setDateEcheance(converter.fromRow(row, prefix + "_date_echeance", LocalDate.class));
        entity.setPriorite(converter.fromRow(row, prefix + "_priorite", Priorite.class));
        entity.setStatut(converter.fromRow(row, prefix + "_statut", StatutTache.class));
        entity.setCategorieId(converter.fromRow(row, prefix + "_categorie_id", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        return entity;
    }
}
