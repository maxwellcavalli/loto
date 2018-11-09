/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.control;

import com.jfoenix.controls.JFXTextField;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import view.action.ConverterObjectToText;
import view.action.SearchActionListener;

/**
 *
 * @author mcavalli
 */
public class JFXAutoComplete<T extends Object> extends JFXTextField {

    private ContextMenu entriesPopup;
//
    private SearchActionListener searchActionListener;
    private ConverterObjectToText converterObjectToText;

    private T objectSelecionado;

    @FXML
    public void initialize() {
        entriesPopup = new ContextMenu();
    }

    public JFXAutoComplete() {
        super();

        //entriesPopup = new ContextMenu();
        setListner();
    }

    /**
     * "Suggestion" specific listners
     */
    private void setListner() {
        //Add "suggestions" by changing text
        textProperty().addListener((observable, oldValue, newValue) -> {
            if (entriesPopup == null){
                entriesPopup = new ContextMenu();
            }
            
            String enteredText = getText();
            //always hide suggestion if nothing has been entered (only "spacebars" are dissalowed in TextFieldWithLengthLimit)
            if (enteredText == null || enteredText.isEmpty()) {
                entriesPopup.hide();
            } else {
                List<T> filteredEntries = null;
                if (searchActionListener != null) {
                    filteredEntries = searchActionListener.onSearch(enteredText);
                }

                //some suggestions are found
                if (filteredEntries != null && !filteredEntries.isEmpty()) {
                    populatePopup(filteredEntries, enteredText);
                    if (!entriesPopup.isShowing()) { //optional
                        entriesPopup.show(JFXAutoComplete.this, Side.BOTTOM, 0, 0); //position of popup
                    }
                    //no suggestions -> hide
                } else {
                    entriesPopup.hide();
                }
            }
        });

        //Hide always by focus-in (optional) and out
        focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (entriesPopup != null) {
                entriesPopup.hide();
            }
        });
    }

    /**
     * Populate the entry set with the given search results. Display is limited
     * to 10 entries, for performance.
     *
     * @param searchResult The set of matching strings.
     */
    private void populatePopup(List<T> searchResult, String searchReauest) {
        //List of "suggestions"
        List<CustomMenuItem> menuItems = new LinkedList<>();
        //List size - 10 or founded suggestions count
        int maxEntries = 10;
        int count = Math.min(searchResult.size(), maxEntries);
        //Build list as set of labels
        for (int i = 0; i < count; i++) {
            T o = searchResult.get(i);

            String result = "";
            if (converterObjectToText != null) {
                result = converterObjectToText.convert(o);
            } else {
                result = "Converter not defined";
            }

//            final String result = searchResult.get(i);
            //label with graphic (text flow) to highlight founded subtext in suggestions
            Label entryLabel = new Label();
            entryLabel.setGraphic(buildTextFlow(result, searchReauest));
            entryLabel.setPrefHeight(10);  //don't sure why it's changed with "graphic"
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            menuItems.add(item);

            final String tmp = result;

            //if any suggestion is select set it into text and close popup
            item.setOnAction((ActionEvent actionEvent) -> {
                setText(tmp);
                setObjectSelecionado(o);

                positionCaret(tmp.length());
                entriesPopup.hide();
            });
        }

        //"Refresh" context menu
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);
    }

    public void clean() {
        setObjectSelecionado(null);
        setText("");
    }

    public TextFlow buildTextFlow(String text, String filter) {
        int filterIndex = text.toLowerCase().indexOf(filter.toLowerCase());
        Text textBefore = new Text(text.substring(0, filterIndex));
        Text textAfter = new Text(text.substring(filterIndex + filter.length()));
        Text textFilter = new Text(text.substring(filterIndex, filterIndex + filter.length())); //instead of "filter" to keep all "case sensitive"
        textFilter.setFill(Color.ORANGE);
        textFilter.setFont(Font.font("Helvetica", FontWeight.BOLD, 12));
        return new TextFlow(textBefore, textFilter, textAfter);
    }

    public void setSearchActionListener(SearchActionListener searchActionListener) {
        this.searchActionListener = searchActionListener;
    }

    public void setConverterObjectToText(ConverterObjectToText converterObjectToText) {
        this.converterObjectToText = converterObjectToText;
    }

    public T getObjectSelecionado() {
        return objectSelecionado;
    }

    public void setObjectSelecionado(T objectSelecionado) {
        this.objectSelecionado = objectSelecionado;
    }

}
