/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.dao;

import br.com.loto.admin.domain.Cidade;
import br.com.loto.admin.domain.Cliente;
import br.com.loto.admin.domain.Estado;
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
    
    public List<Cliente> pesquisar(String nome, Long cidade, Long estado) throws SQLException {
        return pesquisar(nome, cidade, estado, Integer.MAX_VALUE);
    }

    public List<Cliente> pesquisar(String nome, Long cidade, Long estado, Integer maxValues) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT _cli.ID as _cli_ID, ");
        sql.append("        _cli.NOME AS _cli_NOME, ");
        sql.append("        _cli.ATIVO AS _cli_ATIVO, ");
        
        sql.append("        _cid.ID AS _cid_ID, ");
        sql.append("        _cid.NOME AS _cid_NOME, ");
        
        sql.append("        _est.ID AS _est_ID, ");
        sql.append("        _est.NOME AS _est_NOME, ");
        sql.append("        _est.SIGLA AS _est_SIGLA ");
        
        sql.append("   FROM CLIENTE _cli ");
        sql.append("  LEFT JOIN CIDADE _cid ON _cid.id = _cli.id_cidade ");
        sql.append("  LEFT JOIN ESTADO _est ON _est.id = _cid.id_estado ");
        sql.append("  WHERE 1 = 1 ");

        List<Object> parameters = new ArrayList<>();

        if (nome != null && !nome.trim().isEmpty()) {
            sql.append("  AND UPPER(_cli.NOME) LIKE ? ");
            parameters.add("%" + nome.toUpperCase() + "%");
        }
        
        if (cidade != null){
            sql.append("  AND _cid.ID = ? ");
            parameters.add(cidade);
        }
        
        if (estado != null){
            sql.append("  AND _est.ID = ? ");
            parameters.add(estado);
        }

        sql.append(" ORDER BY _cli.NOME ");

        return super.pesquisar(sql.toString(), parameters, (ResultSet rs) -> {
            Cidade c = null;
            if (rs.getObject("_cid_ID") != null){
                Estado e = new Estado();
                e.setId(rs.getLong("_est_ID"));
                e.setNome(rs.getString("_est_NOME"));
                e.setSigla(rs.getString("_est_SIGLA"));
                
                c = new Cidade();
                c.setId(rs.getLong("_cid_ID"));
                c.setEstado(e);
                c.setNome(rs.getString("_cid_NOME"));
            }
            
            Cliente cli = new Cliente();
            cli.setId(rs.getLong("_cli_ID"));
            cli.setNome(rs.getString("_cli_NOME"));
            cli.setAtivo(rs.getBoolean("_cli_ATIVO"));
            cli.setCidade(c);
                
            return cli;
        });
    }
}
