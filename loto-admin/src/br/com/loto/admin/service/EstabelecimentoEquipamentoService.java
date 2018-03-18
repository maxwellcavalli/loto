/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.service;

import br.com.loto.admin.dao.EstabelecimentoEquipamentoDAO;
import br.com.loto.admin.domain.Equipamento;
import br.com.loto.admin.domain.Estabelecimento;
import br.com.loto.admin.domain.EstabelecimentoEquipamento;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class EstabelecimentoEquipamentoService {

    private static EstabelecimentoEquipamentoService instance;

    public static EstabelecimentoEquipamentoService getInstance() {
        if (instance == null) {
            instance = new EstabelecimentoEquipamentoService();
        }

        return instance;
    }

    private EstabelecimentoEquipamentoService() {
    }

    public List<EstabelecimentoEquipamento> persistir(Estabelecimento estabelecimento, List<EstabelecimentoEquipamento> equipamentos) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        List<EstabelecimentoEquipamento> listDB = pesquisar(estabelecimento, false);

        for (EstabelecimentoEquipamento elDB : listDB) {
            boolean exists = equipamentos.stream().filter(el -> el.getId().longValue() == elDB.getId().longValue()).count() > 0;

            if (!exists) {
                EstabelecimentoEquipamentoDAO.getInstance().delete(elDB);
            }
        }

        for (EstabelecimentoEquipamento el : equipamentos) {
            el.setEstabelecimento(estabelecimento);

            el = EstabelecimentoEquipamentoDAO.getInstance().persistir(el);
        }

        return equipamentos;
    }

    public List<EstabelecimentoEquipamento> pesquisar(Estabelecimento estabelecimento, boolean rollbackAfterRun) throws SQLException {
        return EstabelecimentoEquipamentoDAO.getInstance().pesquisar(estabelecimento, rollbackAfterRun);
    }
    
    public List<EstabelecimentoEquipamento> pesquisar(Estabelecimento estabelecimento) throws SQLException {
        return EstabelecimentoEquipamentoDAO.getInstance().pesquisar(estabelecimento);
    }
    
    public boolean isEquipamentoVinculadoOutroEstabelecimento(Estabelecimento estabelecimento, Equipamento equipamento) throws SQLException {
        return EstabelecimentoEquipamentoDAO.getInstance().isEquipamentoVinculadoOutroEstabelecimento(estabelecimento, equipamento);
    }

}
