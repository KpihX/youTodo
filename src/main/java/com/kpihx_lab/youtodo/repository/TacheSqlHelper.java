package com.kpihx_lab.youtodo.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TacheSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("titre", table, columnPrefix + "_titre"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("date_echeance", table, columnPrefix + "_date_echeance"));
        columns.add(Column.aliased("priorite", table, columnPrefix + "_priorite"));
        columns.add(Column.aliased("statut", table, columnPrefix + "_statut"));

        columns.add(Column.aliased("categorie_id", table, columnPrefix + "_categorie_id"));
        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        return columns;
    }
}
