/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.controller.sistema.equipamento;

import br.com.loto.admin.FxmlFiles;
import br.com.loto.admin.LotoAdmin;
import br.com.loto.admin.util.FxmlUtil;
import br.com.loto.admin.domain.Equipamento;
import br.com.loto.admin.service.EquipamentoService;
import br.com.loto.core.fx.datatable.util.TableColumnUtil;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
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
public class EquipamentoListController implements Initializable {

    @FXML
    public JFXTextField txtFiltro;

    @FXML
    public TableView<Equipamento> datatable;
    
    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void pesquisar(ActionEvent acEvent) {
        try {
            String numSerie = txtFiltro.getText();
            List<Equipamento> list = EquipamentoService.getInstance().pesquisar(numSerie);

            TableColumn<Equipamento, String> serialColumn = TableColumnUtil.createStringColumn("Serial", 150, (Equipamento s) -> s.getSerial());
            TableColumn<Equipamento, String> descricaoColumn = TableColumnUtil.createStringColumn("Descrição", 620, (Equipamento s) -> s.getDescricao());
            TableColumn<Equipamento, String> dataAquisicaoColumn = TableColumnUtil.createStringColumn("Data Aquisição", 100, (Equipamento s) -> 
                    s.getDataAquisicao() == null ? "" : sdf.format(s.getDataAquisicao()));
            
            TableColumn<Equipamento, String> ativoColumn = TableColumnUtil.createStringColumn("Ativo", 50, (Equipamento s) -> s.getAtivoStr());

            datatable.getColumns().clear();
            datatable.getColumns().setAll(serialColumn, descricaoColumn, dataAquisicaoColumn, ativoColumn);
            datatable.setItems(FXCollections.observableArrayList(list));

            try {
                datatable.setOnMouseClicked((MouseEvent event) -> {
                    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.EQUIPAMENTO_FORM));

                        AnchorPane p;
                        try {
                            Equipamento equipamento = (Equipamento) datatable.getSelectionModel().getSelectedItem();

                            p = (AnchorPane) fxmlLoader.load();

                            EquipamentoFormController controller = fxmlLoader.<EquipamentoFormController>getController();

                            controller.initData(equipamento);

                            LotoAdmin.centerContainer.getChildren().clear();
                            LotoAdmin.centerContainer.getChildren().add(p);
                        } catch (IOException ex) {
                            Logger.getLogger(EquipamentoListController.class.getName()).log(Level.SEVERE, null, ex);

                            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
                        }
                    }
                });

            } catch (Exception e) {
                throw e;
            }

        } catch (Exception ex) {
            Logger.getLogger(EquipamentoFormController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
        }
    }

    public void adicionar(ActionEvent acEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.EQUIPAMENTO_FORM));
        try {
            AnchorPane p = (AnchorPane) fxmlLoader.load();

            LotoAdmin.centerContainer.getChildren().clear();
            LotoAdmin.centerContainer.getChildren().add(p);

        } catch (IOException ex) {
            Logger.getLogger(EquipamentoFormController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
        }
    }

}
