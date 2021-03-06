/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.shared;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class ResultadoLoteriaDTO {

    private Long id;
    private Integer concurso;
    private BigDecimal valorAcumulado;
    private Integer idTipoLoteria;
    private List<Integer> numeros;

    public List<Integer> getNumeros() {
        return numeros;
    }

    public void setNumeros(List<Integer> numeros) {
        this.numeros = numeros;
    }

    public Integer getConcurso() {
        return concurso;
    }

    public void setConcurso(Integer concurso) {
        this.concurso = concurso;
    }

    public BigDecimal getValorAcumulado() {
        return valorAcumulado;
    }

    public void setValorAcumulado(BigDecimal valorAcumulado) {
        this.valorAcumulado = valorAcumulado;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setIdTipoLoteria(Integer idTipoLoteria) {
        this.idTipoLoteria = idTipoLoteria;
    }

    public Integer getIdTipoLoteria() {
        return idTipoLoteria;
    }

}
