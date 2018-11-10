/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.service;

import br.com.loto.admin.dao.EquipamentoDAO;
import br.com.loto.core.util.JdbcUtil;
import br.com.loto.admin.domain.Equipamento;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author maxwe
 */
public class EquipamentoService {

    private static EquipamentoService instance;

    public static EquipamentoService getInstance() {
        if (instance == null) {
            instance = new EquipamentoService();
        }

        return instance;
    }

    private EquipamentoService() {
    }

    public Equipamento persistir(Equipamento equipamento) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        if (equipamento.getUuid() == null || "".equals(equipamento.getUuid())) {
            equipamento.setUuid(UUID.randomUUID().toString());
        }

        equipamento = EquipamentoDAO.getInstance().persistir(equipamento);

        JdbcUtil.getInstance().commit();
        return equipamento;
    }

    public List<Equipamento> pesquisar(String numSerie) throws SQLException {
        return EquipamentoDAO.getInstance().pesquisar(numSerie);
    }

    public List<Equipamento> pesquisar(String numSerie, Boolean ativo) throws SQLException {
        return EquipamentoDAO.getInstance().pesquisar(numSerie, ativo);
    }

}
