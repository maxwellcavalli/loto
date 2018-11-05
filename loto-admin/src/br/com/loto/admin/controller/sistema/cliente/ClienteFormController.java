/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.controller.sistema.cliente;

import br.com.loto.admin.FxmlFiles;
import br.com.loto.admin.LotoAdmin;
import br.com.loto.admin.domain.Cliente;
import br.com.loto.admin.domain.ClientePropaganda;
import br.com.loto.admin.util.FxmlUtil;
import br.com.loto.admin.domain.Propaganda;
import br.com.loto.admin.service.ClientePropagandaService;
import br.com.loto.admin.service.ClienteService;
import br.com.loto.core.fx.datatable.interfaces.IActionColumn;
import br.com.loto.core.fx.datatable.util.TableColumnUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author maxwe
 */
public class ClienteFormController implements Initializable {

    @FXML
    public TextField txtDescricao;

    @FXML
    public CheckBox ckAtivo;

    //propaganda
    @FXML
    public TableView<ClientePropaganda> tablePropaganda;

    @FXML
    public TextField txtDescricaoPropaganda;

    @FXML
    public TextField txtNomeArquivo;

    @FXML
    public CheckBox cbPropaganda;

    //variaveis globais
    private Cliente cliente;

    private List<ClientePropaganda> propagandas;

    private File file;

    //--
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void salvar(ActionEvent event) {

        List<String> messages = new ArrayList<>();
        if (this.cliente == null) {
            this.cliente = new Cliente();
        }

        this.cliente.setNome(txtDescricao.getText());
        this.cliente.setAtivo(ckAtivo.selectedProperty().getValue());

        if ("".equals(this.cliente.getNome().trim())) {
            messages.add("Nome Inválido");
        }

        if (messages.isEmpty()) {
            try {
                this.cliente = ClienteService.getInstance().persistir(this.cliente, this.propagandas);

                this.propagandas = this.cliente.getListClientePropaganda();

            } catch (IllegalArgumentException | IllegalAccessException | SQLException ex) {
                Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);

                FxmlUtil.getInstance().openMessageDialog(event, ex);
            } catch (Exception ex) {
                Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            FxmlUtil.getInstance().openMessageDialog(event, messages);
        }
    }

    public void voltar(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.CLIENTE_LIST));
        try {
            AnchorPane p = (AnchorPane) fxmlLoader.load();

            LotoAdmin.centerContainer.getChildren().clear();
            LotoAdmin.centerContainer.getChildren().add(p);
        } catch (IOException ex) {
            Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(event, ex);
        }
    }

    public void initData(Cliente cliente) {
        txtDescricao.setText(cliente.getNome());
        ckAtivo.setSelected(cliente.isAtivo());

        cbPropaganda.setSelected(true);

        this.cliente = cliente;

        try {
            this.propagandas = ClientePropagandaService.getInstance().pesquisar(cliente);
            if (this.propagandas == null) {
                this.propagandas = new ArrayList<>(1);
            }

            processaDatatablePropagandas();

            tablePropaganda.setOnMouseClicked((MouseEvent event) -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    ClientePropaganda clientePropaganda = (ClientePropaganda) tablePropaganda.getSelectionModel().getSelectedItem();

                    System.out.println(clientePropaganda);

                }
            });

        } catch (SQLException ex) {
            Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processaDatatablePropagandas() {

        TableColumn<ClientePropaganda, String> descricaoColumn = TableColumnUtil.createStringColumn("Descrição", 750, (ClientePropaganda s) -> s.getPropaganda().getDescricao());
        TableColumn<ClientePropaganda, String> ativoColumn = TableColumnUtil.createStringColumn("Ativo", 50, (ClientePropaganda s) -> s.getPropaganda().getAtivoStr());
        TableColumn<ClientePropaganda, Boolean> actionColumn = TableColumnUtil.createButtonColumn("Ação", 80, tablePropaganda,
                (IActionColumn<ClientePropaganda>) (ClientePropaganda t) -> {
                    propagandas.remove(t);
                    processaDatatablePropagandas();
                });

        tablePropaganda.getColumns().clear();
        tablePropaganda.getColumns().setAll(descricaoColumn, actionColumn);
        tablePropaganda.setItems(FXCollections.observableArrayList(this.propagandas));
    }

    public void adicionarPropaganda(ActionEvent event) {
        try {

            Propaganda propaganda = new Propaganda();
            propaganda.setDescricao(txtDescricaoPropaganda.getText());
            propaganda.setAtivo(cbPropaganda.selectedProperty().getValue());
            propaganda.setData(new Date());

            if (file != null) {
                try {
                    Path fileLocation = Paths.get(file.getPath());
                    byte[] conteudo = Files.readAllBytes(fileLocation);

                    propaganda.setNomeArquivo(file.getName());
                    propaganda.setConteudo(conteudo);

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (propaganda.getConteudo() == null || propaganda.getConteudo().length == 0) {
                FxmlUtil.getInstance().openMessageDialog(event, "Arquivo Inválido");
            } else {

                ClientePropaganda clientePropaganda = new ClientePropaganda();
                clientePropaganda.setCliente(cliente);
                clientePropaganda.setPropaganda(propaganda);

                this.propagandas.add(clientePropaganda);
                processaDatatablePropagandas();

                txtDescricaoPropaganda.setText("");
                cbPropaganda.setSelected(true);
                txtNomeArquivo.setText("");
            }
        } catch (Exception ex) {
            Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(event, ex);
        }
    }

    public void selecionarArquivo(ActionEvent event) {
        Stage stage = new Stage(StageStyle.UTILITY);

        txtNomeArquivo.setText("");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        this.file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            txtNomeArquivo.setText(file.getName());
        }

    }

}
