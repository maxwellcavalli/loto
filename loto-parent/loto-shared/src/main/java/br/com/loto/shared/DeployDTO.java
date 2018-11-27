/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class DeployDTO {

    private Long id;
    private String numSerie;
    private String uuid;
    private Date dataValidade;
    private String uuidDeploy;

    private List<DeployPropagandaDTO> propagandas;

    public DeployDTO() {
        this.propagandas = new ArrayList<>();
    }

    public String getNumSerie() {
        return numSerie;
    }

    public void setNumSerie(String numSerie) {
        this.numSerie = numSerie;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(Date dataValidade) {
        this.dataValidade = dataValidade;
    }

    public List<DeployPropagandaDTO> getPropagandas() {
        return propagandas;
    }

    public void setPropagandas(List<DeployPropagandaDTO> propagandas) {
        this.propagandas = propagandas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuidDeploy() {
        return uuidDeploy;
    }

    public void setUuidDeploy(String uuidDeploy) {
        this.uuidDeploy = uuidDeploy;
    }

}
