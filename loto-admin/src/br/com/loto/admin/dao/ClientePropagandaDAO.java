/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.dao;

import br.com.loto.admin.domain.Cliente;
import br.com.loto.admin.domain.ClientePropaganda;
import br.com.loto.admin.domain.Equipamento;
import br.com.loto.core.dao.BaseDAO;
import br.com.loto.admin.domain.Estabelecimento;
import br.com.loto.admin.domain.EstabelecimentoEquipamento;
import br.com.loto.admin.domain.Propaganda;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class ClientePropagandaDAO extends BaseDAO<ClientePropaganda> {

    private static ClientePropagandaDAO instance;

    private ClientePropagandaDAO() {
    }

    public static ClientePropagandaDAO getInstance() {
        if (instance == null) {
            instance = new ClientePropagandaDAO();
        }

        return instance;
    }

    @Override
    public ClientePropaganda persistir(ClientePropaganda t) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        return super.persistir(t); //To change body of generated methods, choose Tools | Templates.
    }

    public List<ClientePropaganda> pesquisar(Cliente cliente) throws SQLException {
        return pesquisar(cliente, true);
    }

    public List<ClientePropaganda> pesquisar(Cliente cliente, boolean rollbackAfterRun) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" select _cliprop.id as _cliprop_id, ");
        sql.append("        _cliprop.id_cliente as _cliprop_id_cliente, ");

        sql.append("        _prop.id as _prop_id, ");
        sql.append("        _prop.descricao as _prop_descricao, ");
        sql.append("        _prop.ativo as _prop_ativo, ");
        sql.append("        _prop.nome_arquivo as _prop_nome_arquivo, ");
        sql.append("        _prop.conteudo as _prop_conteudo, ");
        sql.append("        _prop.data as _prop_data ");

        sql.append("   from cliente_propaganda _cliprop ");
        sql.append("  inner join propaganda _prop on _prop.id = _cliprop.id_propaganda ");
        sql.append("  where _cliprop.id_cliente = ? ");

        List<Object> parameters = new ArrayList<>();
        parameters.add(cliente.getId());

        return super.pesquisar(sql.toString(), parameters, (ResultSet rs) -> {
            Cliente c = new Cliente();
            c.setId(rs.getLong("_cliprop_id_cliente"));

            Propaganda p = new Propaganda();
            p.setId(rs.getLong("_prop_id"));
            p.setDescricao(rs.getString("_prop_descricao"));
            p.setAtivo(rs.getBoolean("_prop_ativo"));
            p.setConteudo(rs.getBytes("_prop_conteudo"));
            p.setNomeArquivo(rs.getString("_prop_nome_arquivo"));
            p.setData(rs.getDate("_prop_data"));

            ClientePropaganda clientePropaganda = new ClientePropaganda();
            clientePropaganda.setId(rs.getLong("_cliprop_id"));
            clientePropaganda.setCliente(c);
            clientePropaganda.setPropaganda(p);

            return clientePropaganda;
        }, rollbackAfterRun);
    }

    @Override
    public void delete(ClientePropaganda t) throws SQLException, IllegalAccessException, Exception {
        super.delete(t); //To change body of generated methods, choose Tools | Templates.
    }

}