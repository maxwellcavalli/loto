/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.core.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author maxwe
 */
public interface DatabaseRecord<T> {

    public T onRecord(ResultSet rs) throws  SQLException;
    
}
