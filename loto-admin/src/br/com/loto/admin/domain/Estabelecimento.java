/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.domain;

import br.com.loto.core.database.annotation.Campo;
import br.com.loto.core.database.annotation.Entidade;
import java.util.List;

/**
 *
 * @author maxwe
 */
@Entidade(name = "ESTABELECIMENTO")
public class Estabelecimento {

    @Campo(name = "ID", autoIncrement = true, primaryKey = true)
    private Long id;

    @Campo(name = "DESCRICAO")
    private String descricao = "";

    @Campo(name = "ATIVO")
    private boolean ativo  = true;

    private EstabelecimentoEndereco estabelecimentoEndereco;

    private List<EstabelecimentoEquipamento> equipamentos;

    public List<EstabelecimentoEquipamento> getEquipamentos() {
        return equipamentos;
    }

    public void setEquipamentos(List<EstabelecimentoEquipamento> equipamentos) {
        this.equipamentos = equipamentos;
    }

    public EstabelecimentoEndereco getEstabelecimentoEndereco() {
        return estabelecimentoEndereco;
    }

    public void setEstabelecimentoEndereco(EstabelecimentoEndereco estabelecimentoEndereco) {
        this.estabelecimentoEndereco = estabelecimentoEndereco;
    }

    public String getAtivoStr() {
        return this.ativo == true ? "Sim" : "Não";
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
