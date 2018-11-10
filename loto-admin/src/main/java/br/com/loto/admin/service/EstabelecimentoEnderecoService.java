/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.service;

import br.com.loto.admin.dao.EstabelecimentoEnderecoDAO;
import br.com.loto.admin.domain.Estabelecimento;
import br.com.loto.admin.domain.EstabelecimentoEndereco;
import java.sql.SQLException;

/**
 *
 * @author maxwe
 */
public class EstabelecimentoEnderecoService {

    private static EstabelecimentoEnderecoService instance;

    public static EstabelecimentoEnderecoService getInstance() {
        if (instance == null) {
            instance = new EstabelecimentoEnderecoService();
        }

        return instance;
    }

    private EstabelecimentoEnderecoService() {
    }

    public EstabelecimentoEndereco persistir(EstabelecimentoEndereco estabelecimentoEndereco) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {

        estabelecimentoEndereco = EstabelecimentoEnderecoDAO.getInstance().persistir(estabelecimentoEndereco);

        return estabelecimentoEndereco;
    }

    public EstabelecimentoEndereco carregar(Estabelecimento estabelecimento) throws SQLException {
        return EstabelecimentoEnderecoDAO.getInstance().carregar(estabelecimento);
    }

}
