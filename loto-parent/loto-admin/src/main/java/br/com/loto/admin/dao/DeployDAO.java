/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.dao;

import br.com.loto.admin.domain.Cidade;
import br.com.loto.admin.domain.Deploy;
import br.com.loto.admin.domain.Estabelecimento;
import br.com.loto.admin.domain.EstabelecimentoEndereco;
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

    public List<Deploy> pesquisar(String descricao, Long estado, Long cidade, Long estabelecimento) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ");
        sql.append("    _d.ID as _d_ID, ");
        sql.append("    _d.DESCRICAO as _d_DESCRICAO, ");
        sql.append("    _d.ATIVO as _d_ATIVO, ");
        sql.append("    _d.DATA as _d_DATA, ");
        sql.append("    _d.DATA_VALIDADE as _d_DATA_VALIDADE, ");
        sql.append("    _d.SITUACAO AS _d_SITUACAO, ");
        sql.append("    _d.UUID AS _d_UUID, ");

        sql.append("    _e.id as _e_id,  ");
        sql.append("    _e.descricao as _e_descricao,  ");

        sql.append("   _cid.ID AS _cid_ID, ");
        sql.append("   _cid.NOME AS _cid_NOME, ");

        sql.append("   _est.ID AS _est_ID, ");
        sql.append("   _est.NOME AS _est_NOME, ");
        sql.append("   _est.SIGLA AS _est_SIGLA, ");

        sql.append("   _ee.id as _ee_id, ");
        sql.append("   _ee.logradouro as _ee_logradouro, ");
        sql.append("   _ee.numero as _ee_numero ");

        sql.append("   FROM DEPLOY _d ");
        sql.append("  INNER JOIN ESTABELECIMENTO _e ON _e.id = _d.id_estabelecimento ");
        sql.append("   LEFT JOIN ESTABELECIMENTO_ENDERECO _ee ON _ee.id_estabelecimento = _e.ID ");
        sql.append("   LEFT JOIN CIDADE _cid ON _cid.id = _ee.id_cidade ");
        sql.append("   LEFT JOIN ESTADO _est ON _est.id = _cid.id_estado ");
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

        if (estado != null) {
            sql.append("  AND _est.id = ? ");
            parameters.add(estado);
        }

        if (cidade != null) {
            sql.append("  AND _cid.id = ? ");
            parameters.add(cidade);
        }

        sql.append(" ORDER BY _d.DESCRICAO ");

        return super.pesquisar(sql.toString(), parameters, (ResultSet rs) -> {
            Cidade c = null;
            if (rs.getObject("_cid_ID") != null) {
                Estado e = new Estado();
                e.setId(rs.getLong("_est_ID"));
                e.setNome(rs.getString("_est_NOME"));
                e.setSigla(rs.getString("_est_SIGLA"));

                c = new Cidade();
                c.setId(rs.getLong("_cid_ID"));
                c.setEstado(e);
                c.setNome(rs.getString("_cid_NOME"));
            }

            EstabelecimentoEndereco ee = new EstabelecimentoEndereco();
            ee.setId(rs.getLong("_ee_id"));
            ee.setLogradouro(rs.getString("_ee_logradouro"));
            ee.setNumero(rs.getString("_ee_numero"));
            ee.setCidade(c);

            Estabelecimento e = new Estabelecimento();
            e.setId(rs.getLong("_e_id"));
            e.setDescricao(rs.getString("_e_descricao"));
            e.setEstabelecimentoEndereco(ee);

            Deploy d = new Deploy();
            d.setId(rs.getLong("_d_ID"));
            d.setDescricao(rs.getString("_d_DESCRICAO"));
            d.setAtivo(rs.getBoolean("_d_ATIVO"));
            d.setData(rs.getTimestamp("_d_DATA"));
            d.setDataValidade(rs.getTimestamp("_d_DATA_VALIDADE"));
            d.setSituacao(rs.getInt("_d_SITUACAO"));
            d.setUuid(rs.getString("_d_UUID"));
            d.setEstabelecimento(e);

            return d;
        });
    }
}
