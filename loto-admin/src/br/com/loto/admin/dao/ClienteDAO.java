/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.dao;

import br.com.loto.admin.domain.Cliente;
import br.com.loto.core.dao.BaseDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class ClienteDAO extends BaseDAO<Cliente> {

    private static ClienteDAO instance;

    private ClienteDAO() {
    }

    public static ClienteDAO getInstance() {
        if (instance == null) {
            instance = new ClienteDAO();
        }

        return instance;
    }

    @Override
    public Cliente persistir(Cliente t) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        return super.persistir(t); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Cliente> pesquisar(String nome) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ID, NOME ");
        sql.append("   FROM CLIENTE ");
        sql.append("  WHERE 1 = 1 ");

        List<Object> parameters = new ArrayList<>();

        if (nome != null && !nome.trim().isEmpty()) {
            sql.append("  AND UPPER(NOME) LIKE ? ");
            parameters.add("%" + nome.toUpperCase() + "%");
        }

        sql.append(" ORDER BY NOME ");

        return super.pesquisar(sql.toString(), parameters, (ResultSet rs) -> {
            Cliente e = new Cliente();
            e.setId(rs.getLong("ID"));
            e.setNome(rs.getString("NOME"));

            return e;
        });
    }
}
