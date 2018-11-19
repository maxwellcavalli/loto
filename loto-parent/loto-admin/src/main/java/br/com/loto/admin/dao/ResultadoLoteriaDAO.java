/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.dao;

import br.com.loto.admin.domain.ResultadoLoteria;
import br.com.loto.core.dao.BaseDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class ResultadoLoteriaDAO extends BaseDAO<ResultadoLoteria> {

    private static ResultadoLoteriaDAO instance;

    private ResultadoLoteriaDAO() {
    }

    public static ResultadoLoteriaDAO getInstance() {
        if (instance == null) {
            instance = new ResultadoLoteriaDAO();
        }

        return instance;
    }

    @Override
    public ResultadoLoteria persistir(ResultadoLoteria t) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        return super.persistir(t); //To change body of generated methods, choose Tools | Templates.
    }

    public List<ResultadoLoteria> pesquisar(Integer tipoLoteria, Integer concurso) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ID, CONCURSO, ID_TIPO_LOTERIA, VALOR_ACUMULADO ");
        sql.append("   FROM RESULTADO_LOTERIA ");
        sql.append("  WHERE 1 = 1 ");

        List<Object> parameters = new ArrayList<>();

        if (tipoLoteria != null) {
            sql.append("  AND ID_TIPO_LOTERIA = ?");
            parameters.add(tipoLoteria);
        }
        
        if (concurso != null){
            sql.append("  AND CONCURSO = ?");
            parameters.add(concurso);
        }
        sql.append(" ORDER BY ID_TIPO_LOTERIA, CONCURSO");

        return super.pesquisar(sql.toString(), parameters, (ResultSet rs) -> {
            ResultadoLoteria r = new ResultadoLoteria();
            r.setId(rs.getLong("ID"));
            r.setIdTipoLoteria(rs.getInt("ID_TIPO_LOTERIA"));
            r.setConcurso(rs.getInt("CONCURSO"));
            r.setValorAcumulado(rs.getBigDecimal("VALOR_ACUMULADO"));
            
            return r;
        });
    }
}
