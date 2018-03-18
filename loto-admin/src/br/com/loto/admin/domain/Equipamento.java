/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.domain;

import br.com.loto.core.database.annotation.Campo;
import br.com.loto.core.database.annotation.Entidade;

/**
 *
 * @author maxwe
 */
@Entidade(name = "EQUIPAMENTO")
public class Equipamento {

    @Campo(name = "ID", autoIncrement = true, primaryKey = true)
    private Long id;

    @Campo(name = "NUM_SERIE")
    private String serial;

    @Campo(name = "DESCRICAO")
    private String descricao;

    @Campo(name = "ATIVO")
    private boolean ativo;
    
    public String getAtivoStr(){
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

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
