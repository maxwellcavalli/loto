/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.core.fx.datatable.util;

import br.com.loto.core.fx.datatable.ActionColumn;
import br.com.loto.core.fx.datatable.interfaces.IActionColumn;
import br.com.loto.core.fx.datatable.interfaces.ITableColumnValue;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/**
 *
 * @author maxwe
 */
public class TableColumnUtil {

    public static <T extends Object> TableColumn<T, String> createStringColumn(String title, int prefWidth, ITableColumnValue<T> tableColumnValue) {
        TableColumn<T, String> tableColumn = new TableColumn<>(title);
        tableColumn.setCellValueFactory((TableColumn.CellDataFeatures<T, String> param)
                -> new ReadOnlyStringWrapper(tableColumnValue.getValue(param.getValue()))
        );
        tableColumn.setPrefWidth(prefWidth);
        return tableColumn;
    }
    
    public static <T extends Object> TableColumn<T, Boolean> createButtonColumn(String title, int prefWidth, TableView<T> tableView, IActionColumn iActionColumn){
        TableColumn<T, Boolean> actionColumn = new TableColumn<>(title);
        actionColumn.setPrefWidth(80);
        actionColumn.setCellValueFactory((TableColumn.CellDataFeatures<T, Boolean> features) -> new SimpleBooleanProperty(features.getValue() != null));

        actionColumn.setCellFactory((TableColumn<T, Boolean> personBooleanTableColumn) -> new ActionColumn<>(tableView, (T t) -> {
            iActionColumn.onAction(t);
        }));
        
        return actionColumn;
    }

}
