/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.core.fx.datatable;

import br.com.loto.core.fx.datatable.interfaces.IActionColumn;
import br.com.loto.core.fx.datatable.interfaces.IActiveColumn;
import br.com.loto.core.fx.datatable.interfaces.ICondidionalLabelButonColumn;
import javafx.scene.Node;

/**
 *
 * @author maxwe
 * @param <T>
 */
public class ActionColumnButton<T> {

    private String title;
    private IActionColumn<T> action;
    private boolean disabled = false;
    private IActiveColumn<T> activeColumn;
    private ICondidionalLabelButonColumn<T> conditionalLabel;
    private Node button;

    public IActiveColumn<T> getActiveColumn() {
        return activeColumn;
    }

    public void setActiveColumn(IActiveColumn<T> activeColumn) {
        this.activeColumn = activeColumn;
    }

    public ActionColumnButton(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public IActionColumn<T> getAction() {
        return action;
    }

    public void setAction(IActionColumn<T> action) {
        this.action = action;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Node getButton() {
        return button;
    }

    public void setButton(Node button) {
        this.button = button;
    }

    public ICondidionalLabelButonColumn<T> getConditionalLabel() {
        return conditionalLabel;
    }

    public void setConditionalLabel(ICondidionalLabelButonColumn<T> conditionalLabel) {
        this.conditionalLabel = conditionalLabel;
    }

}
