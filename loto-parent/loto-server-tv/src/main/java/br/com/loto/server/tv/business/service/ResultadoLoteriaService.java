/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.server.tv.business.service;

import br.com.loto.server.tv.business.dao.ResultadoLoteriaDAO;
import br.com.loto.shared.ResultadoLoteriaDTO;
import br.com.loto.shared.ResultadoLoteriaTransferDTO;
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

    public ResultadoLoteriaTransferDTO loadLastResults() throws SQLException {
        List<ResultadoLoteriaDTO> resultados = ResultadoLoteriaDAO.getInstance().loadLastResults();

        for (ResultadoLoteriaDTO r : resultados) {
            r.setNumeros(ResultadoLoteriaNumerosService.getInstance().pesquisarNumeros(r.getId()));
        };

        if (resultados != null && !resultados.isEmpty()) {
            ResultadoLoteriaTransferDTO r = new ResultadoLoteriaTransferDTO();
            r.setResultados(resultados);

            return r;
        } else {
            return null;
        }
    }

}
