/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.core.fx.datatable;

import br.com.loto.core.fx.datatable.interfaces.IActionColumn;

/**
 *
 * @author maxwe
 */
public class ActionColumnButton<T> {

    private String title;
    private IActionColumn<T> action;

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
    
    
    
}
