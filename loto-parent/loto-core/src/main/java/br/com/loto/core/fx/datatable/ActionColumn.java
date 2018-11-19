/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.core.fx.datatable;

import br.com.loto.core.fx.datatable.interfaces.IActionColumn;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 *
 * @author maxwe
 */
public class ActionColumn<T> extends TableCell<T, T> {

    // a button for adding a new person.
    final Button addButton = new Button("Delete");
    // pads and centers the add button in the cell.
    //final StackPane paddedButton = new StackPane();

    final HBox hbox = new HBox();
    // records the y pos of the last button press so that the add person dialog can be shown next to the cell.
    final DoubleProperty buttonY = new SimpleDoubleProperty();

    private List<ActionColumnButton<T>> actionsColumnButton;
    private TableView<T> table;

    public ActionColumn(final TableView<T> table, IActionColumn<T> action) {
        this(table, "Delete", action);
    }

    public ActionColumn(final TableView<T> table, String title, IActionColumn<T> action) {
        this.actionsColumnButton = new ArrayList<>(1);

        ActionColumnButton<T> actionColumnButton = new ActionColumnButton(title);
        actionColumnButton.setAction(action);
        this.actionsColumnButton.add(actionColumnButton);
        this.table = table;

//        hbox.setPadding(new Insets(3));
//        hbox.setSpacing(10);
//        hbox.setAlignment(Pos.CENTER);
//
//        addButton.setText(title);
//
//        hbox.getChildren().add(addButton);
//        addButton.setOnMousePressed((MouseEvent mouseEvent) -> {
//            buttonY.set(mouseEvent.getScreenY());
//        });
//        addButton.setOnAction((ActionEvent actionEvent) -> {
//            table.getSelectionModel().select(getTableRow().getIndex());
//
//            if (action != null) {
//                action.onAction(table.getSelectionModel().getSelectedItem());
//            }
//        });
    }

    public ActionColumn(final TableView<T> table, List<ActionColumnButton<T>> actionsColumnButton) {
        this.actionsColumnButton = actionsColumnButton;
        this.table = table;

    }

    /**
     * places an add button in the row only if the row is not empty.
     */
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (!empty) {
            hbox.setPadding(new Insets(3));
            hbox.setSpacing(10);
            hbox.setAlignment(Pos.CENTER);
            hbox.getChildren().clear();

            if (actionsColumnButton != null) {
                actionsColumnButton.forEach((ac) -> {
                    Button b = new Button(ac.getTitle());
                    b.setDisable(ac.isDisabled());

                    ac.setButton(b);

                    hbox.getChildren().add(b);
                    b.setOnMousePressed((MouseEvent mouseEvent) -> {
                        buttonY.set(mouseEvent.getScreenY());
                    });

                    //System.err.println(this.currentItem);
                    b.setOnAction((ActionEvent actionEvent) -> {
                        table.getSelectionModel().select(getTableRow().getIndex());

                        if (ac.getAction() != null) {
                            ac.getAction().onAction(table.getSelectionModel().getSelectedItem());
                        }
                    });
                });
            }

            actionsColumnButton.forEach(ac -> {
                if (ac.getActiveColumn() != null) {
                    ac.getButton().setDisable(ac.getActiveColumn().active(item));
                }
                
                if (ac.getConditionalLabel() != null){
                    ((Button)ac.getButton()).setText(ac.getConditionalLabel().getLabel(item));
                }
                
            });

            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(hbox);
        } else {
            setGraphic(null);
        }
    }

}
