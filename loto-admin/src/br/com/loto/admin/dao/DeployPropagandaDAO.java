/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.dao;

import br.com.loto.admin.domain.Cliente;
import br.com.loto.admin.domain.ClientePropaganda;
import br.com.loto.admin.domain.Deploy;
import br.com.loto.admin.domain.DeployPropaganda;
import br.com.loto.core.dao.BaseDAO;
import br.com.loto.admin.domain.Propaganda;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class DeployPropagandaDAO extends BaseDAO<DeployPropaganda> {

    private static DeployPropagandaDAO instance;

    private DeployPropagandaDAO() {
    }

    public static DeployPropagandaDAO getInstance() {
        if (instance == null) {
            instance = new DeployPropagandaDAO();
        }

        return instance;
    }

    @Override
    public DeployPropaganda persistir(DeployPropaganda t) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        return super.persistir(t); //To change body of generated methods, choose Tools | Templates.
    }

    public List<DeployPropaganda> pesquisar(Deploy deploy) throws SQLException {
        return pesquisar(deploy, true);
    }

    public List<DeployPropaganda> pesquisar(Deploy deploy, boolean rollbackAfterRun) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");
        sql.append("    _dp.id as _dp_id, ");
        sql.append("    _dp.id_tipo_transicao as _dp_id_tipo_transicao, ");
        sql.append("    _dp.duracao_transicao as _dp_duracao_transicao, ");
        sql.append("    _dp.duracao_propaganda as _dp_duracao_propaganda, ");
        sql.append("    _dp.id_tipo_midia as _dp_id_tipo_midia, ");
        sql.append("    _dp.ordem as _dp_ordem, ");
        sql.append("    _dp.id_deploy as _dp_id_deploy, ");
        sql.append("    _cp.id as _cp_id, ");

        sql.append("    _prop.id as _prop_id, ");
        sql.append("    _prop.descricao as _prop_descricao, ");
        
        sql.append("    _cli.id as _cli_id, ");
        sql.append("    _cli.nome as _cli_nome ");

        sql.append("   from deploy_propaganda _dp ");
        sql.append("  inner join cliente_propaganda _cp on _cp.id = _dp.id_cliente_propaganda ");
        sql.append("  inner join propaganda _prop on _prop.id = _cp.id_propaganda ");
        sql.append("  inner join cliente _cli on _cli.id = _cp.id_cliente "); 
        sql.append("  where _dp.id_deploy = ? ");

        List<Object> parameters = new ArrayList<>();
        parameters.add(deploy.getId());

        return super.pesquisar(sql.toString(), parameters, (ResultSet rs) -> {
            Propaganda p = new Propaganda();
            p.setId(rs.getLong("_prop_id"));
            p.setDescricao(rs.getString("_prop_descricao"));

            Cliente c = new Cliente();
            c.setId(rs.getLong("_cli_id"));
            c.setNome(rs.getString("_cli_nome"));
            
            ClientePropaganda cp = new ClientePropaganda();
            cp.setId(rs.getLong("_cp_id"));
            cp.setPropaganda(p);
            cp.setCliente(c);

            Deploy d = new Deploy();
            d.setId(rs.getLong("_dp_id_deploy"));

            DeployPropaganda dp = new DeployPropaganda();
            dp.setId(rs.getLong("_dp_id"));
            dp.setIdTipoTransicao(rs.getInt("_dp_id_tipo_transicao"));
            dp.setIdTipoMidia(rs.getInt("_dp_id_tipo_midia"));
            dp.setDuracaoPropaganda(rs.getInt("_dp_duracao_propaganda"));
            dp.setDuracaoTransicao(rs.getInt("_dp_duracao_transicao"));
            dp.setOrdem(rs.getInt("_dp_ordem"));
            dp.setClientePropaganda(cp);

            dp.setDeploy(d);

            return dp;
        }, rollbackAfterRun);
    }

    @Override
    public void delete(DeployPropaganda t) throws SQLException, IllegalAccessException, Exception {
        super.delete(t); //To change body of generated methods, choose Tools | Templates.
    }

}
