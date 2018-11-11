/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.controller.sistema.resultadoLoteria;

import br.com.loto.admin.FxmlFiles;
import br.com.loto.admin.LotoAdmin;
import br.com.loto.admin.controller.sistema.estado.EstadoFormController;
import br.com.loto.admin.domain.Estado;
import br.com.loto.admin.domain.ResultadoLoteria;
import br.com.loto.admin.service.ResultadoLoteriaService;
import br.com.loto.admin.util.FxmlUtil;
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
public class ResultadoLoteriaListController implements Initializable {

    @FXML
    public JFXTextField txtFiltro;

    @FXML
    public TableView<ResultadoLoteria> datatable;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void pesquisar(ActionEvent acEvent) {
        try {
            String descricao = txtFiltro.getText();
            List<ResultadoLoteria> list = ResultadoLoteriaService.getInstance().pesquisar(descricao);

            TableColumn<ResultadoLoteria, String> descricaoColumn = TableColumnUtil.createStringColumn("Descricao", 500, (ResultadoLoteria s) -> s.getNome());
            TableColumn<ResultadoLoteria, String> concursoColumn = TableColumnUtil.createStringColumn("Concurso", 100, (ResultadoLoteria s) -> s.getConcurso().toString());
            
            datatable.getColumns().clear();
            datatable.getColumns().setAll(descricaoColumn, concursoColumn);
            datatable.setItems(FXCollections.observableArrayList(list));

            try {
                datatable.setOnMouseClicked((MouseEvent event) -> {
                    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.RESULTADO_LOTERIA_FORM));

                        AnchorPane p;
                        try {
                            ResultadoLoteria t = (ResultadoLoteria) datatable.getSelectionModel().getSelectedItem();

                            p = (AnchorPane) fxmlLoader.load();

                            ResultadoLoteriaFormController controller = fxmlLoader.<ResultadoLoteriaFormController>getController();

                            controller.initData(t);

                            LotoAdmin.centerContainer.getChildren().clear();
                            LotoAdmin.centerContainer.getChildren().add(p);
                        } catch (IOException ex) {
                            Logger.getLogger(ResultadoLoteriaListController.class.getName()).log(Level.SEVERE, null, ex);

                            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
                        }
                    }
                });

            } catch (Exception e) {
                throw e;
            }

        } catch (Exception ex) {
            Logger.getLogger(ResultadoLoteriaListController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
        }
    }

    public void adicionar(ActionEvent acEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.RESULTADO_LOTERIA_FORM));
        try {
            AnchorPane p = (AnchorPane) fxmlLoader.load();

            EstadoFormController controller = fxmlLoader.<EstadoFormController>getController();

            controller.initData(new Estado());

            LotoAdmin.centerContainer.getChildren().clear();
            LotoAdmin.centerContainer.getChildren().add(p);

        } catch (IOException ex) {
            Logger.getLogger(ResultadoLoteriaListController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
        }
    }

}
