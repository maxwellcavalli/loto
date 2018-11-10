/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.service;

import br.com.loto.admin.dao.EstabelecimentoClienteDAO;
import br.com.loto.admin.domain.Estabelecimento;
import br.com.loto.admin.domain.EstabelecimentoCliente;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class EstabelecimentoClienteService {

    private static EstabelecimentoClienteService instance;

    public static EstabelecimentoClienteService getInstance() {
        if (instance == null) {
            instance = new EstabelecimentoClienteService();
        }

        return instance;
    }

    private EstabelecimentoClienteService() {
    }

    public List<EstabelecimentoCliente> persistir(Estabelecimento estabelecimento, List<EstabelecimentoCliente> clientes) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        List<EstabelecimentoCliente> listDB = pesquisar(estabelecimento, false);

        for (EstabelecimentoCliente elDB : listDB) {
            boolean exists = clientes.stream().filter(el -> el.getId().longValue() == elDB.getId().longValue()).count() > 0;

            if (!exists) {
                EstabelecimentoClienteDAO.getInstance().delete(elDB);
            }
        }

        for (EstabelecimentoCliente el : clientes) {
            el.setEstabelecimento(estabelecimento);

            el = EstabelecimentoClienteDAO.getInstance().persistir(el);
        }

        return clientes;
    }

    public List<EstabelecimentoCliente> pesquisar(Estabelecimento estabelecimento, boolean rollbackAfterRun) throws SQLException {
        return EstabelecimentoClienteDAO.getInstance().pesquisar(estabelecimento, rollbackAfterRun);
    }

    public List<EstabelecimentoCliente> pesquisar(Estabelecimento estabelecimento) throws SQLException {
        return EstabelecimentoClienteDAO.getInstance().pesquisar(estabelecimento);
    }

}
