/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.controller.sistema.cliente;

import br.com.loto.admin.FxmlFiles;
import br.com.loto.admin.LotoAdmin;
import br.com.loto.admin.controller.sistema.cidade.CidadeFormController;
import br.com.loto.admin.domain.Cidade;
import br.com.loto.admin.domain.Cliente;
import br.com.loto.admin.domain.Estado;
import br.com.loto.admin.service.CidadeService;
import br.com.loto.admin.util.FxmlUtil;
import br.com.loto.admin.service.ClienteService;
import br.com.loto.admin.service.EstadoService;
import br.com.loto.core.fx.datatable.util.TableColumnUtil;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author maxwe
 */
public class ClienteListController implements Initializable {

    @FXML
    public JFXTextField txtFiltro;

    @FXML
    public TableView<Cliente> datatable;

    @FXML
    public JFXComboBox<Estado> cbEstado;

    @FXML
    public JFXComboBox<Cidade> cbCidade;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        popularEstados();
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

    public void pesquisar(ActionEvent acEvent) {
        try {
            String descricao = txtFiltro.getText();
            Long cidade = null;
            Long estado = null;

            List<Cliente> list = ClienteService.getInstance().pesquisar(descricao, cidade, estado);

            TableColumn<Cliente, String> descricaoColumn = TableColumnUtil.createStringColumn("Nome", 550, (Cliente s) -> s.getNome());
            TableColumn<Cliente, String> cidadeColumn = TableColumnUtil.createStringColumn("Cidade", 250, (Cliente s)
                    -> s.getCidade() == null ? "" : s.getCidade().getNome());

            TableColumn<Cliente, String> estadoColumn = TableColumnUtil.createStringColumn("UF", 80, (Cliente s)
                    -> s.getCidade() == null ? "" : s.getCidade().getEstado().getSigla());

            TableColumn<Cliente, String> ativoColumn = TableColumnUtil.createStringColumn("Ativo", 50, (Cliente s) -> s.getAtivoStr());

            datatable.getColumns().clear();
            datatable.getColumns().setAll(descricaoColumn, cidadeColumn, estadoColumn, ativoColumn);
            datatable.setItems(FXCollections.observableArrayList(list));

            try {
                datatable.setOnMouseClicked((MouseEvent event) -> {
                    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.CLIENTE_FORM));

                        AnchorPane p;
                        try {
                            Cliente cliente = (Cliente) datatable.getSelectionModel().getSelectedItem();

                            p = (AnchorPane) fxmlLoader.load();

                            ClienteFormController controller = fxmlLoader.<ClienteFormController>getController();

                            controller.initData(cliente);

                            LotoAdmin.centerContainer.getChildren().clear();
                            LotoAdmin.centerContainer.getChildren().add(p);
                        } catch (IOException ex) {
                            Logger.getLogger(ClienteListController.class.getName()).log(Level.SEVERE, null, ex);

                            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
                        }
                    }
                });

            } catch (Exception e) {
                throw e;
            }

        } catch (Exception ex) {
            Logger.getLogger(ClienteListController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
        }
    }

    public void adicionar(ActionEvent acEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.CLIENTE_FORM));
        try {
            AnchorPane p = (AnchorPane) fxmlLoader.load();

            ClienteFormController controller = fxmlLoader.<ClienteFormController>getController();

            controller.initData(new Cliente());

            LotoAdmin.centerContainer.getChildren().clear();
            LotoAdmin.centerContainer.getChildren().add(p);

        } catch (IOException ex) {
            Logger.getLogger(ClienteListController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
        }
    }

}
