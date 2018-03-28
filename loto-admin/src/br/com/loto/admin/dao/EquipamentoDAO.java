/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.dao;

import br.com.loto.core.dao.BaseDAO;
import br.com.loto.admin.domain.Equipamento;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class EquipamentoDAO extends BaseDAO<Equipamento> {

    private static EquipamentoDAO instance;

    private EquipamentoDAO() {
    }

    public static EquipamentoDAO getInstance() {
        if (instance == null) {
            instance = new EquipamentoDAO();
        }

        return instance;
    }

    @Override
    public Equipamento persistir(Equipamento t) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        return super.persistir(t); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Equipamento> pesquisar(String numSerie, Boolean ativo) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ID, NUM_SERIE, DESCRICAO, ATIVO ");
        sql.append("   FROM EQUIPAMENTO ");
        sql.append("  WHERE 1 = 1 ");

        List<Object> parameters = new ArrayList<>();

        if (numSerie != null && !numSerie.trim().isEmpty()) {
            sql.append("  AND UPPER(NUM_SERIE) = ? ");
            parameters.add(numSerie.toUpperCase());
        }

        if (ativo != null) {
            sql.append("  AND ativo = ? ");
            parameters.add(ativo);
        }

        sql.append(" ORDER BY NUM_SERIE ");

        return super.pesquisar(sql.toString(), parameters, (ResultSet rs) -> {
            Equipamento e = new Equipamento();
            e.setId(rs.getLong("ID"));
            e.setSerial(rs.getString("NUM_SERIE"));
            e.setDescricao(rs.getString("DESCRICAO"));
            e.setAtivo(rs.getBoolean("ATIVO"));

            return e;
        });
    }

    public List<Equipamento> pesquisar(String numSerie) throws SQLException {
        return pesquisar(numSerie, null);
    }

}
