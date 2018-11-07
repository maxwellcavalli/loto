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
import java.util.List;

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

    public List<Estabelecimento> pesquisar(String descricao, Long estado, Long cidade) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT _e.ID, _e.DESCRICAO, _e.ATIVO ");
        sql.append("   FROM ESTABELECIMENTO _e");
        sql.append("   LEFT JOIN ESTABELECIMENTO_ENDERECO _ee ON _ee.id_estabelecimento = _e.ID ");
        sql.append("   LEFT JOIN CIDADE _cid ON _cid.id = _ee.id_cidade ");
        sql.append("   LEFT JOIN ESTADO _est ON _est.id = _cid.id_estado ");
        sql.append("  WHERE 1 = 1 ");

        List<Object> parameters = new ArrayList<>();

        if (descricao != null && !descricao.trim().isEmpty()) {
            sql.append("  AND UPPER(_e.DESCRICAO) = ? ");
            parameters.add(descricao.toUpperCase());
        }

        if (cidade != null) {
            sql.append("  AND _cid.ID = ? ");
            parameters.add(cidade);
        }

        if (estado != null) {
            sql.append("  AND _est.ID = ? ");
            parameters.add(estado);
        }

        sql.append(" ORDER BY _e.DESCRICAO ");

        return super.pesquisar(sql.toString(), parameters, (ResultSet rs) -> {
            Estabelecimento e = new Estabelecimento();
            e.setId(rs.getLong("ID"));
            e.setDescricao(rs.getString("DESCRICAO"));
            e.setAtivo(rs.getBoolean("ATIVO"));

            return e;
        });
    }

}
