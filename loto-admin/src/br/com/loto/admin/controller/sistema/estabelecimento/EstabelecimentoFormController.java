/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.controller.sistema.estabelecimento;

import br.com.loto.admin.FxmlFiles;
import br.com.loto.admin.LotoAdmin;
import br.com.loto.admin.domain.Equipamento;
import br.com.loto.admin.util.FxmlUtil;
import br.com.loto.admin.domain.Estabelecimento;
import br.com.loto.admin.domain.EstabelecimentoEndereco;
import br.com.loto.admin.domain.EstabelecimentoEquipamento;
import br.com.loto.admin.service.EquipamentoService;
import br.com.loto.admin.service.EstabelecimentoEnderecoService;
import br.com.loto.admin.service.EstabelecimentoEquipamentoService;
import br.com.loto.admin.service.EstabelecimentoService;
import br.com.loto.core.fx.datatable.interfaces.IActionColumn;
import br.com.loto.core.fx.datatable.util.TableColumnUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
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
    public TextField txtCidade;

    @FXML
    public TextField txtEstado;

    //equipamento
    @FXML
    public ComboBox<Equipamento> cbEquipamento;

    @FXML
    public TableView<EstabelecimentoEquipamento> tableEquipamento;

    //variaveis globais
    private Estabelecimento estabelecimento;

    private EstabelecimentoEndereco estabelecimentoEndereco;

    private List<EstabelecimentoEquipamento> equipamentos;

    //--
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        this.estabelecimentoEndereco.setCidade(this.txtCidade.getText());
        this.estabelecimentoEndereco.setEstado(this.txtEstado.getText());

        if ("".equals(this.estabelecimento.getDescricao().trim())) {
            messages.add("Descrição Inválida");
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
                this.txtCidade.setText(this.estabelecimentoEndereco.getCidade());
                this.txtEstado.setText(this.estabelecimentoEndereco.getEstado());
            }

            cbEquipamento.getItems().clear();
            this.equipamentos = EstabelecimentoEquipamentoService.getInstance().pesquisar(estabelecimento);
            if (this.equipamentos == null) {
                this.equipamentos = new ArrayList<>(1);
            }

            processaDatatableEquipamentos();

            List<Equipamento> listEquipamento = EquipamentoService.getInstance().pesquisar("", Boolean.TRUE);
            listEquipamento.stream().forEach(el -> cbEquipamento.getItems().add(el));

        } catch (SQLException ex) {
            Logger.getLogger(EstabelecimentoFormController.class.getName()).log(Level.SEVERE, null, ex);
        }

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

}
