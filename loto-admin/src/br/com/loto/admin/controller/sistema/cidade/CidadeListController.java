/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.controller.sistema.cidade;

import br.com.loto.admin.FxmlFiles;
import br.com.loto.admin.LotoAdmin;
import br.com.loto.admin.domain.Cidade;
import br.com.loto.admin.domain.Estado;
import br.com.loto.admin.service.CidadeService;
import br.com.loto.admin.service.EstadoService;
import br.com.loto.admin.util.FxmlUtil;
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
public class CidadeListController implements Initializable {

    @FXML
    public JFXTextField txtFiltro;

    @FXML
    public JFXComboBox<Estado> cbEstado;

    @FXML
    public TableView<Cidade> datatable;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        loadEstados();
    }

    private void loadEstados() {
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

    public void pesquisar(ActionEvent acEvent) {
        try {
            String descricao = txtFiltro.getText();
            Estado estadoO = cbEstado.getSelectionModel().getSelectedItem();
            Long estado = estadoO == null || estadoO.getId() == null ? null : estadoO.getId();

            List<Cidade> list = CidadeService.getInstance().pesquisar(descricao, estado);

            TableColumn<Cidade, String> estadoColumn = TableColumnUtil.createStringColumn("Estado", 100, (Cidade s) -> s.getEstado().getSigla());
            TableColumn<Cidade, String> descricaoColumn = TableColumnUtil.createStringColumn("Cidade", 750, (Cidade s) -> s.getNome());
            TableColumn<Cidade, String> ativoColumn = TableColumnUtil.createStringColumn("Ativo", 50, (Cidade s) -> s.getAtivoStr());

            datatable.getColumns().clear();
            datatable.getColumns().setAll(estadoColumn, descricaoColumn, ativoColumn);
            datatable.setItems(FXCollections.observableArrayList(list));

            try {
                datatable.setOnMouseClicked((MouseEvent event) -> {
                    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.CIDADE_FORM));

                        AnchorPane p;
                        try {
                            Cidade cidade = (Cidade) datatable.getSelectionModel().getSelectedItem();

                            p = (AnchorPane) fxmlLoader.load();

                            CidadeFormController controller = fxmlLoader.<CidadeFormController>getController();

                            controller.initData(cidade);

                            LotoAdmin.centerContainer.getChildren().clear();
                            LotoAdmin.centerContainer.getChildren().add(p);
                        } catch (IOException ex) {
                            Logger.getLogger(CidadeListController.class.getName()).log(Level.SEVERE, null, ex);

                            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
                        }
                    }
                });

            } catch (Exception e) {
                throw e;
            }

        } catch (Exception ex) {
            Logger.getLogger(CidadeListController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
        }
    }

    public void adicionar(ActionEvent acEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.CIDADE_FORM));
        try {
            AnchorPane p = (AnchorPane) fxmlLoader.load();

            CidadeFormController controller = fxmlLoader.<CidadeFormController>getController();

            controller.initData(new Cidade());

            LotoAdmin.centerContainer.getChildren().clear();
            LotoAdmin.centerContainer.getChildren().add(p);

        } catch (IOException ex) {
            Logger.getLogger(CidadeListController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
        }
    }

}
