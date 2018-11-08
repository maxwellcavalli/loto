/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.controller.sistema.estabelecimento;

import br.com.loto.admin.FxmlFiles;
import br.com.loto.admin.LotoAdmin;
import br.com.loto.admin.controller.sistema.cidade.CidadeFormController;
import br.com.loto.admin.domain.Cidade;
import br.com.loto.admin.domain.Cliente;
import br.com.loto.admin.domain.Equipamento;
import br.com.loto.admin.util.FxmlUtil;
import br.com.loto.admin.domain.Estabelecimento;
import br.com.loto.admin.domain.EstabelecimentoCliente;
import br.com.loto.admin.domain.EstabelecimentoEndereco;
import br.com.loto.admin.domain.EstabelecimentoEquipamento;
import br.com.loto.admin.domain.Estado;
import br.com.loto.admin.service.CidadeService;
import br.com.loto.admin.service.EquipamentoService;
import br.com.loto.admin.service.EstabelecimentoClienteService;
import br.com.loto.admin.service.EstabelecimentoEnderecoService;
import br.com.loto.admin.service.EstabelecimentoEquipamentoService;
import br.com.loto.admin.service.EstabelecimentoService;
import br.com.loto.admin.service.EstadoService;
import br.com.loto.components.autocomplete.AutocompletionlTextField;

import br.com.loto.core.fx.datatable.interfaces.IActionColumn;
import br.com.loto.core.fx.datatable.util.TableColumnUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author maxwe
 */
public class EstabelecimentoFormController implements Initializable {

    @FXML
    public TextField txtDescricao;

    @FXML
    public CheckBox ckAtivo;

    //endereco
    @FXML
    public TextField txtLogradouro;

    @FXML
    public TextField txtNumero;

    @FXML
    public ComboBox<Estado> cbEstado;

    @FXML
    public ComboBox<Cidade> cbCidade;

    //equipamento
    @FXML
    public ComboBox<Equipamento> cbEquipamento;

    @FXML
    public TableView<EstabelecimentoEquipamento> tableEquipamento;

    //cliente
    //@FXML
    //public ComboBox<Cliente> cbCliente;
    @FXML
    private AutocompletionlTextField txtCliente;

    @FXML
    public TableView<EstabelecimentoCliente> tableCliente;

    //variaveis globais
    private Estabelecimento estabelecimento;

    private EstabelecimentoEndereco estabelecimentoEndereco;

    private List<EstabelecimentoEquipamento> equipamentos;

    private List<EstabelecimentoCliente> clientes;

    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private Long cliCidade = null;
    private Long cliEstado = null;

