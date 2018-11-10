/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.util;

import br.com.loto.admin.controller.sistema.equipamento.EquipamentoFormController;
import br.com.loto.admin.controller.modal.MensagemController;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author maxwe
 */
public class FxmlUtil {

    private static FxmlUtil instance;

    public static FxmlUtil getInstance() {
        if (instance == null) {
            instance = new FxmlUtil();
        }

        return instance;
    }

    private FxmlUtil() {
    }
    
     public void openMessageDialog(ActionEvent event, String message) {
        Stage stage = new Stage(StageStyle.UTILITY);

        FXMLLoader root;
        try {
            root = new FXMLLoader(getClass().getResource("/br/com/loto/admin/view/modal/Mensagem.fxml"));
            Parent p = root.load();

            MensagemController mensagemController = root.<MensagemController>getController();

            StringBuilder builder = new StringBuilder();
            builder.append(message).append("\n");

            mensagemController.txtMensagens.setText(builder.toString());

            stage.setScene(new Scene(p));
            stage.setTitle("Mensagens");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.setResizable(false);
            stage.setMaximized(false);

            stage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent ev) -> {
                if (KeyCode.ESCAPE == ev.getCode()) {
                    stage.close();
                }
            });

            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(EquipamentoFormController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void openMessageDialog(ActionEvent event, List<String> messages) {
        Stage stage = new Stage(StageStyle.UTILITY);

        FXMLLoader root;
        try {
            root = new FXMLLoader(getClass().getResource("/br/com/loto/admin/view/modal/Mensagem.fxml"));
            Parent p = root.load();

            MensagemController mensagemController = root.<MensagemController>getController();

            StringBuilder builder = new StringBuilder();
            messages.stream().forEach(el -> builder.append(el).append("\n"));

            mensagemController.txtMensagens.setText(builder.toString());

            stage.setScene(new Scene(p));
            stage.setTitle("Mensagens");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.setResizable(false);
            stage.setMaximized(false);

            stage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent ev) -> {
                if (KeyCode.ESCAPE == ev.getCode()) {
                    stage.close();
                }
            });

            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(EquipamentoFormController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public void openMessageDialog(ActionEvent event, Exception ex) {
        Stage stage = new Stage(StageStyle.UTILITY);

        FXMLLoader root;
        try {
            root = new FXMLLoader(getClass().getResource("/br/com/loto/admin/view/modal/Mensagem.fxml"));
            Parent p = root.load();

            MensagemController mensagemController = root.<MensagemController>getController();

            StringBuilder builder = new StringBuilder();
            builder.append(ex.getMessage());

            mensagemController.txtMensagens.setText(builder.toString());

            stage.setScene(new Scene(p));
            stage.setTitle("Mensagens");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.setResizable(false);
            stage.setMaximized(false);

            stage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent ev) -> {
                if (KeyCode.ESCAPE == ev.getCode()) {
                    stage.close();
                }
            });

            stage.show();

        } catch (IOException e) {
            Logger.getLogger(EquipamentoFormController.class.getName()).log(Level.SEVERE, null, e);
        }

    }
    
    

}
