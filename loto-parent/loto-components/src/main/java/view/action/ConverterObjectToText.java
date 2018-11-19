/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.action;

/**
 *
 * @author mcavalli
 */
public interface ConverterObjectToText<T> {

    public String convert(T o);
    
}