    //--
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        cbCliente.setConverter(new StringConverter<Cliente>() {
//            @Override
//            public String toString(Cliente object) {
//                return object.getNome();
//            }
//
//            @Override
//            public Cliente fromString(String string) {
//                return null;
//            }
//        });
//        
//        
//        AutoCompleteCliente.bindAutoCompleteToComboBox(cbCliente, cliCidade, cliEstado);
    }

    public void salvar(ActionEvent event) {

        List<String> messages = new ArrayList<>();
        if (this.estabelecimento == null) {
            this.estabelecimento = new Estabelecimento();
        }

        this.estabelecimento.setDescricao(txtDescricao.getText());
        this.estabelecimento.setAtivo(ckAtivo.selectedProperty().getValue());

        this.estabelecimentoEndereco.setLogradouro(this.txtLogradouro.getText());
        this.estabelecimentoEndereco.setNumero(this.txtNumero.getText());

        Cidade cidade = cbCidade.getSelectionModel().getSelectedItem();
        this.estabelecimentoEndereco.setCidade(cidade);

        if ("".equals(this.estabelecimento.getDescricao().trim())) {
            messages.add("Descrição Inválida");
        }

        if (this.estabelecimentoEndereco.getCidade() == null || this.estabelecimentoEndereco.getCidade().getId() == null) {
            messages.add("Estado/Cidade Inválidos");
        }

        if (messages.isEmpty()) {
            try {
                this.estabelecimento = EstabelecimentoService.getInstance().persistir(this.estabelecimento, this.estabelecimentoEndereco, this.equipamentos);
                this.estabelecimentoEndereco = this.estabelecimento.getEstabelecimentoEndereco();
                this.equipamentos = this.estabelecimento.getEquipamentos();

            } catch (IllegalArgumentException | IllegalAccessException | SQLException ex) {
                Logger.getLogger(EstabelecimentoFormController.class.getName()).log(Level.SEVERE, null, ex);

                FxmlUtil.getInstance().openMessageDialog(event, ex);
            } catch (Exception ex) {
                Logger.getLogger(EstabelecimentoFormController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            FxmlUtil.getInstance().openMessageDialog(event, messages);
        }
    }

    public void voltar(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.ESTABELECIMENTO_LIST));
        try {
            AnchorPane p = (AnchorPane) fxmlLoader.load();

            LotoAdmin.centerContainer.getChildren().clear();
            LotoAdmin.centerContainer.getChildren().add(p);
        } catch (IOException ex) {
            Logger.getLogger(EstabelecimentoFormController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(event, ex);
        }
    }

    public void initData(Estabelecimento estabelecimento) {
        txtDescricao.setText(estabelecimento.getDescricao());
        ckAtivo.setSelected(estabelecimento.isAtivo());

        this.estabelecimento = estabelecimento;

        try {
            this.estabelecimentoEndereco = EstabelecimentoEnderecoService.getInstance().carregar(estabelecimento);
            if (this.estabelecimentoEndereco == null) {
                this.estabelecimentoEndereco = new EstabelecimentoEndereco();
            } else {
                this.txtLogradouro.setText(this.estabelecimentoEndereco.getLogradouro());
                this.txtNumero.setText(this.estabelecimentoEndereco.getNumero());
            }

            popularTabelaClientes();
            popularTabelaEquipamentos();
            popularComboEquipamentos();

            this.popularEstados();
            cbEstado.getSelectionModel().clearSelection();
            cbCidade.getSelectionModel().clearSelection();

            if (estabelecimentoEndereco.getCidade() != null) {
                cbEstado.getSelectionModel().select(estabelecimentoEndereco.getCidade().getEstado());

                popularCidade();

                cbCidade.getSelectionModel().select(estabelecimentoEndereco.getCidade());
            }

        } catch (SQLException ex) {
            Logger.getLogger(EstabelecimentoFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void popularComboEquipamentos() throws SQLException {
        List<Equipamento> listEquipamento = EquipamentoService.getInstance().pesquisar("", Boolean.TRUE);
        listEquipamento.stream().forEach(el -> cbEquipamento.getItems().add(el));

        cbEquipamento.setConverter(new StringConverter<Equipamento>() {
            @Override
            public String toString(Equipamento object) {
                return "[" + object.getSerial() + "] " + object.getDescricao();
            }

            @Override
            public Equipamento fromString(String string) {
                return null;
            }
        });
    }

    private void popularTabelaEquipamentos() throws SQLException {
        cbEquipamento.getItems().clear();
        this.equipamentos = EstabelecimentoEquipamentoService.getInstance().pesquisar(estabelecimento);
        if (this.equipamentos == null) {
            this.equipamentos = new ArrayList<>(1);
        }

        processaDatatableEquipamentos();
    }

    private void popularTabelaClientes() throws SQLException {
        this.clientes = EstabelecimentoClienteService.getInstance().pesquisar(estabelecimento);
        if (this.clientes == null) {
            this.clientes = new ArrayList<>(1);
        }

        processaDatatableClientes();
    }

    private void popularEstados() {
        List<Estado> listEstado;
        try {
            cbEstado.getItems().add(new Estado());

            listEstado = EstadoService.getInstance().pesquisar("");
            listEstado.stream().forEach(el -> cbEstado.getItems().add(el));

            cbEstado.setConverter(new StringConverter<Estado>() {
                @Override
                public String toString(Estado object) {
                    return object.getSigla();
                }

                @Override
                public Estado fromString(String string) {
                    return null;
                }
            });

        } catch (SQLException ex) {
            Logger.getLogger(CidadeFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void popularCidade() {
        cbCidade.getItems().clear();

        Estado e = cbEstado.getSelectionModel().getSelectedItem();

        if (e != null && e.getId() != null) {
            List<Cidade> listCidade;
            try {
                cbCidade.getItems().add(new Cidade());

                listCidade = CidadeService.getInstance().pesquisar("", e.getId());
                listCidade.stream().forEach(el -> cbCidade.getItems().add(el));

                cbCidade.setConverter(new StringConverter<Cidade>() {
                    @Override
                    public String toString(Cidade object) {
                        return object.getNome();
                    }

                    @Override
                    public Cidade fromString(String string) {
                        return null;
                    }
                });

            } catch (SQLException ex) {
                Logger.getLogger(CidadeFormController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void changeEstado(ActionEvent event) {
        cbCidade.getSelectionModel().clearSelection();
        popularCidade();
    }

    private void processaDatatableEquipamentos() {

        TableColumn<EstabelecimentoEquipamento, String> serialColumn = TableColumnUtil.createStringColumn("Serial", 100, (EstabelecimentoEquipamento s) -> s.getEquipamento().getSerial());
        TableColumn<EstabelecimentoEquipamento, String> descricaoColumn = TableColumnUtil.createStringColumn("Descrição", 750, (EstabelecimentoEquipamento s) -> s.getEquipamento().getDescricao());
        TableColumn<EstabelecimentoEquipamento, Boolean> actionColumn = TableColumnUtil.createButtonColumn("Ação", 80, tableEquipamento,
                (IActionColumn<EstabelecimentoEquipamento>) (EstabelecimentoEquipamento t) -> {
                    equipamentos.remove(t);
                    processaDatatableEquipamentos();
                });

        tableEquipamento.getColumns().clear();
        tableEquipamento.getColumns().setAll(serialColumn, descricaoColumn, actionColumn);
        tableEquipamento.setItems(FXCollections.observableArrayList(this.equipamentos));
    }

    private void processaDatatableClientes() {

        TableColumn<EstabelecimentoCliente, String> nomeColumn = TableColumnUtil.createStringColumn("Nome", 600, (EstabelecimentoCliente s) -> s.getCliente().getNome());
        TableColumn<EstabelecimentoCliente, String> ativoColumn = TableColumnUtil.createStringColumn("Ativo", 80, (EstabelecimentoCliente s) -> s.getAtivoStr());
        TableColumn<EstabelecimentoCliente, String> dataInativacaoColumn = TableColumnUtil.createStringColumn("Data Inativação", 150, (EstabelecimentoCliente s)
                -> s.getDataInativacao() == null ? "" : sdf.format(s.getDataInativacao()));

        TableColumn<EstabelecimentoCliente, Boolean> actionColumn = TableColumnUtil.createButtonColumn("Ação", 80, tableCliente,
                (IActionColumn<EstabelecimentoCliente>) (EstabelecimentoCliente t) -> {
                    t.setAtivo(false);
                    t.setDataInativacao(new Date());

                    processaDatatableClientes();
                });

        tableCliente.getColumns().clear();
        tableCliente.getColumns().setAll(nomeColumn, ativoColumn, dataInativacaoColumn, actionColumn);
        tableCliente.setItems(FXCollections.observableArrayList(this.clientes));
    }

    public void adicionarEquipamento(ActionEvent event) {
        try {

            Equipamento equipamento = cbEquipamento.getSelectionModel().getSelectedItem();

            EstabelecimentoEquipamento estabelecimentoEquipamento = new EstabelecimentoEquipamento();
            estabelecimentoEquipamento.setEquipamento(equipamento);

            long qtd = this.equipamentos.stream().filter(el -> el.getEquipamento().getId().longValue() == equipamento.getId().longValue()).count();
            if (qtd == 0) {
                boolean vinculado = EstabelecimentoEquipamentoService.getInstance().isEquipamentoVinculadoOutroEstabelecimento(estabelecimento, equipamento);
                if (!vinculado) {
                    this.equipamentos.add(estabelecimentoEquipamento);
                    processaDatatableEquipamentos();
                } else {
                    FxmlUtil.getInstance().openMessageDialog(event, "Equipamento Vinculado a outro Estabelecimento");
                }
            } else {
                FxmlUtil.getInstance().openMessageDialog(event, "Equipamento já Vinculado a este Estabelecimento");
            }
        } catch (Exception ex) {
            Logger.getLogger(EstabelecimentoFormController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(event, ex);
        }
    }

    public void adicionarCliente(ActionEvent event) {
        try {

//            Cliente cliente = cbCliente.getSelectionModel().getSelectedItem();
            Cliente cliente = null;

            EstabelecimentoCliente estabelecimentoCliente = new EstabelecimentoCliente();
            estabelecimentoCliente.setCliente(cliente);

            long qtd = this.clientes.stream().filter(el -> el.getCliente().getId().longValue() == cliente.getId().longValue()).count();
            if (qtd == 0) {

                this.clientes.add(estabelecimentoCliente);
                processaDatatableClientes();

            } else {
                FxmlUtil.getInstance().openMessageDialog(event, "Cliente já Vinculado a este Estabelecimento");
            }
        } catch (Exception ex) {
            Logger.getLogger(EstabelecimentoFormController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(event, ex);
        }
    }

}
