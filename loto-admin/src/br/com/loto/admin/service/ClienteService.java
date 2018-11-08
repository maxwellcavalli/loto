/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.service;

import br.com.loto.admin.dao.ClienteDAO;
import br.com.loto.admin.domain.Cliente;
import br.com.loto.admin.domain.ClientePropaganda;
import br.com.loto.core.util.JdbcUtil;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class ClienteService {

    private static ClienteService instance;

    public static ClienteService getInstance() {
        if (instance == null) {
            instance = new ClienteService();
        }

        return instance;
    }

    private ClienteService() {
    }

    public Cliente persistir(Cliente cliente, List<ClientePropaganda> listClientePropaganda) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        cliente = ClienteDAO.getInstance().persistir(cliente);
        listClientePropaganda = ClientePropagandaService.getInstance().persistir(cliente, listClientePropaganda);

        cliente.setListClientePropaganda(listClientePropaganda);

        JdbcUtil.getInstance().commit();
        return cliente;
    }

    public List<Cliente> pesquisar(String nome, Long cidade, Long estado) throws SQLException {
        return ClienteDAO.getInstance().pesquisar(nome, cidade, estado);
    }

    public List<Cliente> pesquisar(String nome, Long cidade, Long estado, Integer maxValues) throws SQLException {
        return ClienteDAO.getInstance().pesquisar(nome, cidade, estado, maxValues);
    }

}
