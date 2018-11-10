/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.service;

import br.com.loto.admin.dao.DeployDAO;
import br.com.loto.admin.domain.Deploy;
import br.com.loto.admin.domain.DeployPropaganda;
import br.com.loto.core.util.JdbcUtil;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class DeployService {

    private static DeployService instance;

    public static DeployService getInstance() {
        if (instance == null) {
            instance = new DeployService();
        }

        return instance;
    }

    private DeployService() {
    }

    public Deploy persistir(Deploy deploy, List<DeployPropaganda> deployPropagandas) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        deploy = DeployDAO.getInstance().persistir(deploy);
        deployPropagandas = DeployPropagandaService.getInstance().persistir(deploy, deployPropagandas);

        deploy.setDeployPropagandas(deployPropagandas);

        JdbcUtil.getInstance().commit();
        return deploy;
    }

    public List<Deploy> pesquisar(String descricao, Long estado, Long cidade, Long estabelecimento) throws SQLException {
        return DeployDAO.getInstance().pesquisar(descricao, estado, cidade, estabelecimento);
    }

}
