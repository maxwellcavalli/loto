/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.shared;

/**
 *
 * @author maxwe
 */
public class DeployPropagandaDTO {

    private Integer duracaoPropaganda;
    private Integer duracaoTransicao;
    private Integer tipoMidia;
    private Integer tipoTransicao;
    private Integer ordem;
    private String conteudo;
    private String nomeArquivo;

    private String uuidPropaganda;
    private Integer acao;

    public Integer getDuracaoPropaganda() {
        return duracaoPropaganda;
    }

    public void setDuracaoPropaganda(Integer duracaoPropaganda) {
        this.duracaoPropaganda = duracaoPropaganda;
    }

    public Integer getDuracaoTransicao() {
        return duracaoTransicao;
    }

    public void setDuracaoTransicao(Integer duracaoTransicao) {
        this.duracaoTransicao = duracaoTransicao;
    }

    public Integer getTipoMidia() {
        return tipoMidia;
    }

    public void setTipoMidia(Integer tipoMidia) {
        this.tipoMidia = tipoMidia;
    }

    public Integer getTipoTransicao() {
        return tipoTransicao;
    }

    public void setTipoTransicao(Integer tipoTransicao) {
        this.tipoTransicao = tipoTransicao;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getUuidPropaganda() {
        return uuidPropaganda;
    }

    public void setUuidPropaganda(String uuidPropaganda) {
        this.uuidPropaganda = uuidPropaganda;
    }

    public Integer getAcao() {
        return acao;
    }

    public void setAcao(Integer acao) {
        this.acao = acao;
    }

}
