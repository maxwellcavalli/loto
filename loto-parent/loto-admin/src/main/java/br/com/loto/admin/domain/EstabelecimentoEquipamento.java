/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.domain;

import br.com.loto.core.database.annotation.Campo;
import br.com.loto.core.database.annotation.Entidade;
import br.com.loto.core.database.annotation.Relacionamento;

/**
 *
 * @author maxwe
 */
@Entidade(name = "ESTABELECIMENTO_EQUIPAMENTO")
public class EstabelecimentoEquipamento {

    @Campo(name = "ID", autoIncrement = true, primaryKey = true)
    private Long id;

    @Relacionamento(relationColumnName = "id_estabelecimento")
    private Estabelecimento estabelecimento;

    @Relacionamento(relationColumnName = "id_equipamento")
    private Equipamento equipamento;

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

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }

}
