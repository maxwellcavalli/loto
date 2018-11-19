/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.service;

import br.com.loto.admin.dao.ResultadoLoteriaNumerosDAO;
import br.com.loto.admin.domain.ResultadoLoteria;
import br.com.loto.admin.domain.ResultadoLoteriaNumeros;
import java.sql.SQLException;
import java.util.ArrayList;
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

    public List<ResultadoLoteriaNumeros> persistir(ResultadoLoteria resultadoLoteria, List<ResultadoLoteriaNumeros> numerosLoteria) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        List<ResultadoLoteriaNumeros> listDB = pesquisar(resultadoLoteria, false);

        for (ResultadoLoteriaNumeros resultadoLoteriaNumerosDB : listDB) {
            boolean exists = false;

            for (ResultadoLoteriaNumeros resultadoLoteriaNumeros : numerosLoteria) {
                if (resultadoLoteriaNumeros.getId() == null || resultadoLoteriaNumeros.getId().longValue() == 0) {
                    continue;
                }

                if (resultadoLoteriaNumeros.getId().longValue() == resultadoLoteriaNumerosDB.getId().longValue()) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                delete(resultadoLoteriaNumerosDB);
            }
        }

        List<ResultadoLoteriaNumeros> ret = new ArrayList<>();

        for (ResultadoLoteriaNumeros resultadoLoteriaNumeros : numerosLoteria) {

            resultadoLoteriaNumeros.setResultadoLoteria(resultadoLoteria);
            ResultadoLoteriaNumerosDAO.getInstance().persistir(resultadoLoteriaNumeros);

            ret.add(resultadoLoteriaNumeros);
        }

        return ret;
    }

    public void delete(ResultadoLoteriaNumeros resultadoLoteriaNumeros) throws IllegalAccessException, Exception {
        ResultadoLoteriaNumerosDAO.getInstance().delete(resultadoLoteriaNumeros);

    }

    public List<ResultadoLoteriaNumeros> pesquisar(ResultadoLoteria resultadoLoteria, boolean rollbackAfterRun) throws SQLException {
        return ResultadoLoteriaNumerosDAO.getInstance().pesquisar(resultadoLoteria, rollbackAfterRun);
    }

    public List<ResultadoLoteriaNumeros> pesquisar(ResultadoLoteria resultadoLoteria) throws SQLException {
        return ResultadoLoteriaNumerosDAO.getInstance().pesquisar(resultadoLoteria);
    }
}
