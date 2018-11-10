
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
@Entidade(name = "CLIENTE_PROPAGANDA")
public class ClientePropaganda {

    @Campo(name = "ID", autoIncrement = true, primaryKey = true)
    private Long id;

    @Relacionamento(relationColumnName = "id_cliente")
    private Cliente cliente;

    @Relacionamento(relationColumnName = "id_propaganda")
    private Propaganda propaganda;
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Propaganda getPropaganda() {
        return propaganda;
    }

    public void setPropaganda(Propaganda propaganda) {
        this.propaganda = propaganda;
    }

}
