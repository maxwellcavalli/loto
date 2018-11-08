/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.dao;

import br.com.loto.admin.domain.Cliente;
import br.com.loto.admin.domain.Equipamento;
import br.com.loto.core.dao.BaseDAO;
import br.com.loto.admin.domain.Estabelecimento;
import br.com.loto.admin.domain.EstabelecimentoCliente;
import br.com.loto.admin.domain.EstabelecimentoEquipamento;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author maxwe
 */
public class EstabelecimentoClienteDAO extends BaseDAO<EstabelecimentoCliente> {

    private static EstabelecimentoClienteDAO instance;

    private EstabelecimentoClienteDAO() {
    }

    public static EstabelecimentoClienteDAO getInstance() {
        if (instance == null) {
            instance = new EstabelecimentoClienteDAO();
        }

        return instance;
    }

    @Override
    public EstabelecimentoCliente persistir(EstabelecimentoCliente t) throws IllegalArgumentException, IllegalAccessException, SQLException, Exception {
        return super.persistir(t); //To change body of generated methods, choose Tools | Templates.
    }

    public List<EstabelecimentoCliente> pesquisar(Estabelecimento estabelecimento) throws SQLException {
        return pesquisar(estabelecimento, true);
    }

    public List<EstabelecimentoCliente> pesquisar(Estabelecimento estabelecimento, boolean rollbackAfterRun) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ");
        sql.append("    est_cliente.id as est_cliente_id, ");
        sql.append("    est_cliente.id_estabelecimento as est_cliente_id_estabelecimento, ");
        sql.append("    est_cliente.id_cliente as est_cliente_id_cliente, ");
        sql.append("    est_cliente.ativo as est_cliente_ativo, ");
        sql.append("    est_cliente.data_inativacao as est_cliente_data_inativacao, ");
        
        sql.append("    cli.id as cli_id , ");
        sql.append("    cli.nome as cli_nome, ");
        sql.append("    cli.ativo as cli_ativo ");
        sql.append("  FROM  estabelecimento_cliente est_cliente ");
        sql.append(" INNER JOIN cliente cli ON cli.id  = est_cliente.id_cliente ");
        sql.append(" WHERE est_cliente.id_estabelecimento = ? ");

        List<Object> parameters = new ArrayList<>();
        parameters.add(estabelecimento.getId());

        return super.pesquisar(sql.toString(), parameters, (ResultSet rs) -> {
            EstabelecimentoCliente e = new EstabelecimentoCliente();
            e.setId(rs.getLong("est_cliente_id"));
            e.setAtivo(rs.getBoolean("est_cliente_ativo"));
            e.setDataInativacao(rs.getTimestamp("est_cliente_data_inativacao"));

            Estabelecimento estabelecimento1 = new Estabelecimento();
            estabelecimento1.setId(rs.getLong("est_cliente_id_estabelecimento"));

            Cliente c = new Cliente();
            c.setId(rs.getLong("cli_id"));
            c.setNome(rs.getString("cli_nome"));
            c.setAtivo(rs.getBoolean("cli_ativo"));

            e.setEstabelecimento(estabelecimento1);
            e.setCliente(c);

            return e;
        }, rollbackAfterRun);
    }

    @Override
    public void delete(EstabelecimentoCliente t) throws SQLException, IllegalAccessException, Exception {
        t.setAtivo(false);
        t.setDataInativacao(new Date());
        super.persistir(t);
    }

}
