/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.core.database.metadada;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author maxwe
 */
public class TableMetadata {

    private String tableName;

    private List<ColumnMetadata> columns = new ArrayList<>();

    public String getSqlInsert() {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ");
        builder.append(tableName);
        builder.append(" ( ");

        List<ColumnMetadata> colTmp = columns.stream().filter(el -> !el.isAutoIncrement()).collect(Collectors.toList());

        int i = 0;
        for (ColumnMetadata columnMetadata : colTmp) {
            builder.append(columnMetadata.getColumnName());
            if (i + 1 < colTmp.size()) {
                builder.append(", ");
            }

            i++;
        }

        builder.append(" ) ");

        builder.append(" VALUES ");
        builder.append(" ( ");

        for (int x = 0; x < i; x++) {
            builder.append(" ? ");

            if (x + 1 < i) {
                builder.append(", ");
            }
        }

        builder.append(" ) ");

        return builder.toString();
    }

    public String getSqlUpdate() {
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE ");
        builder.append(tableName);
        builder.append(" SET ");

        int i = 0;
        for (ColumnMetadata columnMetadata : columns) {
            if (columnMetadata.isPrimaryKey()) {
                i++;
                continue;
            }

            builder.append(columnMetadata.getColumnName());
            builder.append(" = ");
            builder.append(" ? ");
            if (i + 1 < columns.size()) {
                builder.append(", ");
            }

            i++;
        }

        builder.append(" WHERE ");
        builder.append(" 1 = 1 ");

        for (ColumnMetadata columnMetadata : columns) {
            if (columnMetadata.isPrimaryKey()) {
                builder.append(" AND ");
                builder.append(columnMetadata.getColumnName());
                builder.append(" = ");
                builder.append(" ? ");
            }
        }

        return builder.toString();
    }
    
    public String getSqlDelete() {
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM ");
        builder.append(tableName);

        builder.append(" WHERE ");
        builder.append(" 1 = 1 ");

        for (ColumnMetadata columnMetadata : columns) {
            if (columnMetadata.isPrimaryKey()) {
                builder.append(" AND ");
                builder.append(columnMetadata.getColumnName());
                builder.append(" = ");
                builder.append(" ? ");
            }
        }

        return builder.toString();
    }

    public boolean isNewRecord() {
        if (columns == null) {
            throw new NullPointerException();
        }

        for (ColumnMetadata columnMetadata : columns) {
            if (columnMetadata.isPrimaryKey()) {
                if (columnMetadata.getValue() == null) {
                    return true;
                } else if (columnMetadata.getDataType().equals(Integer.class)) {
                    Integer i = (Integer) columnMetadata.getValue();
                    return i != 0;
                }
            }
        }

        return false;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnMetadata> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnMetadata> columns) {
        this.columns = columns;
    }

}
