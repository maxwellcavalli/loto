/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.controller.modal;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author maxwe
 */
public class MensagemController implements Initializable {

    @FXML
    public TextArea txtMensagens;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
   
    public void close(ActionEvent event) {
        System.out.println("br.com.loto.admin.controller.modal.MensagemController.close()");
        JFXButton jFXButton = (JFXButton) event.getSource();
        Stage stage = (Stage) jFXButton.getScene().getWindow();

        stage.close();
    }

}
