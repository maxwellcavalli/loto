/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.server.tv.business.dao;

import br.com.loto.core.dao.BaseDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class EquipamentoDAO extends BaseDAO<Object> {

    private static EquipamentoDAO instance;

    private EquipamentoDAO() {
    }

    public static EquipamentoDAO getInstance() {
        if (instance == null) {
            instance = new EquipamentoDAO();
        }

        return instance;
    }

    public boolean hasEquipamento(String uuid) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ID ");
        sql.append("   FROM EQUIPAMENTO ");
        sql.append("  WHERE UUID = ? ");
        sql.append("    AND ATIVO = ? ");

        List<Object> parameters = new ArrayList<>();
        parameters.add(uuid);
        parameters.add(true);

        Long id = (Long) super.carregar(sql.toString(), parameters, (ResultSet rs) -> {
            Long ret = rs.getLong("ID");

            return ret;
        });

        return id != null;

    }

}
