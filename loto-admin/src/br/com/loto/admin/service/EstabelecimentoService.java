/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.service;

import br.com.loto.admin.dao.EstabelecimentoDAO;
import br.com.loto.core.util.JdbcUtil;
import br.com.loto.admin.domain.Estabelecimento;
import br.com.loto.admin.domain.EstabelecimentoEndereco;
import br.com.loto.admin.domain.EstabelecimentoEquipamento;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class EstabelecimentoService {
    
    private static EstabelecimentoService instance;
    
    public static EstabelecimentoService getInstance() {
        if (instance == null) {
            instance = new EstabelecimentoService();
        }
        
        return instance;
    }
    
    private EstabelecimentoService() {
    }
    
    public Estabelecimento persistir(Estabelecimento estabelecimento,
            EstabelecimentoEndereco estabelecimentoEndereco,
            List<EstabelecimentoEquipamento> equipamentos) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        
        Connection conn = JdbcUtil.getInstance().getConnection();
        estabelecimento = EstabelecimentoDAO.getInstance().persistir(estabelecimento);
        
        estabelecimentoEndereco.setEstabelecimento(estabelecimento);
        estabelecimentoEndereco = EstabelecimentoEnderecoService.getInstance().persistir(estabelecimentoEndereco);
        estabelecimento.setEstabelecimentoEndereco(estabelecimentoEndereco);
        
        equipamentos = EstabelecimentoEquipamentoService.getInstance().persistir(estabelecimento, equipamentos);
        estabelecimento.setEquipamentos(equipamentos);
        
        conn.commit();
        return estabelecimento;
    }
    
    public List<Estabelecimento> pesquisar(String descricao, Long estado, Long cidade) throws SQLException {
        return EstabelecimentoDAO.getInstance().pesquisar(descricao, estado, cidade);
    }
    
}
