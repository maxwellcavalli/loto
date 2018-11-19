/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.server.tv.business.service;

import br.com.loto.server.tv.business.dao.DeployPropagandaDAO;
import br.com.loto.shared.DeployPropagandaDTO;
import java.sql.SQLException;
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

    public List<DeployPropagandaDTO> loadDeployPropaganda(Long deploy) throws SQLException {
        return DeployPropagandaDAO.getInstance().loadDeployPropaganda(deploy);
    }

}
