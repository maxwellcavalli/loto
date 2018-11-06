
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
@Entidade(name = "ESTADO")
public class Estado {

    @Campo(name = "ID", autoIncrement = true, primaryKey = true)
    private Long id;

    @Campo(name = "NOME")
    private String nome = "";

    @Campo(name = "SIGLA")
    private String sigla = "";

    @Campo(name = "ATIVO")
    private boolean ativo = true;

    public String getAtivoStr() {
        return this.ativo == true ? "Sim" : "Não";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

}
