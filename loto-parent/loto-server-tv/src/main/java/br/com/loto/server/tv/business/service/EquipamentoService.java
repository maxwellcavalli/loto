/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.server.tv.business.service;

import br.com.loto.server.tv.business.dao.EquipamentoDAO;
import java.sql.SQLException;

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

    public boolean hasEquipamento(String uuid) throws SQLException {
        return EquipamentoDAO.getInstance().hasEquipamento(uuid);
    }

}
