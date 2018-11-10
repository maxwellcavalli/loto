/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.service;

import br.com.loto.admin.dao.CidadeDAO;
import br.com.loto.admin.dao.EstadoDAO;
import br.com.loto.admin.domain.Cidade;
import br.com.loto.core.util.JdbcUtil;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class CidadeService {

    private static CidadeService instance;

    public static CidadeService getInstance() {
        if (instance == null) {
            instance = new CidadeService();
        }

        return instance;
    }

    private CidadeService() {
    }

    public Cidade persistir(Cidade cidade) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        cidade = CidadeDAO.getInstance().persistir(cidade);

        JdbcUtil.getInstance().commit();
        return cidade;
    }

    public List<Cidade> pesquisar(String nome, Long estado) throws SQLException {
        return pesquisar(nome, estado, Integer.MAX_VALUE);
    }

    public List<Cidade> pesquisar(String nome, Long estado, Integer maxValues) throws SQLException {
        return CidadeDAO.getInstance().pesquisar(nome, estado, maxValues);
    }

}
