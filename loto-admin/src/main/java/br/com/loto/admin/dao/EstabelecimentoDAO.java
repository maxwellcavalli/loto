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
    
    public Estabelecimento carregar(Long estabelecimento) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ");
        sql.append("    _e.ID as _e_ID, ");
        sql.append("    _e.DESCRICAO as _e_DESCRICAO, ");
        sql.append("    _e.ATIVO as _e_ATIVO, ");
        
        sql.append("   _cid.ID AS _cid_ID, ");
        sql.append("   _cid.NOME AS _cid_NOME, ");
        
        sql.append("   _est.ID AS _est_ID, ");
        sql.append("   _est.NOME AS _est_NOME, ");
        sql.append("   _est.SIGLA AS _est_SIGLA, ");
        
        sql.append("   _ee.id as _ee_id, ");
        sql.append("   _ee.logradouro as _ee_logradouro, ");
        sql.append("   _ee.numero as _ee_numero ");
        
        sql.append("   FROM ESTABELECIMENTO _e");
        sql.append("   LEFT JOIN ESTABELECIMENTO_ENDERECO _ee ON _ee.id_estabelecimento = _e.ID ");
        sql.append("   LEFT JOIN CIDADE _cid ON _cid.id = _ee.id_cidade ");
        sql.append("   LEFT JOIN ESTADO _est ON _est.id = _cid.id_estado ");
        sql.append("  WHERE _e.id = ? ");
        
        List<Object> parameters = new ArrayList<>();
        parameters.add(estabelecimento);
        
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
            
            EstabelecimentoEndereco ee = new EstabelecimentoEndereco();
            ee.setId(rs.getLong("_ee_id"));
            ee.setLogradouro(rs.getString("_ee_logradouro"));
            ee.setNumero(rs.getString("_ee_numero"));
            ee.setCidade(c);
            
            Estabelecimento e = new Estabelecimento();
            e.setId(rs.getLong("_e_ID"));
            e.setDescricao(rs.getString("_e_DESCRICAO"));
            e.setAtivo(rs.getBoolean("_e_ATIVO"));
            e.setEstabelecimentoEndereco(ee);
            
            return e;
        });
    }
    
    public List<Estabelecimento> pesquisar(String descricao, Long estado, Long cidade, Integer maxValue) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT _e.ID, _e.DESCRICAO, _e.ATIVO, ");
        
        sql.append("   _cid.ID AS _cid_ID, ");
        sql.append("   _cid.NOME AS _cid_NOME, ");
        
        sql.append("   _est.ID AS _est_ID, ");
        sql.append("   _est.NOME AS _est_NOME, ");
        sql.append("   _est.SIGLA AS _est_SIGLA, ");
        
        sql.append("   _ee.id as _ee_id, ");
        sql.append("   _ee.logradouro as _ee_logradouro, ");
        sql.append("   _ee.numero as _ee_numero ");
        
        sql.append("   FROM ESTABELECIMENTO _e");
        sql.append("   LEFT JOIN ESTABELECIMENTO_ENDERECO _ee ON _ee.id_estabelecimento = _e.ID ");
        sql.append("   LEFT JOIN CIDADE _cid ON _cid.id = _ee.id_cidade ");
        sql.append("   LEFT JOIN ESTADO _est ON _est.id = _cid.id_estado ");
        sql.append("  WHERE 1 = 1 ");
        
        List<Object> parameters = new ArrayList<>();
        
        if (descricao != null && !descricao.trim().isEmpty()) {
            sql.append("  AND UPPER(_e.DESCRICAO) LIKE ? ");
            parameters.add("%" + descricao.toUpperCase() + "%");
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
            e.setId(rs.getLong("ID"));
            e.setDescricao(rs.getString("DESCRICAO"));
            e.setAtivo(rs.getBoolean("ATIVO"));
            e.setEstabelecimentoEndereco(ee);
            return e;
        }, maxValue);
    }
    
}
