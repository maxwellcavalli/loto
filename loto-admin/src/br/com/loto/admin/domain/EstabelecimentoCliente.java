/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.domain;

import br.com.loto.core.database.annotation.Campo;
import br.com.loto.core.database.annotation.Entidade;
import br.com.loto.core.database.annotation.Relacionamento;
import java.util.Date;

/**
 *
 * @author maxwe
 */
@Entidade(name = "ESTABELECIMENTO_CLIENTE")
public class EstabelecimentoCliente {

    @Campo(name = "ID", autoIncrement = true, primaryKey = true)
    private Long id;

    @Relacionamento(relationColumnName = "id_estabelecimento")
    private Estabelecimento estabelecimento;

    @Relacionamento(relationColumnName = "id_cliente")
    private Cliente cliente;

    @Campo(name = "ATIVO")
    private boolean ativo = true;

    @Campo(name = "DATA_INATIVACAO")
    private Date dataInativacao;
    
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Date getDataInativacao() {
        return dataInativacao;
    }

    public void setDataInativacao(Date dataInativacao) {
        this.dataInativacao = dataInativacao;
    }

}
