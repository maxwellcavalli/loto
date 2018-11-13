
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.domain;

import br.com.loto.core.database.annotation.Campo;
import br.com.loto.core.database.annotation.Entidade;
import java.math.BigDecimal;

/**
 *
 * @author maxwe
 */
@Entidade(name = "RESULTADO_LOTERIA")
public class ResultadoLoteria {

    @Campo(name = "ID", autoIncrement = true, primaryKey = true)
    private Long id;

    @Campo(name = "NOME")
    private String nome = "";

    @Campo(name = "CONCURSO")
    private Integer concurso;

    @Campo(name = "VALOR_ACUMULADO")
    private BigDecimal valorAcumulado;

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

}
