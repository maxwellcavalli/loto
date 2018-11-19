/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.domain;

import br.com.loto.core.database.annotation.Campo;
import br.com.loto.core.database.annotation.Entidade;
import br.com.loto.core.database.annotation.Relacionamento;
import java.math.BigDecimal;

/**
 *
 * @author maxwe
 */
@Entidade(name = "ESTABELECIMENTO_ENDERECO")
public class EstabelecimentoEndereco {

    @Campo(name = "ID", autoIncrement = true, primaryKey = true)
    private Long id;

    @Relacionamento(relationColumnName = "id_estabelecimento")
    private Estabelecimento estabelecimento;

    @Campo(name = "LOGRADOURO")
    private String logradouro;

    @Campo(name = "NUMERO")
    private String numero;

    @Relacionamento(relationColumnName = "id_cidade")
    private Cidade cidade;

    @Campo(name = "LATITUDE")
    private BigDecimal latitude;

    @Campo(name = "LONGITUDE")
    private BigDecimal longitude;

    @Campo(name = "GEO_REFERENCIADO")
    private boolean geoReferenciado;

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

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public boolean isGeoReferenciado() {
        return geoReferenciado;
    }

    public void setGeoReferenciado(boolean geoReferenciado) {
        this.geoReferenciado = geoReferenciado;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

}
