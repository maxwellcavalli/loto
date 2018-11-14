/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.controller.sistema.resultadoLoteria;

import br.com.loto.admin.FxmlFiles;
import br.com.loto.admin.LotoAdmin;
import br.com.loto.admin.domain.ResultadoLoteria;
import br.com.loto.admin.domain.ResultadoLoteriaNumeros;
import br.com.loto.admin.domain.helper.ComboHelper;
import br.com.loto.admin.service.ResultadoLoteriaNumerosService;
import br.com.loto.admin.service.ResultadoLoteriaService;
import br.com.loto.admin.util.FxmlUtil;
import br.com.loto.shared.domain.type.TipoLoteria;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author maxwe
 */
public class ResultadoLoteriaFormController implements Initializable {

    @FXML
    public ComboBox<ComboHelper> cbTipoLoteria;

    @FXML
    public TextField txtConcurso;

    @FXML
    public TextField txtValorAcumulado;

    @FXML
    public HBox panelDezenas;

    //variaveis globais
    private ResultadoLoteria resultadoLoteria;

    private List<ResultadoLoteriaNumeros> numerosLoteria;

    private List<ComboHelper> tiposLoteria;

    private List<TextField> camposDezenas;

    //--
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    void configurarTipoLoteria() {
        this.tiposLoteria = new ArrayList<>();
        for (TipoLoteria tl : TipoLoteria.values()) {
            tiposLoteria.add(new ComboHelper(tl.getKey(), tl.getDescription()));
        }

        this.cbTipoLoteria.getItems().clear();
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

    public void salvar(ActionEvent event) {

        List<String> messages = new ArrayList<>();
        if (this.resultadoLoteria == null) {
            this.resultadoLoteria = new ResultadoLoteria();
        }

        this.resultadoLoteria.setIdTipoLoteria(this.cbTipoLoteria.getSelectionModel().getSelectedItem().getCodigo());

        if ("".equals(txtConcurso.getText())) {
            messages.add("Concurso Inválido");
        } else {
            this.resultadoLoteria.setConcurso(Integer.parseInt(txtConcurso.getText().trim()));
        }

        if ("".equals(txtValorAcumulado.getText())) {
            messages.add("Valor Acumulado Inválido");
        } else {
            this.resultadoLoteria.setValorAcumulado(new BigDecimal(txtValorAcumulado.getText().trim()));
        }

        try {
            for (int i = 0; i < this.panelDezenas.getChildren().size(); i++) {
                if (!"".equals(((TextField) this.panelDezenas.getChildren().get(i)).getText().trim())) {
                    Integer n = Integer.parseInt(((TextField) this.panelDezenas.getChildren().get(i)).getText().trim());

                    this.numerosLoteria.get(i).setNumero(n);

                }
            }
        } catch (Exception e) {
            messages.add("Existe algum número inválido nas dezenas");
        }

        if (messages.isEmpty()) {
            try {
                this.resultadoLoteria = ResultadoLoteriaService.getInstance().persistir(this.resultadoLoteria, this.numerosLoteria);
                this.numerosLoteria = this.resultadoLoteria.getNumerosLoteria();

            } catch (IllegalArgumentException | IllegalAccessException | SQLException ex) {
                Logger.getLogger(ResultadoLoteriaFormController.class.getName()).log(Level.SEVERE, null, ex);

                FxmlUtil.getInstance().openMessageDialog(event, ex);
            } catch (Exception ex) {
                Logger.getLogger(ResultadoLoteriaFormController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            FxmlUtil.getInstance().openMessageDialog(event, messages);
        }
    }

    public void voltar(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.RESULTADO_LOTERIA_LIST));
        try {
            AnchorPane p = (AnchorPane) fxmlLoader.load();

            LotoAdmin.centerContainer.getChildren().clear();
            LotoAdmin.centerContainer.getChildren().add(p);
        } catch (IOException ex) {
            Logger.getLogger(ResultadoLoteriaFormController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(event, ex);
        }
    }

    public void initData(ResultadoLoteria resultadoLoteria) {
        configurarTipoLoteria();

        this.numerosLoteria = new ArrayList<>();
        if (resultadoLoteria.getId() != null) {
            ComboHelper tipoLoteria = this.tiposLoteria.stream().filter(el -> el.getCodigo() == resultadoLoteria.getIdTipoLoteria()).findFirst().get();
            cbTipoLoteria.getSelectionModel().select(tipoLoteria);
            try {
                this.numerosLoteria = ResultadoLoteriaNumerosService.getInstance().pesquisar(resultadoLoteria);

                this.panelDezenas.getChildren().clear();
                this.numerosLoteria.stream().map((rln) -> {
                    TextField txt = new TextField();
                    txt.setText(rln.getNumero().toString());
                    return txt;
                }).forEachOrdered((txt) -> {
                    this.panelDezenas.getChildren().add(txt);
                    HBox.setMargin(txt, new Insets(0, 10, 0, 0));
                });

            } catch (SQLException ex) {
                Logger.getLogger(ResultadoLoteriaFormController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            cbTipoLoteria.getSelectionModel().select(tiposLoteria.get(0));
            changeTipoLoteria(null);
        }

        cbTipoLoteria.setDisable(resultadoLoteria.getId() != null);

        if (resultadoLoteria.getConcurso() != null) {
            txtConcurso.setText(resultadoLoteria.getConcurso().toString());
        }

        if (resultadoLoteria.getValorAcumulado() != null) {
            txtValorAcumulado.setText(resultadoLoteria.getValorAcumulado().toBigInteger().toString());
        }

        this.resultadoLoteria = resultadoLoteria;
    }

    public void changeTipoLoteria(ActionEvent event) {
        ComboHelper comboHelper = cbTipoLoteria.getSelectionModel().getSelectedItem();
        TipoLoteria tipoLoteria = TipoLoteria.get(comboHelper.getCodigo());

        this.panelDezenas.getChildren().clear();
        this.camposDezenas = new ArrayList<>();
        this.numerosLoteria = new ArrayList<>();
        int i = 0;
        while (i < tipoLoteria.getNumeroDezenas()) {
            TextField txt = new TextField();

            this.panelDezenas.getChildren().add(txt);
            HBox.setMargin(txt, new Insets(0, 10, 0, 0));

            this.numerosLoteria.add(new ResultadoLoteriaNumeros());
            i++;
        }
    }
}
