/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.controller.sistema.estado;

import br.com.loto.admin.FxmlFiles;
import br.com.loto.admin.LotoAdmin;
import br.com.loto.admin.domain.Estado;
import br.com.loto.admin.service.EstadoService;
import br.com.loto.admin.util.FxmlUtil;
import java.io.IOException;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author maxwe
 */
public class EstadoFormController implements Initializable {

    @FXML
    public TextField txtDescricao;

    @FXML
    public TextField txtSigla;

    @FXML
    public CheckBox ckAtivo;

    //variaveis globais
    private Estado estado;

    //--
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void salvar(ActionEvent event) {

        List<String> messages = new ArrayList<>();
        if (this.estado == null) {
            this.estado = new Estado();
        }

        this.estado.setNome(txtDescricao.getText());
        this.estado.setSigla(txtSigla.getText());
        this.estado.setAtivo(ckAtivo.selectedProperty().getValue());

        if ("".equals(this.estado.getNome().trim())) {
            messages.add("Nome Inválido");
        }

        if ("".equals(this.estado.getSigla().trim())) {
            messages.add("Sigla Inválida");
        }

        if (messages.isEmpty()) {
            try {
                this.estado = EstadoService.getInstance().persistir(this.estado);
            } catch (IllegalArgumentException | IllegalAccessException | SQLException ex) {
                Logger.getLogger(EstadoFormController.class.getName()).log(Level.SEVERE, null, ex);

                FxmlUtil.getInstance().openMessageDialog(event, ex);
            } catch (Exception ex) {
                Logger.getLogger(EstadoFormController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            FxmlUtil.getInstance().openMessageDialog(event, messages);
        }
    }

    public void voltar(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.ESTADO_LIST));
        try {
            AnchorPane p = (AnchorPane) fxmlLoader.load();

            LotoAdmin.centerContainer.getChildren().clear();
            LotoAdmin.centerContainer.getChildren().add(p);
        } catch (IOException ex) {
            Logger.getLogger(EstadoFormController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(event, ex);
        }
    }

    public void initData(Estado estado) {
        txtDescricao.setText(estado.getNome());
        txtSigla.setText(estado.getSigla());
        ckAtivo.setSelected(estado.isAtivo());

        this.estado = estado;
    }
}
