/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.controller.sistema.estabelecimento;

import br.com.loto.admin.FxmlFiles;
import br.com.loto.admin.LotoAdmin;
import br.com.loto.admin.util.FxmlUtil;
import br.com.loto.admin.domain.Estabelecimento;
import br.com.loto.admin.service.EstabelecimentoService;
import br.com.loto.core.fx.datatable.util.TableColumnUtil;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
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

/**
 * FXML Controller class
 *
 * @author maxwe
 */
public class EstabelecimentoListController implements Initializable {

    @FXML
    public JFXTextField txtFiltro;

    @FXML
    public TableView<Estabelecimento> datatable;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void pesquisar(ActionEvent acEvent) {
        try {
            String descricao = txtFiltro.getText();
            List<Estabelecimento> list = EstabelecimentoService.getInstance().pesquisar(descricao);
            
            TableColumn<Estabelecimento, String> descricaoColumn = TableColumnUtil.createStringColumn("Descrição", 750, (Estabelecimento s) -> s.getDescricao());
            TableColumn<Estabelecimento, String> ativoColumn = TableColumnUtil.createStringColumn("Ativo", 50, (Estabelecimento s) -> s.getAtivoStr());

            datatable.getColumns().clear();
            datatable.getColumns().setAll(descricaoColumn, ativoColumn);
            datatable.setItems(FXCollections.observableArrayList(list));
            
            try {
                datatable.setOnMouseClicked((MouseEvent event) -> {
                    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.ESTABELECIMENTO_FORM));

                        AnchorPane p;
                        try {
                            Estabelecimento estabelecimento = (Estabelecimento) datatable.getSelectionModel().getSelectedItem();

                            p = (AnchorPane) fxmlLoader.load();

                            EstabelecimentoFormController controller = fxmlLoader.<EstabelecimentoFormController>getController();

                            controller.initData(estabelecimento);

                            LotoAdmin.centerContainer.getChildren().clear();
                            LotoAdmin.centerContainer.getChildren().add(p);
                        } catch (IOException ex) {
                            Logger.getLogger(EstabelecimentoListController.class.getName()).log(Level.SEVERE, null, ex);

                            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
                        }
                    }
                });

            } catch (Exception e) {
                throw e;
            }

        } catch (Exception ex) {
            Logger.getLogger(EstabelecimentoListController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
        }
    }

    public void adicionar(ActionEvent acEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.ESTABELECIMENTO_FORM));
        try {
            AnchorPane p = (AnchorPane) fxmlLoader.load();

            EstabelecimentoFormController controller = fxmlLoader.<EstabelecimentoFormController>getController();

            controller.initData(new Estabelecimento());

            LotoAdmin.centerContainer.getChildren().clear();
            LotoAdmin.centerContainer.getChildren().add(p);

        } catch (IOException ex) {
            Logger.getLogger(EstabelecimentoListController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
        }
    }

}
