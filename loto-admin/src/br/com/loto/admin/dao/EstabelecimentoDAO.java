/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.dao;

import br.com.loto.core.dao.BaseDAO;
import br.com.loto.admin.domain.Estabelecimento;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author maxwe
 */
public class EstabelecimentoDAO extends BaseDAO<Estabelecimento> {

    private static EstabelecimentoDAO instance;

    private EstabelecimentoDAO() {
    }

    public static EstabelecimentoDAO getInstance() {
        if (instance == null) {
            instance = new EstabelecimentoDAO();
        }

        return instance;
    }

    @Override
    public Estabelecimento persistir(Estabelecimento t) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        return super.persistir(t); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Estabelecimento> pesquisar(String descricao) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ID, DESCRICAO, ATIVO ");
        sql.append("   FROM ESTABELECIMENTO ");
        sql.append("  WHERE 1 = 1 ");

        List<Object> parameters = new ArrayList<>();

        if (descricao != null && !descricao.trim().isEmpty()) {
            sql.append("  AND UPPER(DESCRICAO) = ? ");
            parameters.add(descricao.toUpperCase());
        }

        sql.append(" ORDER BY DESCRICAO ");

        return super.pesquisar(sql.toString(), parameters, (ResultSet rs) -> {
            Estabelecimento e = new Estabelecimento();
            e.setId(rs.getLong("ID"));
            e.setDescricao(rs.getString("DESCRICAO"));
            e.setAtivo(rs.getBoolean("ATIVO"));

            return e;
        });
    }

}
