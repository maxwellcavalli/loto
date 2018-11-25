/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.service;

import br.com.loto.admin.dao.DeployDAO;
import br.com.loto.admin.domain.Deploy;
import br.com.loto.admin.domain.DeployPropaganda;
import br.com.loto.admin.domain.Estabelecimento;
import br.com.loto.admin.domain.type.SituacaoDeploy;
import br.com.loto.core.util.JdbcUtil;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
        if (deploy.getUuid() == null){
            deploy.setUuid(UUID.randomUUID().toString());
        }
        
        if (deploy.getId() == null){
            Estabelecimento estabelecimento = deploy.getEstabelecimento();

            Integer ultVersao = ultimaVersao(estabelecimento.getId());
            if (ultVersao == null){
                ultVersao = 0;
            }

            ultVersao++;
            deploy.setVersao(ultVersao);
        }
        
        deploy = DeployDAO.getInstance().persistir(deploy);
        deployPropagandas = DeployPropagandaService.getInstance().persistir(deploy, deployPropagandas);

        deploy.setDeployPropagandas(deployPropagandas);

        JdbcUtil.getInstance().commit();
        return deploy;
    }

    public List<Deploy> pesquisar(String descricao, Long estado, Long cidade, Long estabelecimento, Integer situacao, boolean somenteUltVersao) throws SQLException {
        return DeployDAO.getInstance().pesquisar(descricao, estado, cidade, estabelecimento, situacao, somenteUltVersao);
    }
    
    public Deploy clonar(Deploy deploy) throws SQLException, CloneNotSupportedException, CloneNotSupportedException, Exception{
       Integer versaoDB = ultimaVersao(deploy.getEstabelecimento().getId());
       if (deploy.getVersao() < versaoDB){
           throw new Exception("Para clonar um registro, o mesmo deverá estar na última versão");
       }
        
        Deploy newDeploy = (Deploy) deploy.clone();
        newDeploy.setData(new Date());
        newDeploy.setAtivo(true);
        newDeploy.setDataValidade(null);
        newDeploy.setSituacao(SituacaoDeploy.CADASTRANDO.getKey());
        newDeploy.setUuid(null);
        newDeploy.setId(null);
        newDeploy.setVersao(deploy.getVersao() + 1);
        
        List<DeployPropaganda> propagandas = DeployPropagandaService.getInstance().pesquisar(deploy);

        List<DeployPropaganda> newPropagandas = new ArrayList<>();
        for (DeployPropaganda dp : propagandas){
            DeployPropaganda newDp = (DeployPropaganda) dp.clone();
            newDp.setId(null);
            newDp.setDeploy(newDeploy);
            newPropagandas.add(newDp);
        }
        
        
        newDeploy.setDeployPropagandas(newPropagandas);
        
        return newDeploy;
    }
    
    public Integer ultimaVersao(Long estabelecimento) throws SQLException {
        return DeployDAO.getInstance().ultimaVersao(estabelecimento);
    }

}
