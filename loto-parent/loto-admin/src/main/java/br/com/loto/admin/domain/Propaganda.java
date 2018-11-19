/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.domain;

import br.com.loto.core.database.annotation.Campo;
import br.com.loto.core.database.annotation.Entidade;
import java.util.Date;

/**
 *
 * @author maxwe
 */
@Entidade(name = "PROPAGANDA")
public class Propaganda {

    @Campo(name = "ID", autoIncrement = true, primaryKey = true)
    private Long id;

    @Campo(name = "DESCRICAO")
    private String descricao;

    @Campo(name = "ATIVO")
    private boolean ativo;

    @Campo(name = "NOME_ARQUIVO")
    private String nomeArquivo;

    @Campo(name = "CONTEUDO")
    private byte[] conteudo;

    @Campo(name = "DATA")
    private Date data = new Date();

    @Campo(name = "DATA_INATIVACAO")
    private Date dataInativacao;

    @Campo(name = "UUID")
    private String uuid;

    public String getAtivoStr() {
        return this.ativo == true ? "Sim" : "Não";
    }

    public Date getDataInativacao() {
        return dataInativacao;
    }

    public void setDataInativacao(Date dataInativacao) {
        this.dataInativacao = dataInativacao;
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

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public byte[] getConteudo() {
        return conteudo;
    }

    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
