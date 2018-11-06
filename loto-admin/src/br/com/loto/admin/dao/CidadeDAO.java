/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.dao;

import br.com.loto.admin.domain.Cidade;
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
public class CidadeDAO extends BaseDAO<Cidade> {

    private static CidadeDAO instance;

    private CidadeDAO() {
    }

    public static CidadeDAO getInstance() {
        if (instance == null) {
            instance = new CidadeDAO();
        }

        return instance;
    }

    @Override
    public Cidade persistir(Cidade t) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        return super.persistir(t); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Cidade> pesquisar(String nome, Long estado) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT _cid.ID as _cid_id, ");
        sql.append("        _cid.NOME as _cid_nome, ");
        sql.append("        _cid.ATIVO as _cid_ativo, ");
        sql.append("        _est.id as _est_id, ");
        sql.append("        _est.nome as _est_nome, ");
        sql.append("        _est.sigla as _est_sigla, ");
        sql.append("        _est.ativo as _est_ativo ");
                
        sql.append("   FROM CIDADE _cid ");
        sql.append("  INNER JOIN ESTADO _est ON _est.id = _cid.id_estado ");
        sql.append("  WHERE 1 = 1 ");

        List<Object> parameters = new ArrayList<>();

        if (nome != null && !nome.trim().isEmpty()) {
            sql.append("  AND UPPER(_cid.NOME) LIKE ? ");
            parameters.add("%" + nome.toUpperCase() + "%");
        }
        
        if (estado != null && estado > 0){
            sql.append("  AND _est.id = ? ");
            parameters.add(estado);
        }

        sql.append(" ORDER BY _cid.NOME ");

        return super.pesquisar(sql.toString(), parameters, (ResultSet rs) -> {
            Cidade c = new Cidade();
            c.setId(rs.getLong("_cid_id"));
            c.setNome(rs.getString("_cid_nome"));
            c.setAtivo(rs.getBoolean("_cid_ativo"));
                    
            Estado e = new Estado();
            e.setId(rs.getLong("_est_id"));
            e.setNome(rs.getString("_est_nome"));
            e.setSigla(rs.getString("_est_sigla"));
            e.setAtivo(rs.getBoolean("_est_ativo"));
            
            c.setEstado(e);
            
            return c;
        });
    }
}
