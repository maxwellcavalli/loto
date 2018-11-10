/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.server.tv.business.dao;

import br.com.loto.core.dao.BaseDAO;
import br.com.loto.core.util.JdbcUtil;
import br.com.loto.shared.DeployDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxwe
 */
public class DeployDAO extends BaseDAO<DeployDTO> {

    private static DeployDAO instance;

    private DeployDAO() {
    }

    public static DeployDAO getInstance() {
        if (instance == null) {
            instance = new DeployDAO();
        }

        return instance;
    }

    public DeployDTO loadDeployByUuid(String uuid) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" ");

        sql.append(" select _eq.num_serie, ");
        sql.append("        _eq.uuid, ");
        sql.append("        _d.data_validade, ");
        sql.append("        _d.id, ");
        sql.append("        _d.uuid as _d_uuid ");

        sql.append("  from deploy _d ");
        sql.append(" inner join estabelecimento _e on _e.id = _d.id_estabelecimento ");
        sql.append(" inner join estabelecimento_equipamento _ee on _ee.id_estabelecimento = _e.id ");
        sql.append(" inner join equipamento _eq on _eq.id = _ee.id_equipamento ");

        sql.append(" where 1 = 1 ");
        sql.append("   and _d.situacao = 3 ");
        sql.append("   and _eq.uuid = ? ");
        sql.append("   and _d.ativo = ? ");

        List<Object> parameters = new ArrayList<>();
        parameters.add(uuid);
        parameters.add(true);

        return super.carregar(sql.toString(), parameters, (ResultSet rs) -> {
            DeployDTO d = new DeployDTO();
            d.setId(rs.getLong("id"));
            d.setDataValidade(rs.getTimestamp("data_validade"));
            d.setNumSerie(rs.getString("num_serie"));
            d.setUuid(rs.getString("uuid"));
            d.setUuidDeploy(rs.getString("_d_uuid"));
            return d;
        });
    }

    public void updateDeploy(String uuid) throws SQLException {
        StringBuilder updateCmd = new StringBuilder();
        updateCmd.append(" update deploy ");
        updateCmd.append("   set situacao = 4 ");
        updateCmd.append("  where uuid = ? ");

        try {
            Connection conn = JdbcUtil.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(updateCmd.toString());
            stmt.setString(1, uuid);
            stmt.executeUpdate();

            JdbcUtil.getInstance().commit();
        } catch (SQLException e) {
            try {
                JdbcUtil.getInstance().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(DeployDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            throw e;
        }
    }

}
