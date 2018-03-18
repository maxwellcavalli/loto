/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.dao;

import br.com.loto.core.dao.BaseDAO;
import br.com.loto.admin.domain.Estabelecimento;
import br.com.loto.admin.domain.EstabelecimentoEndereco;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class EstabelecimentoEnderecoDAO extends BaseDAO<EstabelecimentoEndereco> {

    private static EstabelecimentoEnderecoDAO instance;

    private EstabelecimentoEnderecoDAO() {
    }

    public static EstabelecimentoEnderecoDAO getInstance() {
        if (instance == null) {
            instance = new EstabelecimentoEnderecoDAO();
        }

        return instance;
    }

    @Override
    public EstabelecimentoEndereco persistir(EstabelecimentoEndereco t) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        return super.persistir(t); //To change body of generated methods, choose Tools | Templates.
    }

    public EstabelecimentoEndereco carregar(Estabelecimento estabelecimento) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ID, ID_ESTABELECIMENTO, LOGRADOURO, NUMERO, CIDADE, ESTADO, ");
        sql.append("        LATITUDE, LONGITUDE, GEO_REFERENCIADO ");
        sql.append("   FROM ESTABELECIMENTO_ENDERECO ");
        sql.append("  WHERE 1 = 1 ");
        sql.append("   AND ID_ESTABELECIMENTO = ? ");

        List<Object> parameters = new ArrayList<>();
        parameters.add(estabelecimento.getId());

        return super.carregar(sql.toString(), parameters, (ResultSet rs) -> {
            EstabelecimentoEndereco e = new EstabelecimentoEndereco();
            e.setId(rs.getLong("ID"));

            Estabelecimento estabelecimento1 = new Estabelecimento();
            estabelecimento1.setId(rs.getLong("ID_ESTABELECIMENTO"));

            e.setEstabelecimento(estabelecimento1);
            e.setLogradouro(rs.getString("CIDADE"));
            e.setNumero(rs.getString("NUMERO"));
            e.setCidade(rs.getString("CIDADE"));
            e.setEstado(rs.getString("ESTADO"));
            e.setLatitude(rs.getBigDecimal("LATITUDE"));
            e.setLongitude(rs.getBigDecimal("LONGITUDE"));
            e.setGeoReferenciado(rs.getBoolean("GEO_REFERENCIADO"));

            return e;
        });
    }

}
