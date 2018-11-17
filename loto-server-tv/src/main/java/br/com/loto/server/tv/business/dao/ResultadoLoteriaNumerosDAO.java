/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.server.tv.business.dao;

import br.com.loto.core.dao.BaseDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class ResultadoLoteriaNumerosDAO extends BaseDAO<Integer> {

    private static ResultadoLoteriaNumerosDAO instance;

    private ResultadoLoteriaNumerosDAO() {
    }

    public static ResultadoLoteriaNumerosDAO getInstance() {
        if (instance == null) {
            instance = new ResultadoLoteriaNumerosDAO();
        }

        return instance;
    }

    public List<Integer> pesquisarNumeros(Long resultadoLoteria) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" select numero  ");
        sql.append("   from resultado_loteria_numeros ");
        sql.append("  where 1 = 1 ");
        sql.append("    and id_resultado_loteria = ? ");
        sql.append("  order by numero ");

        List<Object> parameters = new ArrayList<>();
        parameters.add(resultadoLoteria);

        return super.pesquisar(sql.toString(), parameters, (ResultSet rs) -> {
           
            return rs.getInt("numero");

        });
    }

}
