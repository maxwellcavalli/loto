/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.core.fx.datatable.interfaces;

/**
 *
 * @author maxwe
 */
public interface IActiveColumn<T> {

    public boolean active(T t);
}
