/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.service;

import br.com.loto.admin.dao.PropagandaDAO;
import br.com.loto.admin.domain.Propaganda;
import java.sql.SQLException;
import java.util.UUID;

/**
 *
 * @author maxwe
 */
public class PropagandaService {

    private static PropagandaService instance;

    public static PropagandaService getInstance() {
        if (instance == null) {
            instance = new PropagandaService();
        }

        return instance;
    }

    private PropagandaService() {
    }

    public Propaganda persistir(Propaganda propaganda) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        if (propaganda.getUuid() == null || propaganda.getUuid().isEmpty()){
            propaganda.setUuid(UUID.randomUUID().toString());
        }
        
        return PropagandaDAO.getInstance().persistir(propaganda);
    }

    public void delete(Propaganda propaganda) throws IllegalAccessException, Exception {
        PropagandaDAO.getInstance().delete(propaganda);
    }

}
