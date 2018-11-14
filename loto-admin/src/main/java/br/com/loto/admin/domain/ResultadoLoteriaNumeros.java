
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
@Entidade(name = "RESULTADO_LOTERIA_NUMEROS")
public class ResultadoLoteriaNumeros {

    @Campo(name = "ID", autoIncrement = true, primaryKey = true)
    private Long id;

    @Relacionamento(relationColumnName = "ID_RESULTADO_LOTERIA")
    private ResultadoLoteria resultadoLoteria;

    @Campo(name = "NUMERO")
    private Integer numero;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResultadoLoteria getResultadoLoteria() {
        return resultadoLoteria;
    }

    public void setResultadoLoteria(ResultadoLoteria resultadoLoteria) {
        this.resultadoLoteria = resultadoLoteria;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

}
