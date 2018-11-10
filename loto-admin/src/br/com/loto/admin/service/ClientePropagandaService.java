/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.service;

import br.com.loto.admin.dao.ClientePropagandaDAO;
import br.com.loto.admin.domain.Cliente;
import br.com.loto.admin.domain.ClientePropaganda;
import br.com.loto.admin.domain.Propaganda;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class ClientePropagandaService {

    private static ClientePropagandaService instance;

    public static ClientePropagandaService getInstance() {
        if (instance == null) {
            instance = new ClientePropagandaService();
        }

        return instance;
    }

    private ClientePropagandaService() {
    }

    public List<ClientePropaganda> persistir(Cliente cliente, List<ClientePropaganda> listClientePropaganda) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        List<ClientePropaganda> listDB = pesquisar(cliente, "", false);

        for (ClientePropaganda clientePropagandaDB : listDB) {
            boolean exists = false;

            for (ClientePropaganda clientePropaganda : listClientePropaganda) {
                if (clientePropaganda.getId() == null || clientePropaganda.getId().longValue() == 0) {
                    continue;
                }

                if (clientePropaganda.getId().longValue() == clientePropagandaDB.getId().longValue()) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                delete(clientePropagandaDB);
            }
        }

        List<ClientePropaganda> ret = new ArrayList<>();

        for (ClientePropaganda clientePropaganda : listClientePropaganda) {
            Propaganda propaganda = clientePropaganda.getPropaganda();
            propaganda = PropagandaService.getInstance().persistir(propaganda);

            clientePropaganda.setCliente(cliente);
            clientePropaganda.setPropaganda(propaganda);

            ClientePropagandaDAO.getInstance().persistir(clientePropaganda);

            ret.add(clientePropaganda);
        }

        return ret;
    }

    public void delete(ClientePropaganda clientePropaganda) throws IllegalAccessException, Exception {
        ClientePropagandaDAO.getInstance().delete(clientePropaganda);

        Propaganda propaganda = clientePropaganda.getPropaganda();
        PropagandaService.getInstance().delete(propaganda);
    }

    public List<ClientePropaganda> pesquisar(Cliente cliente, String propaganda, boolean rollbackAfterRun) throws SQLException {
        return ClientePropagandaDAO.getInstance().pesquisar(cliente, propaganda, rollbackAfterRun, Integer.MAX_VALUE);
    }
    
    public List<ClientePropaganda> pesquisar(Cliente cliente, String propaganda, Integer maxResults) throws SQLException {
        return ClientePropagandaDAO.getInstance().pesquisar(cliente, propaganda, true, maxResults);
    }

    public List<ClientePropaganda> pesquisar(Cliente cliente) throws SQLException {
        return ClientePropagandaDAO.getInstance().pesquisar(cliente);
    }
}
