/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.service;

import br.com.loto.admin.dao.EstadoDAO;
import br.com.loto.admin.domain.Estado;
import br.com.loto.core.util.JdbcUtil;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class EstadoService {

    private static EstadoService instance;

    public static EstadoService getInstance() {
        if (instance == null) {
            instance = new EstadoService();
        }

        return instance;
    }

    private EstadoService() {
    }

    public Estado persistir(Estado estado) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        estado = EstadoDAO.getInstance().persistir(estado);

        JdbcUtil.getInstance().commit();
        return estado;
    }

    public List<Estado> pesquisar(String nome) throws SQLException {
        return EstadoDAO.getInstance().pesquisar(nome);
    }

}
