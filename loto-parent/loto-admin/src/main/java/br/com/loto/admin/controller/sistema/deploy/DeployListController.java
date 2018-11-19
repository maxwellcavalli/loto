/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.controller.sistema.deploy;

import br.com.loto.admin.FxmlFiles;
import br.com.loto.admin.LotoAdmin;
import br.com.loto.admin.controller.sistema.estabelecimento.EstabelecimentoFormController;
import br.com.loto.admin.domain.Cidade;
import br.com.loto.admin.domain.Deploy;
import br.com.loto.admin.domain.Estabelecimento;
import br.com.loto.admin.domain.Estado;
import br.com.loto.admin.domain.type.SituacaoDeploy;
import br.com.loto.admin.service.CidadeService;
import br.com.loto.admin.util.FxmlUtil;
import br.com.loto.admin.service.DeployService;
import br.com.loto.admin.service.EstabelecimentoService;
import br.com.loto.admin.service.EstadoService;
import br.com.loto.core.fx.datatable.util.TableColumnUtil;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import view.action.ConverterObjectToText;
import view.action.SearchActionListener;
import view.control.JFXAutoComplete;

/**
 * FXML Controller class
 *
 * @author maxwe
 */
public class DeployListController implements Initializable {

    @FXML
    public JFXTextField txtFiltro;

    @FXML
    public TableView<Deploy> datatable;

    //filtros
    @FXML
    public ComboBox<Estado> cbEstado;

    @FXML
    public JFXAutoComplete<Cidade> txtCidade;

    @FXML
    public JFXAutoComplete<Estabelecimento> txtEstabelecimento;

    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    final SimpleDateFormat sdfNH = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        popularEstados();

        configAutoCompleteCidade();
        configuraAutocompleteEstabelecimento();
    }

    void configAutoCompleteCidade() {
        txtCidade.setConverterObjectToText((ConverterObjectToText<Cidade>) (Cidade t) -> t.getNome());

        txtCidade.setSearchActionListener((SearchActionListener<Cidade>) (String query) -> {
            Estado e = cbEstado.getSelectionModel().getSelectedItem();
            Long estado = e == null ? null : e.getId();

            if (estado == null) {
                return new ArrayList<>(0);
            } else {
                try {
                    return CidadeService.getInstance().pesquisar(query, estado);
                } catch (SQLException ex) {
                    Logger.getLogger(EstabelecimentoFormController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return null;
        });
    }

    private void configuraAutocompleteEstabelecimento() {
        txtEstabelecimento.setConverterObjectToText((ConverterObjectToText<Estabelecimento>) (Estabelecimento t) -> t.getDescricao());

        txtEstabelecimento.setSearchActionListener((SearchActionListener<Estabelecimento>) (String query) -> {
            Cidade c = txtCidade.getObjectSelecionado();
            Estado e = cbEstado.getSelectionModel().getSelectedItem();

            Long cidade = c == null ? null : c.getId();
            Long estado = e == null ? null : e.getId();

            if (cidade == null) {
                return new ArrayList<>(0);
            } else {
                try {
                    return EstabelecimentoService.getInstance().pesquisar(query, estado, cidade, 10);
                } catch (SQLException ex) {
                    Logger.getLogger(EstabelecimentoFormController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return null;
        });

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
            Logger.getLogger(DeployFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void changeEstado(ActionEvent event) {
        txtCidade.clean();
        txtEstabelecimento.clean();
    }

    public void pesquisar(ActionEvent acEvent) {
        try {
            String descricao = txtFiltro.getText();
            Cidade cidadeO = txtCidade.getObjectSelecionado();
            Estado estadoO = cbEstado.getSelectionModel().getSelectedItem();
            Estabelecimento estabelecimentoO = txtEstabelecimento.getObjectSelecionado();

            Long cidade = cidadeO == null || cidadeO.getId() == null ? null : cidadeO.getId();
            Long estado = estadoO == null || estadoO.getId() == null ? null : estadoO.getId();
            Long estabelecimento = estabelecimentoO == null ? null : estabelecimentoO.getId();

            List<Deploy> list = DeployService.getInstance().pesquisar(descricao, estado, cidade, estabelecimento);

            TableColumn<Deploy, String> descricaoColumn = TableColumnUtil.createStringColumn("Descrição", 200, (Deploy s) -> s.getDescricao());
            TableColumn<Deploy, String> estabelecimentoColumn = TableColumnUtil.createStringColumn("Estabelecimento", 200, (Deploy s)
                    -> s.getEstabelecimento().getDescricao());

            TableColumn<Deploy, String> cidadeColumn = TableColumnUtil.createStringColumn("Cidade", 200, (Deploy s)
                    -> s.getEstabelecimento().getEstabelecimentoEndereco() == null ? "" : s.getEstabelecimento().getEstabelecimentoEndereco().getCidade().getNome());

            TableColumn<Deploy, String> estadoColumn = TableColumnUtil.createStringColumn("UF", 60, (Deploy s)
                    -> s.getEstabelecimento().getEstabelecimentoEndereco() == null ? ""
                    : s.getEstabelecimento().getEstabelecimentoEndereco().getCidade().getEstado().getSigla());

            TableColumn<Deploy, String> dataColumn = TableColumnUtil.createStringColumn("Data", 120, (Deploy s)
                    -> sdf.format(s.getData()));

            TableColumn<Deploy, String> dataValidadeColumn = TableColumnUtil.createStringColumn("Data Validade", 120, (Deploy s)
                    -> s.getDataValidade() == null ? "" : sdfNH.format(s.getDataValidade()));

            TableColumn<Deploy, String> situacaoColumn = TableColumnUtil.createStringColumn("Situação", 140, (Deploy s)
                    -> SituacaoDeploy.get(s.getSituacao()).getDescription());

            TableColumn<Deploy, String> ativoColumn = TableColumnUtil.createStringColumn("Ativo", 50, (Deploy s) -> s.getAtivoStr());

            datatable.getColumns().clear();
            datatable.getColumns().setAll(estabelecimentoColumn, descricaoColumn, cidadeColumn, estadoColumn,
                    dataColumn, dataValidadeColumn, situacaoColumn, ativoColumn);
            datatable.setItems(FXCollections.observableArrayList(list));

            try {
                datatable.setOnMouseClicked((MouseEvent event) -> {
                    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.DEPLOY_FORM));

                        AnchorPane p;
                        try {
                            Deploy deploy = (Deploy) datatable.getSelectionModel().getSelectedItem();

                            p = (AnchorPane) fxmlLoader.load();

                            DeployFormController controller = fxmlLoader.<DeployFormController>getController();

                            controller.initData(deploy, true);

                            LotoAdmin.centerContainer.getChildren().clear();
                            LotoAdmin.centerContainer.getChildren().add(p);
                        } catch (IOException ex) {
                            Logger.getLogger(DeployListController.class.getName()).log(Level.SEVERE, null, ex);

                            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
                        }
                    }
                });

            } catch (Exception e) {
                throw e;
            }

        } catch (Exception ex) {
            Logger.getLogger(DeployListController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
        }
    }

    public void adicionar(ActionEvent acEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.DEPLOY_FORM));
        try {
            AnchorPane p = (AnchorPane) fxmlLoader.load();

            DeployFormController controller = fxmlLoader.<DeployFormController>getController();

            controller.initData(new Deploy(), false);

            LotoAdmin.centerContainer.getChildren().clear();
            LotoAdmin.centerContainer.getChildren().add(p);

        } catch (IOException ex) {
            Logger.getLogger(DeployListController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
        }
    }

}
