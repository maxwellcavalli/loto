/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.server.tv.business.service;

import br.com.loto.server.tv.business.dao.DeployDAO;
import br.com.loto.shared.DeployDTO;
import br.com.loto.shared.DeployPropagandaDTO;
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

    public DeployDTO loadLastDeployByUuid(String uuid) throws SQLException {
        DeployDTO deployDTO = DeployDAO.getInstance().loadLastDeployByUuid(uuid);
        if (deployDTO != null) {
            List<DeployPropagandaDTO> list = DeployPropagandaService.getInstance().loadDeployPropaganda(deployDTO.getId());
            deployDTO.setPropagandas(list);
        }
        return deployDTO;
    }

    public DeployDTO loadDeployByUuid(String uuid) throws SQLException {
        DeployDTO deployDTO = DeployDAO.getInstance().loadDeployByUuid(uuid);
        if (deployDTO != null) {
            List<DeployPropagandaDTO> list = DeployPropagandaService.getInstance().loadDeployPropaganda(deployDTO.getId());
            deployDTO.setPropagandas(list);
        }
        return deployDTO;
    }

    public void updateDeploy(String uuid) throws SQLException {
        DeployDAO.getInstance().updateDeploy(uuid);
    }

}
