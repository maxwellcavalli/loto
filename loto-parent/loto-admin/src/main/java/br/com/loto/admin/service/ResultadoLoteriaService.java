/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.service;

import br.com.loto.admin.dao.ResultadoLoteriaDAO;
import br.com.loto.admin.domain.ResultadoLoteria;
import br.com.loto.admin.domain.ResultadoLoteriaNumeros;
import br.com.loto.core.util.JdbcUtil;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class ResultadoLoteriaService {

    private static ResultadoLoteriaService instance;

    public static ResultadoLoteriaService getInstance() {
        if (instance == null) {
            instance = new ResultadoLoteriaService();
        }

        return instance;
    }

    private ResultadoLoteriaService() {
    }

    public ResultadoLoteria persistir(ResultadoLoteria t, List<ResultadoLoteriaNumeros> numerosLoteria) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        t = ResultadoLoteriaDAO.getInstance().persistir(t);
        
        numerosLoteria = ResultadoLoteriaNumerosService.getInstance().persistir(t, numerosLoteria);
        t.setNumerosLoteria(numerosLoteria);

        JdbcUtil.getInstance().commit();
        return t;
    }

    public List<ResultadoLoteria> pesquisar(Integer tipoLoteria, Integer concurso) throws SQLException {
        return ResultadoLoteriaDAO.getInstance().pesquisar(tipoLoteria, concurso);
    }

}
