/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.server.tv.business.dao;

import br.com.loto.core.dao.BaseDAO;
import br.com.loto.shared.DeployPropagandaDTO;
import br.com.loto.shared.domain.type.AcaoDeploy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

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
        sql.append("        _p.nome_arquivo, ");
        sql.append("        _p.uuid as _p_uuid ");

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

            String fileName = rs.getString("nome_arquivo");
            fileName = deAccent(fileName);
            fileName = fileName.replaceAll("\\s", "");

            DeployPropagandaDTO dp = new DeployPropagandaDTO();
            dp.setConteudo(sConteudo);
            dp.setDuracaoPropaganda(rs.getInt("duracao_propaganda"));
            dp.setDuracaoTransicao(rs.getInt("duracao_transicao"));
            dp.setNomeArquivo(fileName);
            dp.setTipoMidia(rs.getInt("id_tipo_midia"));
            dp.setTipoTransicao(rs.getInt("id_tipo_transicao"));
            dp.setOrdem(rs.getInt("ordem"));
            dp.setUuidPropaganda(rs.getString("_p_uuid"));
            dp.setAcao(AcaoDeploy.INSERCAO.getCode());
            
            return dp;
        });

    }

    public static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

}
