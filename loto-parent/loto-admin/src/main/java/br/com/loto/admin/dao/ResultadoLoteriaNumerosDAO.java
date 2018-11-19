/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.dao;

import br.com.loto.admin.domain.ResultadoLoteria;
import br.com.loto.core.dao.BaseDAO;
import br.com.loto.admin.domain.ResultadoLoteriaNumeros;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class ResultadoLoteriaNumerosDAO extends BaseDAO<ResultadoLoteriaNumeros> {

    private static ResultadoLoteriaNumerosDAO instance;

    private ResultadoLoteriaNumerosDAO() {
    }

    public static ResultadoLoteriaNumerosDAO getInstance() {
        if (instance == null) {
            instance = new ResultadoLoteriaNumerosDAO();
        }

        return instance;
    }

    @Override
    public ResultadoLoteriaNumeros persistir(ResultadoLoteriaNumeros t) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        return super.persistir(t); //To change body of generated methods, choose Tools | Templates.
    }

    public List<ResultadoLoteriaNumeros> pesquisar(ResultadoLoteria resultadoLoteria) throws SQLException {
        return pesquisar(resultadoLoteria, true);
    }

    public List<ResultadoLoteriaNumeros> pesquisar(ResultadoLoteria resultadoLoteria, boolean rollbackAfterRun) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");
        sql.append("    ID, ");
        sql.append("    ID_RESULTADO_LOTERIA, ");
        sql.append("    NUMERO ");

        sql.append("   from RESULTADO_LOTERIA_NUMEROS");
        sql.append("  where ID_RESULTADO_LOTERIA = ? ");

        List<Object> parameters = new ArrayList<>();
        parameters.add(resultadoLoteria.getId());

        sql.append("  order by ID ");
        
        return super.pesquisar(sql.toString(), parameters, (ResultSet rs) -> {
            ResultadoLoteriaNumeros rln = new ResultadoLoteriaNumeros();
            rln.setId(rs.getLong("ID"));
            rln.setResultadoLoteria(resultadoLoteria);
            rln.setNumero(rs.getInt("NUMERO"));
            

            return rln;
        }, rollbackAfterRun);
    }

    @Override
    public void delete(ResultadoLoteriaNumeros t) throws SQLException, IllegalAccessException, Exception {
        super.delete(t); //To change body of generated methods, choose Tools | Templates.
    }

}
