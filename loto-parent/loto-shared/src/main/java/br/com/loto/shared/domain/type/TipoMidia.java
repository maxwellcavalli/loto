/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.shared.domain.type;

/**
 *
 * @author mcavalli
 */
public enum TipoMidia {
    ESTATICA(1, "Estática"),
    DINAMICA(2, "Dinamica");

    private int key;
    private String description;

    private TipoMidia(int key, String description) {
        this.key = key;
        this.description = description;
    }

    public static TipoMidia get(int key) {
        for (TipoMidia t : TipoMidia.values()) {
            if (t.getKey() == key) {
                return t;
            }
        }
        return null;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
