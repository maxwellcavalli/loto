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
import br.com.loto.admin.util.DateUtils;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author maxwe
 */
public class EquipamentoFormController implements Initializable {

    @FXML
    public TextField txtSerial;

    @FXML
    public TextField txtDescricao;

    @FXML
    public CheckBox ckAtivo;

    @FXML
    public DatePicker dpAquisicao;
    
    @FXML
    public TextField txtUuid;

    private Equipamento equipamento;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void salvar(ActionEvent event) throws Exception {

        List<String> messages = new ArrayList<>();
        if (this.equipamento == null) {
            this.equipamento = new Equipamento();
        }

        this.equipamento.setSerial(txtSerial.getText());
        this.equipamento.setDescricao(txtDescricao.getText());
        this.equipamento.setAtivo(ckAtivo.selectedProperty().getValue());
        if (dpAquisicao.getValue() != null) {
            this.equipamento.setDataAquisicao(DateUtils.asDate(dpAquisicao.getValue()));
        }

        if ("".equals(this.equipamento.getSerial().trim())) {
            messages.add("Serial Inválido");
        }

        if ("".equals(this.equipamento.getDescricao().trim())) {
            messages.add("Descrição Inválida");
        }

        if (messages.isEmpty()) {
            try {
                this.equipamento = EquipamentoService.getInstance().persistir(this.equipamento);
            } catch (IllegalArgumentException | IllegalAccessException | SQLException ex) {
                Logger.getLogger(EquipamentoFormController.class.getName()).log(Level.SEVERE, null, ex);

                FxmlUtil.getInstance().openMessageDialog(event, ex);
            }
        } else {
            FxmlUtil.getInstance().openMessageDialog(event, messages);
        }
    }

    public void voltar(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.EQUIPAMENTO_LIST));
        try {
            AnchorPane p = (AnchorPane) fxmlLoader.load();

            LotoAdmin.centerContainer.getChildren().clear();
            LotoAdmin.centerContainer.getChildren().add(p);
        } catch (IOException ex) {
            Logger.getLogger(EquipamentoFormController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(event, ex);
        }
    }

    public void initData(Equipamento equipamento) {
        txtSerial.setText(equipamento.getSerial());
        txtDescricao.setText(equipamento.getDescricao());
        ckAtivo.setSelected(equipamento.isAtivo());
        txtUuid.setText(equipamento.getUuid());

        if (equipamento.getDataAquisicao() != null) {
            dpAquisicao.setValue(DateUtils.asLocalDate(equipamento.getDataAquisicao()));
        }

        this.equipamento = equipamento;
    }

}
