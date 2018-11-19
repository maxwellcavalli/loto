/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.controller.sistema.resultadoLoteria;

import br.com.loto.admin.FxmlFiles;
import br.com.loto.admin.LotoAdmin;
import br.com.loto.admin.domain.ResultadoLoteria;
import br.com.loto.admin.domain.helper.ComboHelper;
import br.com.loto.admin.service.ResultadoLoteriaService;
import br.com.loto.admin.util.FxmlUtil;
import br.com.loto.core.fx.datatable.util.TableColumnUtil;
import br.com.loto.shared.domain.type.TipoLoteria;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author maxwe
 */
public class ResultadoLoteriaListController implements Initializable {

    @FXML
    public JFXTextField txtFiltro;
    
    @FXML
    public JFXComboBox<ComboHelper> cbTipoLoteria;
    

    @FXML
    public TableView<ResultadoLoteria> datatable;
    
    private List<ComboHelper> tiposLoteria;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTipoLoteria();
        configureTextFields();
    }
    
     void configureTextFields() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();

            if (text.matches("[0-9]*")) {
                return change;
            }

            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        txtFiltro.setTextFormatter(textFormatter);
    }
    
     void configurarTipoLoteria() {
        this.tiposLoteria = new ArrayList<>();
        for (TipoLoteria tl : TipoLoteria.values()) {
            tiposLoteria.add(new ComboHelper(tl.getKey(), tl.getDescription()));
        }

        this.cbTipoLoteria.getItems().clear();
        
        this.cbTipoLoteria.getItems().add(new ComboHelper(0, ""));
        this.tiposLoteria.forEach(tl
                -> this.cbTipoLoteria.getItems().add(tl)
        );

        this.cbTipoLoteria.setConverter(new StringConverter<ComboHelper>() {
            @Override
            public String toString(ComboHelper object) {
                return object.getDescricao();
            }

            @Override
            public ComboHelper fromString(String string) {
                return null;
            }
        });
    }

    public void pesquisar(ActionEvent acEvent) {
        try {
            String descricao = txtFiltro.getText();
            
            ComboHelper tl = this.cbTipoLoteria.getSelectionModel().getSelectedItem();
            Integer tipoLoteria = tl == null || tl.getCodigo() == 0 ? null : tl.getCodigo(); 
            Integer concurso = null;
            
            if (!"".equals(txtFiltro.getText())){
                concurso = Integer.parseInt(txtFiltro.getText());
            }
            
            List<ResultadoLoteria> list = ResultadoLoteriaService.getInstance().pesquisar(tipoLoteria, concurso);

            TableColumn<ResultadoLoteria, String> descricaoColumn = TableColumnUtil.createStringColumn("Tipo Loteria", 500, (ResultadoLoteria s) -> 
                    TipoLoteria.get(s.getIdTipoLoteria()).getDescription());
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

            ResultadoLoteriaFormController controller = fxmlLoader.<ResultadoLoteriaFormController>getController();

            controller.initData(new ResultadoLoteria());

            LotoAdmin.centerContainer.getChildren().clear();
            LotoAdmin.centerContainer.getChildren().add(p);

        } catch (IOException ex) {
            Logger.getLogger(ResultadoLoteriaListController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
        }
    }

}
