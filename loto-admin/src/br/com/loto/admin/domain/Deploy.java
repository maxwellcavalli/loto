
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.domain;

import br.com.loto.admin.domain.type.SituacaoDeploy;
import br.com.loto.core.database.annotation.Campo;
import br.com.loto.core.database.annotation.Entidade;
import br.com.loto.core.database.annotation.Relacionamento;
import java.util.Date;
import java.util.List;

/**
 *
 * @author maxwe
 */
@Entidade(name = "DEPLOY")
public class Deploy {

    @Campo(name = "ID", autoIncrement = true, primaryKey = true)
    private Long id;

    @Relacionamento(relationColumnName = "id_estabelecimento")
    private Estabelecimento estabelecimento;

    @Campo(name = "DESCRICAO")
    private String descricao = "";

    @Campo(name = "ATIVO")
    private boolean ativo = true;

    @Campo(name = "DATA")
    private Date data = new Date();

    @Campo(name = "DATA_VALIDADE")
    private Date dataValidade;

    @Campo(name = "SITUACAO")
    private Integer situacao = SituacaoDeploy.CADASTRANDO.getKey();

    //usado para retorno
    private List<DeployPropaganda> deployPropagandas;

    public String getAtivoStr() {
        return this.ativo == true ? "Sim" : "Não";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Estabelecimento getEstabelecimento() {
        return estabelecimento;
    }

    public void setEstabelecimento(Estabelecimento estabelecimento) {
        this.estabelecimento = estabelecimento;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Date getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(Date dataValidade) {
        this.dataValidade = dataValidade;
    }

    public List<DeployPropaganda> getDeployPropagandas() {
        return deployPropagandas;
    }

    public void setDeployPropagandas(List<DeployPropaganda> deployPropagandas) {
        this.deployPropagandas = deployPropagandas;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Integer getSituacao() {
        return situacao;
    }

    public void setSituacao(Integer situacao) {
        this.situacao = situacao;
    }

}
