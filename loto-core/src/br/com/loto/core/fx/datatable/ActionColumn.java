/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.core.fx.datatable;

import br.com.loto.core.fx.datatable.interfaces.IActionColumn;
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
public class ActionColumn<T> extends TableCell<T, Boolean> {

    // a button for adding a new person.
    final Button addButton = new Button("Delete");
    // pads and centers the add button in the cell.
    //final StackPane paddedButton = new StackPane();

    final HBox hbox = new HBox();
    // records the y pos of the last button press so that the add person dialog can be shown next to the cell.
    final DoubleProperty buttonY = new SimpleDoubleProperty();

    public ActionColumn(final TableView<T> table, IActionColumn<T> action) {
        this(table, "Delete", action);
    }

    public ActionColumn(final TableView<T> table, String title, IActionColumn<T> action) {
        hbox.setPadding(new Insets(3));
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);

        addButton.setText(title);

        hbox.getChildren().add(addButton);
        addButton.setOnMousePressed((MouseEvent mouseEvent) -> {
            buttonY.set(mouseEvent.getScreenY());
        });
        addButton.setOnAction((ActionEvent actionEvent) -> {
            table.getSelectionModel().select(getTableRow().getIndex());

            if (action != null) {
                action.onAction(table.getSelectionModel().getSelectedItem());
            }
        });
    }

    public ActionColumn(final TableView<T> table, List<ActionColumnButton<T>> actionsColumnButton) {
        hbox.setPadding(new Insets(3));
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);

        if (actionsColumnButton != null) {
            for (ActionColumnButton ac : actionsColumnButton) {
                Button b = new Button(ac.getTitle());

                hbox.getChildren().add(b);
                b.setOnMousePressed((MouseEvent mouseEvent) -> {
                    buttonY.set(mouseEvent.getScreenY());
                });
                b.setOnAction((ActionEvent actionEvent) -> {
                    table.getSelectionModel().select(getTableRow().getIndex());

                    if (ac.getAction() != null) {
                        ac.getAction().onAction(table.getSelectionModel().getSelectedItem());
                    }
                });
            }
        }

    }

    /**
     * places an add button in the row only if the row is not empty.
     */
    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(hbox);
        } else {
            setGraphic(null);
        }
    }

}
