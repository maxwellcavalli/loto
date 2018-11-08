/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.view.util;

import br.com.loto.admin.domain.Cliente;
import br.com.loto.admin.service.ClienteService;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author maxwe
 */
public class AutoCompleteCliente {

    public static void bindAutoCompleteToComboBox(ComboBox<Cliente> comboBox, Long cidade, Long estado) {

        final Integer MAX_RECORDS = 50;

        /**
         * if mouse pressed: select all of the text field
         */
        comboBox.getEditor().setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (comboBox.getEditor().isFocused() && !comboBox.getEditor().getText().isEmpty()) {
                            comboBox.getEditor().selectAll();
                        }
                    }
                });
            }
        });

        /**
         * events on text input
         */
        comboBox.setOnKeyReleased(new EventHandler<KeyEvent>() {

            private List<Cliente> reducedList = new ArrayList<Cliente>();

            @Override
            public void handle(KeyEvent event) {

                if (event.getCode().isLetterKey() || event.getCode().isDigitKey() || event.getCode().equals(KeyCode.BACK_SPACE)) {

                    /**
                     * Open comboBox if letter, number or backspace
                     */
                    comboBox.show();

                    String temp = comboBox.getEditor().getText();
                    reducedList = new ArrayList<>();

                    try {
                        reducedList = ClienteService.getInstance().pesquisar(temp, cidade, estado, MAX_RECORDS);
                    } catch (SQLException ex) {
                        Logger.getLogger(AutoCompleteCliente.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    /**
                     * all elements are cleared, the reduced list will be added.
                     * First element is selected
                     */
                    comboBox.getItems().clear();
                    comboBox.getItems().addAll(reducedList);
                    comboBox.getSelectionModel().select(0);
                    comboBox.getEditor().setText(temp);

                } else if (event.getCode().equals(KeyCode.ENTER)) {

                    /**
                     * if enter, the element which is selected will be applied
                     * to the text field and the dropdown will be closed
                     */
                    if (comboBox.getSelectionModel().getSelectedIndex() != -1) {
                        comboBox.getEditor().setText(comboBox.getItems().get((comboBox.getSelectionModel().getSelectedIndex())).getNome());
                    } else {
                        comboBox.getEditor().setText(comboBox.getItems().get(0).getNome());
                    }

                } else if (event.getCode().equals(KeyCode.DOWN)) {

                    /**
                     * arrow down shows the dropdown
                     */
                    comboBox.show();
                }

                /**
                 * Tab marks everything (when tabbing into the field
                 */
                if (event.getCode().equals(KeyCode.TAB)) {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (comboBox.getEditor().isFocused() && !comboBox.getEditor().getText().isEmpty()) {
                                comboBox.getEditor().selectAll();
                            }
                        }
                    });

                } else {
                    /**
                     * all entries except for tab put the caret on the last
                     * character
                     */
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            comboBox.getEditor().positionCaret(comboBox.getEditor().getText().length());
                        }
                    });
                }

            }
        });

//        comboBox.focusedProperty().addListener(new ChangeListener<Boolean>() {
//
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//
//                if (oldValue) {
//
//                    boolean emptyTextField = comboBox.getEditor().getText().isEmpty();
//
//                    if (comboBox.getSelectionModel().getSelectedIndex() != -1) {
//                        comboBox.getEditor().setText(comboBox.getItems().get(comboBox.getSelectionModel().getSelectedIndex()).getNome());
//                    }
//
//                    String temp = comboBox.getEditor().getText();
//                    
//                    
//                    List<Cliente> reducedList = new ArrayList<>();
//
//                    try {
//                        reducedList = ClienteService.getInstance().pesquisar(temp, cidade, estado, MAX_RECORDS);
//                    } catch (SQLException ex) {
//                        Logger.getLogger(AutoCompleteCliente.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//
//                    comboBox.getItems().clear();
//                    comboBox.getItems().addAll(reducedList);
//
//                    if (!emptyTextField) {
//                        //comboBox.getSelectionModel().select(temp);
//                    } else {
//                        comboBox.getEditor().setText("");
//                    }
//                }
//            }
//        });

    }

}
