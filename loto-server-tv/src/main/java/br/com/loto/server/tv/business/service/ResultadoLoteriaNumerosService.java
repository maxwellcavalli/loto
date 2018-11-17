/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.server.tv.business.service;

import br.com.loto.server.tv.business.dao.ResultadoLoteriaNumerosDAO;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class ResultadoLoteriaNumerosService {

    private static ResultadoLoteriaNumerosService instance;

    public static ResultadoLoteriaNumerosService getInstance() {
        if (instance == null) {
            instance = new ResultadoLoteriaNumerosService();
        }

        return instance;
    }

    private ResultadoLoteriaNumerosService() {
    }

    public List<Integer> pesquisarNumeros(Long resultadoLoteria) throws SQLException {
        return ResultadoLoteriaNumerosDAO.getInstance().pesquisarNumeros(resultadoLoteria);
    }

}
