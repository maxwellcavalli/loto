/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.server.tv.business.service;

import br.com.loto.server.tv.business.dao.DeployDAO;
import br.com.loto.shared.DeployDTO;
import br.com.loto.shared.DeployPropagandaDTO;
import br.com.loto.shared.domain.type.AcaoDeploy;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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

    public DeployDTO loadDeployByUuid(String uuid, List<String> uuidsInClient) throws SQLException {
        DeployDTO deployDTO = DeployDAO.getInstance().loadDeployByUuid(uuid);
        if (deployDTO != null) {
            List<DeployPropagandaDTO> list = DeployPropagandaService.getInstance().loadDeployPropaganda(deployDTO.getId());
            
            uuidsInClient.forEach(uuidClient -> {
                Optional<DeployPropagandaDTO> op = list.stream().filter(dp -> dp.getUuidPropaganda().equals(uuidClient)).findFirst();
                if (op.isPresent()){
                    op.get().setAcao(AcaoDeploy.NADA.getCode());
                    op.get().setConteudo(null);
                } else {
                    DeployPropagandaDTO dpE = new DeployPropagandaDTO();
                    dpE.setUuidPropaganda(uuidClient);
                    dpE.setAcao(AcaoDeploy.EXCLUSAO.getCode());
                    
                    list.add(dpE);
                    
                }
            });
            
            deployDTO.setPropagandas(list);
        }
        return deployDTO;
    }

    public void updateDeploy(String uuid) throws SQLException {
        DeployDAO.getInstance().updateDeploy(uuid);
    }

}
