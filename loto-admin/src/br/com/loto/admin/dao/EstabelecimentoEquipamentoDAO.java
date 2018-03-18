/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.dao;

import br.com.loto.admin.domain.Equipamento;
import br.com.loto.core.dao.BaseDAO;
import br.com.loto.admin.domain.Estabelecimento;
import br.com.loto.admin.domain.EstabelecimentoEquipamento;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class EstabelecimentoEquipamentoDAO extends BaseDAO<EstabelecimentoEquipamento> {

    private static EstabelecimentoEquipamentoDAO instance;

    private EstabelecimentoEquipamentoDAO() {
    }

    public static EstabelecimentoEquipamentoDAO getInstance() {
        if (instance == null) {
            instance = new EstabelecimentoEquipamentoDAO();
        }

        return instance;
    }

    @Override
    public EstabelecimentoEquipamento persistir(EstabelecimentoEquipamento t) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        return super.persistir(t); //To change body of generated methods, choose Tools | Templates.
    }
    
     public List<EstabelecimentoEquipamento> pesquisar(Estabelecimento estabelecimento) throws SQLException {
         return pesquisar(estabelecimento, true);
     }

    public List<EstabelecimentoEquipamento> pesquisar(Estabelecimento estabelecimento, boolean rollbackAfterRun) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ");
        sql.append("    est_equipamento.id as est_equipamento_id, ");
        sql.append("    est_equipamento.id_estabelecimento as est_equipamento_id_estabelecimento, ");
        sql.append("    est_equipamento.id_equipamento as est_equipamento_id_equipamento, ");
        sql.append("    equip.id as _equip_id, ");
        sql.append("    equip.num_serie as _equip_num_serie, ");
        sql.append("    equip.descricao as _equip_descricao, ");
        sql.append("    equip.ativo as _equip_ativo ");
        sql.append("  FROM  estabelecimento_equipamento est_equipamento ");
        sql.append(" INNER JOIN equipamento equip ON equip.id  = est_equipamento.id_equipamento ");
        sql.append(" WHERE est_equipamento.id_estabelecimento = ? ");

        List<Object> parameters = new ArrayList<>();
        parameters.add(estabelecimento.getId());

        return super.pesquisar(sql.toString(), parameters, (ResultSet rs) -> {
            EstabelecimentoEquipamento e = new EstabelecimentoEquipamento();
            e.setId(rs.getLong("EST_EQUIPAMENTO_ID"));

            Estabelecimento estabelecimento1 = new Estabelecimento();
            estabelecimento1.setId(rs.getLong("EST_EQUIPAMENTO_ID_ESTABELECIMENTO"));

            Equipamento equipamento = new Equipamento();
            equipamento.setId(rs.getLong("_equip_id"));
            equipamento.setSerial(rs.getString("_equip_num_serie"));
            equipamento.setDescricao(rs.getString("_equip_descricao"));
            equipamento.setAtivo(rs.getBoolean("_equip_ativo"));

            e.setEstabelecimento(estabelecimento1);
            e.setEquipamento(equipamento);

            return e;
        }, rollbackAfterRun);
    }

    @Override
    public void delete(EstabelecimentoEquipamento t) throws SQLException, IllegalAccessException, Exception {
        super.delete(t); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isEquipamentoVinculadoOutroEstabelecimento(Estabelecimento estabelecimento, Equipamento equipamento) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT COUNT(1) as qtd ");
        sql.append("   FROM ESTABELECIMENTO_EQUIPAMENTO ");
        sql.append("  WHERE id_estabelecimento != ? ");
        sql.append("    AND id_equipamento = ? ");

        Long id_estabelecimento = estabelecimento.getId() == null ? 0l : estabelecimento.getId();

        List<Object> parameters = new ArrayList<>();
        parameters.add(id_estabelecimento);
        parameters.add(equipamento.getId());

        return recordCount(sql.toString(), parameters) > 0;
    }

}
