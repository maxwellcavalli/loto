/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.dao;

import br.com.loto.admin.domain.Cidade;
import br.com.loto.core.dao.BaseDAO;
import br.com.loto.admin.domain.Estabelecimento;
import br.com.loto.admin.domain.EstabelecimentoEndereco;
import br.com.loto.admin.domain.Estado;
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
        sql.append(" SELECT _e.ID as _e_ID, ");
        sql.append("        _e.ID_ESTABELECIMENTO AS _e_ID_ESTABELECIMENTO, ");
        sql.append("        _e.LOGRADOURO AS _e_LOGRADOURO, ");
        sql.append("        _e.NUMERO AS _e_NUMERO, ");
        sql.append("        _e.LATITUDE AS _e_LATITUDE, ");
        sql.append("        _e.LONGITUDE AS _e_LONGITUDE, ");
        sql.append("        _e.GEO_REFERENCIADO AS _e_GEO_REFERENCIADO, ");
        
        sql.append("        _cid.ID AS _cid_ID, ");
        sql.append("        _cid.NOME AS _cid_NOME, ");
        
        sql.append("        _est.ID AS _est_ID, ");
        sql.append("        _est.NOME AS _est_NOME, ");
        sql.append("        _est.SIGLA AS _est_SIGLA ");
        
        sql.append("   FROM ESTABELECIMENTO_ENDERECO _e ");
        sql.append("   LEFT JOIN CIDADE _cid ON _cid.ID = _e.ID_CIDADE ");
        sql.append("   LEFT JOIN ESTADO _est ON _est.ID = _cid.ID_ESTADO ");        
        sql.append("  WHERE 1 = 1 ");
        sql.append("   AND _e.ID_ESTABELECIMENTO = ? ");
        
        List<Object> parameters = new ArrayList<>();
        parameters.add(estabelecimento.getId());
        
        return super.carregar(sql.toString(), parameters, (ResultSet rs) -> {
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
            
            EstabelecimentoEndereco e = new EstabelecimentoEndereco();
            e.setId(rs.getLong("_e_ID"));
            
            Estabelecimento estabelecimento1 = new Estabelecimento();
            estabelecimento1.setId(rs.getLong("_e_ID_ESTABELECIMENTO"));
            
            e.setEstabelecimento(estabelecimento1);
            e.setLogradouro(rs.getString("_e_LOGRADOURO"));
            e.setNumero(rs.getString("_e_NUMERO"));
            e.setLatitude(rs.getBigDecimal("_e_LATITUDE"));
            e.setLongitude(rs.getBigDecimal("_e_LONGITUDE"));
            e.setGeoReferenciado(rs.getBoolean("_e_GEO_REFERENCIADO"));
            
            e.setCidade(c);
            
            return e;
        });
    }
    
}
