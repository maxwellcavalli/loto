/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.server.tv.business.dao;

import br.com.loto.core.dao.BaseDAO;
import br.com.loto.shared.DeployPropagandaDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class DeployPropagandaDAO extends BaseDAO<DeployPropagandaDTO> {

    private static DeployPropagandaDAO instance;

    private DeployPropagandaDAO() {
    }

    public static DeployPropagandaDAO getInstance() {
        if (instance == null) {
            instance = new DeployPropagandaDAO();
        }

        return instance;
    }

    public List<DeployPropagandaDTO> loadDeployPropaganda(Long deploy) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" ");
        sql.append(" select _dp.duracao_propaganda, ");
        sql.append("        _dp.duracao_transicao, ");
        sql.append("        _dp.id_tipo_midia, ");
        sql.append("        _dp.id_tipo_transicao, ");
        sql.append("        _dp.ordem, ");
        sql.append("        _p.conteudo, ");
        sql.append("        _p.nome_arquivo ");

        sql.append("   from deploy_propaganda _dp ");
        sql.append("  inner join cliente_propaganda _cp on _cp.id = _dp.id_cliente_propaganda ");
        sql.append("  inner join cliente _c on _c.id = _cp.id_cliente ");
        sql.append("  inner join propaganda _p on _p.id = _cp.id_propaganda ");
        sql.append("  where _c.ativo = ? ");
        sql.append("    and _p.ativo = ? ");
        sql.append("    and _dp.id_deploy = ? ");
        sql.append("  order by _dp.ordem ");

        List<Object> parameters = new ArrayList<>();
        parameters.add(true);
        parameters.add(true);
        parameters.add(deploy);

        return super.pesquisar(sql.toString(), parameters, (ResultSet rs) -> {
            byte[] conteudo = rs.getBytes("conteudo");
            String sConteudo = new String(Base64.getEncoder().encode(conteudo));
            
            DeployPropagandaDTO dp = new DeployPropagandaDTO();
            dp.setConteudo(sConteudo);
            dp.setDuracaoPropaganda(rs.getInt("duracao_propaganda"));
            dp.setDuracaoTransicao(rs.getInt("duracao_transicao"));
            dp.setNomeArquivo(rs.getString("nome_arquivo"));
            dp.setTipoMidia(rs.getInt("id_tipo_midia"));
            dp.setTipoTransicao(rs.getInt("id_tipo_transicao"));

            return dp;
        });

    }

}
