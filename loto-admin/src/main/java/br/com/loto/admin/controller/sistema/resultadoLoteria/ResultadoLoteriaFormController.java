/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.controller.sistema.resultadoLoteria;

import br.com.loto.admin.FxmlFiles;
import br.com.loto.admin.LotoAdmin;
import br.com.loto.admin.domain.ResultadoLoteria;
import br.com.loto.admin.service.ResultadoLoteriaService;
import br.com.loto.admin.util.FxmlUtil;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author maxwe
 */
public class ResultadoLoteriaFormController implements Initializable {

    @FXML
    public TextField txtDescricao;

    @FXML
    public TextField txtConcurso;

    @FXML
    public TextField txtNumeros;

    @FXML
    public TextField txtValorAcumulado;

    //variaveis globais
    private ResultadoLoteria resultadoLoteria;

    //--
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void salvar(ActionEvent event) {

        List<String> messages = new ArrayList<>();
        if (this.resultadoLoteria == null) {
            this.resultadoLoteria = new ResultadoLoteria();
        }

        this.resultadoLoteria.setNome(txtDescricao.getText());

        if ("".equals(this.resultadoLoteria.getNome().trim())) {
            messages.add("Nome Inválido");
        }

        if ("".equals(txtConcurso.getText())) {
            messages.add("Concurso Inválido");
        } else {
            this.resultadoLoteria.setConcurso(Integer.parseInt(txtConcurso.getText()));
        }

        this.resultadoLoteria.setNumeros(txtNumeros.getText());

        if ("".equals(txtValorAcumulado.getText())) {
            messages.add("Valor Acumulado Inválido");
        } else {
            this.resultadoLoteria.setValorAcumulado(new BigDecimal(txtValorAcumulado.getText()));
        }

        if (messages.isEmpty()) {
            try {
                this.resultadoLoteria = ResultadoLoteriaService.getInstance().persistir(this.resultadoLoteria);
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
        txtDescricao.setText(resultadoLoteria.getNome());
        txtConcurso.setText(resultadoLoteria.getConcurso().toString());
        txtNumeros.setText(resultadoLoteria.getNumeros());
        txtValorAcumulado.setText(resultadoLoteria.getValorAcumulado().toBigInteger().toString());

        this.resultadoLoteria = resultadoLoteria;
    }
}
