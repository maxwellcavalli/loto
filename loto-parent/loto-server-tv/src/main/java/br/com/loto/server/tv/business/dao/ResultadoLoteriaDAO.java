/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.server.tv.business.dao;

import br.com.loto.core.dao.BaseDAO;
import br.com.loto.shared.ResultadoLoteriaDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class ResultadoLoteriaDAO extends BaseDAO<ResultadoLoteriaDTO> {

    private static ResultadoLoteriaDAO instance;

    private ResultadoLoteriaDAO() {
    }

    public static ResultadoLoteriaDAO getInstance() {
        if (instance == null) {
            instance = new ResultadoLoteriaDAO();
        }

        return instance;
    }

    public List<ResultadoLoteriaDTO> loadLastResults() throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" select rl.id, rl.concurso, rl.valor_acumulado, rl.id_tipo_loteria  ");
        sql.append("   from resultado_loteria rl ");
        sql.append("  where 1 = 1 ");
        sql.append("    and rl.concurso = (select max(rl1.concurso) ");
        sql.append("                         from resultado_loteria rl1 ");
        sql.append("                        where rl1.id_tipo_loteria = rl.id_tipo_loteria)  ");
        sql.append("  order by id_tipo_loteria ");

        List<Object> parameters = new ArrayList<>();

        return super.pesquisar(sql.toString(), parameters, (ResultSet rs) -> {
            ResultadoLoteriaDTO r = new ResultadoLoteriaDTO();
            r.setId(rs.getLong("id"));
            r.setConcurso(rs.getInt("concurso"));
            r.setValorAcumulado(rs.getBigDecimal("valor_acumulado"));
            r.setIdTipoLoteria(rs.getInt("id_tipo_loteria"));

            return r;
        });
    }

}
