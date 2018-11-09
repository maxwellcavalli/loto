/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.dao;

import br.com.loto.admin.domain.Deploy;
import br.com.loto.admin.domain.Estabelecimento;
import br.com.loto.core.dao.BaseDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class DeployDAO extends BaseDAO<Deploy> {

    private static DeployDAO instance;

    private DeployDAO() {
    }

    public static DeployDAO getInstance() {
        if (instance == null) {
            instance = new DeployDAO();
        }

        return instance;
    }

    @Override
    public Deploy persistir(Deploy t) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        return super.persistir(t); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Deploy> pesquisar(String descricao, Long estabelecimento) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ");
        sql.append("    _d.ID as _d_ID, ");
        sql.append("    _d.DESCRICAO as _d_DESCRICAO, ");
        sql.append("    _d.ATIVO as _d_ATIVO, ");
        sql.append("    _d.DATA as _d_DATA, ");
        sql.append("    _d.DATA_VALIDADE as _d_DATA_VALIDADE, ");
        sql.append("    _d.SITUACAO AS _d_SITUACAO, ");
        
        sql.append("    _e.id as _e_id,  ");
        sql.append("    _e.descricao as _e_descricao  ");
                
        
        sql.append("   FROM DEPLOY _d ");
        sql.append("  INNER JOIN ESTABELECIMENTO _e ON _e.id = _d.id_estabelecimento ");
        sql.append("  WHERE 1 = 1 ");

        List<Object> parameters = new ArrayList<>();

        if (descricao != null && !descricao.trim().isEmpty()) {
            sql.append("  AND UPPER(_d.DESCRICAO) LIKE ? ");
            parameters.add("%" + descricao.toUpperCase() + "%");
        }

        if (estabelecimento != null) {
            sql.append("  AND _d.ID_ESTABELECIMENTO = ? ");
            parameters.add(estabelecimento);
        }

        sql.append(" ORDER BY _d.DESCRICAO ");

        return super.pesquisar(sql.toString(), parameters, (ResultSet rs) -> {
            Estabelecimento e = new Estabelecimento();
            e.setId(rs.getLong("_e_id"));
            e.setDescricao(rs.getString("_e_descricao"));
            
            Deploy d = new Deploy();
            d.setId(rs.getLong("_d_ID"));
            d.setDescricao(rs.getString("_d_DESCRICAO"));
            d.setAtivo(rs.getBoolean("_d_ATIVO"));
            d.setData(rs.getTimestamp("_d_DATA"));
            d.setDataValidade(rs.getTimestamp("_d_DATA_VALIDADE"));
            d.setSituacao(rs.getInt("_d_SITUACAO"));
            d.setEstabelecimento(e);

            return d;
        });
    }
}
