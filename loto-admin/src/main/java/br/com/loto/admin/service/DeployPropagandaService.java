/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.service;

import br.com.loto.admin.dao.ClientePropagandaDAO;
import br.com.loto.admin.dao.DeployPropagandaDAO;
import br.com.loto.admin.domain.ClientePropaganda;
import br.com.loto.admin.domain.Deploy;
import br.com.loto.admin.domain.DeployPropaganda;
import br.com.loto.admin.domain.Propaganda;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class DeployPropagandaService {

    private static DeployPropagandaService instance;

    public static DeployPropagandaService getInstance() {
        if (instance == null) {
            instance = new DeployPropagandaService();
        }

        return instance;
    }

    private DeployPropagandaService() {
    }

    public List<DeployPropaganda> persistir(Deploy deploy, List<DeployPropaganda> deployPropagandas) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        List<DeployPropaganda> listDB = pesquisar(deploy, false);

        for (DeployPropaganda deployPropagandaDB : listDB) {
            boolean exists = false;

            for (DeployPropaganda deployPropaganda : deployPropagandas) {
                if (deployPropaganda.getId() == null || deployPropaganda.getId().longValue() == 0) {
                    continue;
                }

                if (deployPropaganda.getId().longValue() == deployPropagandaDB.getId().longValue()) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                delete(deployPropagandaDB);
            }
        }

        List<DeployPropaganda> ret = new ArrayList<>();

        for (DeployPropaganda deployPropaganda : deployPropagandas) {
            deployPropaganda.setDeploy(deploy);

            deployPropaganda = DeployPropagandaDAO.getInstance().persistir(deployPropaganda);

            ret.add(deployPropaganda);
        }

        Collections.sort(ret, new Comparator<DeployPropaganda>() {
            @Override
            public int compare(DeployPropaganda o1, DeployPropaganda o2) {
                return o1.getOrdem().compareTo(o2.getOrdem());
            }

        });

        return ret;
    }

    public void delete(DeployPropaganda deployPropaganda) throws IllegalAccessException, Exception {
        DeployPropagandaDAO.getInstance().delete(deployPropaganda);
    }

    public List<DeployPropaganda> pesquisar(Deploy deploy, boolean rollbackAfterRun) throws SQLException {
        return DeployPropagandaDAO.getInstance().pesquisar(deploy, rollbackAfterRun);
    }

    public List<DeployPropaganda> pesquisar(Deploy deploy) throws SQLException {
        return DeployPropagandaDAO.getInstance().pesquisar(deploy);
    }
}
