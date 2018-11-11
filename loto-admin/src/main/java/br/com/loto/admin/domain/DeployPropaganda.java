
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.domain;

import br.com.loto.admin.domain.type.TipoMidia;
import br.com.loto.admin.domain.type.TipoTransicao;
import br.com.loto.core.database.annotation.Campo;
import br.com.loto.core.database.annotation.Entidade;
import br.com.loto.core.database.annotation.Relacionamento;

/**
 *
 * @author maxwe
 */
@Entidade(name = "DEPLOY_PROPAGANDA")
public class DeployPropaganda {

    @Campo(name = "ID", autoIncrement = true, primaryKey = true)
    private Long id;

    @Relacionamento(relationColumnName = "id_deploy")
    private Deploy deploy;

    @Relacionamento(relationColumnName = "id_cliente_propaganda")
    private ClientePropaganda clientePropaganda;

    @Campo(name = "ORDEM")
    private Integer ordem;

    @Campo(name = "ID_TIPO_TRANSICAO")
    private Integer idTipoTransicao = TipoTransicao.FADE_IN.getKey();

    @Campo(name = "DURACAO_TRANSICAO")
    private Integer duracaoTransicao = 500;

    @Campo(name = "DURACAO_PROPAGANDA")
    private Integer duracaoPropaganda = 5000;

    @Campo(name = "ID_TIPO_MIDIA")
    private Integer idTipoMidia = TipoMidia.ESTATICA.getKey();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Deploy getDeploy() {
        return deploy;
    }

    public void setDeploy(Deploy deploy) {
        this.deploy = deploy;
    }

    public ClientePropaganda getClientePropaganda() {
        return clientePropaganda;
    }

    public void setClientePropaganda(ClientePropaganda clientePropaganda) {
        this.clientePropaganda = clientePropaganda;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Integer getIdTipoTransicao() {
        return idTipoTransicao;
    }

    public void setIdTipoTransicao(Integer idTipoTransicao) {
        this.idTipoTransicao = idTipoTransicao;
    }

    public Integer getDuracaoTransicao() {
        return duracaoTransicao;
    }

    public void setDuracaoTransicao(Integer duracaoTransicao) {
        this.duracaoTransicao = duracaoTransicao;
    }

    public Integer getDuracaoPropaganda() {
        return duracaoPropaganda;
    }

    public void setDuracaoPropaganda(Integer duracaoPropaganda) {
        this.duracaoPropaganda = duracaoPropaganda;
    }

    public Integer getIdTipoMidia() {
        return idTipoMidia;
    }

    public void setIdTipoMidia(Integer idTipoMidia) {
        this.idTipoMidia = idTipoMidia;
    }

}
