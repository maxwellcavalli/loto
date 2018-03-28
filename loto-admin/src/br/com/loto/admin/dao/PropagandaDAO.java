/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.dao;

import br.com.loto.admin.domain.Propaganda;
import br.com.loto.core.dao.BaseDAO;
import java.sql.SQLException;

/**
 *
 * @author maxwe
 */
public class PropagandaDAO extends BaseDAO<Propaganda> {

    private static PropagandaDAO instance;

    private PropagandaDAO() {
    }

    public static PropagandaDAO getInstance() {
        if (instance == null) {
            instance = new PropagandaDAO();
        }

        return instance;
    }

    @Override
    public Propaganda persistir(Propaganda t) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        return super.persistir(t); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Propaganda t) throws SQLException, IllegalAccessException, Exception {
        super.delete(t); //To change body of generated methods, choose Tools | Templates.
    }
    
}
