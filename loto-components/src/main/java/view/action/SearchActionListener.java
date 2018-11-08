/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.action;

import java.util.List;

/**
 *
 * @author mcavalli
 */
public interface SearchActionListener<T> {
    
    public List<T> onSearch(String query);
    
}
