/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.core.dao;

import br.com.loto.core.dao.interfaces.DatabaseRecord;
import br.com.loto.core.database.annotation.Campo;
import br.com.loto.core.database.annotation.Entidade;
import br.com.loto.core.database.annotation.Relacionamento;
import br.com.loto.core.database.metadada.ColumnMetadata;
import br.com.loto.core.database.metadada.TableMetadata;
import br.com.loto.core.util.JdbcUtil;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author maxwe
 * @param <T>
 */
public class BaseDAO<T> {

    protected List<T> pesquisar(String sql, List<Object> parameters, DatabaseRecord<T> databaseRecord) throws SQLException {
        return pesquisar(sql, parameters, databaseRecord, true);
    }

    protected List<T> pesquisar(String sql, List<Object> parameters, DatabaseRecord<T> databaseRecord, boolean rollbackAfterRun) throws SQLException {
        Connection conn = JdbcUtil.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);) {
            int index = 1;
            for (Object paramValue : parameters) {
                stmt.setObject(index++, paramValue);
            }

            try (ResultSet rs = stmt.executeQuery();) {
                List<T> ret = new ArrayList<>();
                while (rs.next()) {
                    T t = databaseRecord.onRecord(rs);

                    ret.add(t);
                }

                return ret;
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (rollbackAfterRun) {
                JdbcUtil.getInstance().rollback();
            }
        }
    }

    protected Long recordCount(String sql, List<Object> parameters) throws SQLException {
        Connection conn = JdbcUtil.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);) {
            int index = 1;

            for (Object paramValue : parameters) {
                stmt.setObject(index++, paramValue);
            }

            try (ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    return 0l;
                }

            } catch (Exception e) {
                throw e;
            } finally {
                JdbcUtil.getInstance().rollback();
            }
        } catch (Exception e) {
            JdbcUtil.getInstance().rollback();
            throw e;
        }
    }

    protected T carregar(String sql, List<Object> parameters, DatabaseRecord<T> databaseRecord) throws SQLException {
        Connection conn = JdbcUtil.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);) {
            int index = 1;
            for (Object paramValue : parameters) {
                stmt.setObject(index++, paramValue);
            }

            try (ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    return databaseRecord.onRecord(rs);
                } else {
                    return null;
                }

            } catch (Exception e) {
                throw e;
            } finally {
                JdbcUtil.getInstance().rollback();
            }
        } catch (Exception e) {
            JdbcUtil.getInstance().rollback();
            throw e;
        }
    }

    protected T persistir(T t) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {

        TableMetadata tableMetadata = extractTableMetadata(t);

        if (tableMetadata.isNewRecord()) {
            long ret = save(tableMetadata);

            Class clazz = t.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Campo.class)) {
                    Campo campo = field.getAnnotation(Campo.class);
                    if (campo.primaryKey() && campo.autoIncrement()) {
                        field.setAccessible(true);
                        field.set(t, ret);

                        break;
                    }
                }
            }

            return t;
        } else {
            update(tableMetadata);
            return t;
        }
    }

    private TableMetadata extractTableMetadata(T t) throws IllegalArgumentException, IllegalAccessException, Exception {
        Class clazz = t.getClass();

        TableMetadata tableMetadata = new TableMetadata();

        if (clazz.isAnnotationPresent(Entidade.class)) {
            Entidade entidade = (Entidade) clazz.getAnnotation(Entidade.class);

            tableMetadata.setTableName(entidade.name());

            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);

                if (field.isAnnotationPresent(Campo.class)) {
                    Object value = field.get(t);

                    Campo campo = field.getAnnotation(Campo.class);

                    ColumnMetadata columnMetadata = new ColumnMetadata();
                    columnMetadata.setFieldName(field.getName());
                    columnMetadata.setColumnName(campo.name());
                    columnMetadata.setValue(value);
                    columnMetadata.setPrimaryKey(campo.primaryKey());
                    columnMetadata.setDataType(field.getType());
                    columnMetadata.setAutoIncrement(campo.autoIncrement());

                    tableMetadata.getColumns().add(columnMetadata);

                } else if (field.isAnnotationPresent(Relacionamento.class)) {
                    Relacionamento relacionamento = field.getAnnotation(Relacionamento.class);

                    Object value = field.get(t);
                    if (value != null) {
                        Class relationClass = value.getClass();

                        Field fieldPk = null;
                        Field relationFields[] = relationClass.getDeclaredFields();
                        for (Field relationField : relationFields) {
                            if (relationField.isAnnotationPresent(Campo.class)) {
                                Campo campo = relationField.getAnnotation(Campo.class);
                                if (campo.primaryKey()) {
                                    fieldPk = relationField;
                                    break;
                                }
                            }
                        }

                        if (fieldPk == null) {
                            throw new Exception("Relacionamento Invalido");
                        }

                        fieldPk.setAccessible(true);
                        Object valuePk = fieldPk.get(value);

                        ColumnMetadata columnMetadata = new ColumnMetadata();
                        columnMetadata.setFieldName(field.getName());
                        columnMetadata.setColumnName(relacionamento.relationColumnName());
                        columnMetadata.setValue(valuePk);
                        columnMetadata.setPrimaryKey(false);
                        columnMetadata.setDataType(fieldPk.getType());
                        columnMetadata.setAutoIncrement(false);

                        tableMetadata.getColumns().add(columnMetadata);

                    }
                }
            }
        }

        return tableMetadata;
    }

    private long save(TableMetadata tableMetadata) throws SQLException {
        Connection conn = JdbcUtil.getInstance().getConnection();
        String sql = tableMetadata.getSqlInsert();

        long generatedKey = -1;
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            List<ColumnMetadata> cols = tableMetadata.getColumns().stream()
                    .filter(el -> !el.isAutoIncrement())
                    .collect(Collectors.toList());

            int index = 1;
            for (ColumnMetadata columnMetadata : cols) {
                statementParamValue(columnMetadata, stmt, index);
                index++;
            }

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getLong(1);
                }
            } catch (SQLException s) {
                throw s;
            }
            return generatedKey;
        } catch (Exception e) {
            JdbcUtil.getInstance().rollback();

            throw e;
        }
    }

    private void update(TableMetadata tableMetadata) throws SQLException {
        Connection conn = JdbcUtil.getInstance().getConnection();
        String sql = tableMetadata.getSqlUpdate();

        try (PreparedStatement stmt = conn.prepareStatement(sql);) {

            //setting fields values
            List<ColumnMetadata> cols = tableMetadata.getColumns().stream()
                    .filter(el -> !el.isPrimaryKey())
                    .collect(Collectors.toList());

            int index = 1;
            for (ColumnMetadata columnMetadata : cols) {
                statementParamValue(columnMetadata, stmt, index);
                index++;
            }

            //setting pk value
            List<ColumnMetadata> pks = tableMetadata.getColumns().stream()
                    .filter(el -> el.isPrimaryKey())
                    .collect(Collectors.toList());

            for (ColumnMetadata columnMetadata : pks) {
                statementParamValue(columnMetadata, stmt, index);
                index++;
            }

            stmt.executeUpdate();
        } catch (Exception e) {

            JdbcUtil.getInstance().rollback();

            throw e;
        }
    }

    protected void delete(T t) throws SQLException, IllegalAccessException, Exception {
        TableMetadata tableMetadata = extractTableMetadata(t);
        delete(tableMetadata);
    }

    private void delete(TableMetadata tableMetadata) throws SQLException {
        Connection conn = JdbcUtil.getInstance().getConnection();
        String sql = tableMetadata.getSqlDelete();

        try (PreparedStatement stmt = conn.prepareStatement(sql);) {
            int index = 1;

            //setting pk value
            List<ColumnMetadata> pks = tableMetadata.getColumns().stream()
                    .filter(el -> el.isPrimaryKey())
                    .collect(Collectors.toList());

            for (ColumnMetadata columnMetadata : pks) {
                statementParamValue(columnMetadata, stmt, index);
                index++;
            }

            stmt.executeUpdate();
        } catch (Exception e) {

            JdbcUtil.getInstance().rollback();

            throw e;
        }
    }

    private void statementParamValue(ColumnMetadata columnMetadata, PreparedStatement stmt, int index) throws SQLException {
        if (columnMetadata.getDataType().equals(byte[].class)) {
            if (columnMetadata.getValue() == null) {
                stmt.setNull(index, Types.BLOB);
            } else {
                byte[] value = (byte[]) columnMetadata.getValue();

                stmt.setBytes(index, value);
            }
        } else if (columnMetadata.getDataType().equals(String.class)) {
            if (columnMetadata.getValue() == null) {
                stmt.setNull(index, Types.VARCHAR);
            } else {
                stmt.setString(index, (String) columnMetadata.getValue());
            }
        } else if (columnMetadata.getDataType().equals(Integer.class)) {
            if (columnMetadata.getValue() == null) {
                stmt.setNull(index, Types.INTEGER);
            } else {
                stmt.setInt(index, (Integer) columnMetadata.getValue());
            }
        } else if (columnMetadata.getDataType().equals(Long.class)) {
            if (columnMetadata.getValue() == null) {
                stmt.setNull(index, Types.INTEGER);
            } else {
                stmt.setLong(index, (Long) columnMetadata.getValue());
            }
        } else if (columnMetadata.getDataType().equals(Double.class)) {
            if (columnMetadata.getValue() == null) {
                stmt.setNull(index, Types.DOUBLE);
            } else {
                stmt.setDouble(index, (Double) columnMetadata.getValue());
            }
        } else if (columnMetadata.getDataType().equals(BigDecimal.class)) {
            if (columnMetadata.getValue() == null) {
                stmt.setNull(index, Types.NUMERIC);
            } else {
                stmt.setBigDecimal(index, (BigDecimal) columnMetadata.getValue());
            }
        } else if (columnMetadata.getDataType().equals(Date.class)) {
            if (columnMetadata.getValue() == null) {
                stmt.setNull(index, Types.TIMESTAMP);
            } else {
                stmt.setTimestamp(index, new java.sql.Timestamp(((Date) columnMetadata.getValue()).getTime()));
            }

        } else {
            stmt.setObject(index, columnMetadata.getValue());
        }
    }

}
