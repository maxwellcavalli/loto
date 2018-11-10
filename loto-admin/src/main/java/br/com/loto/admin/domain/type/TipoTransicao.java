/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.domain.type;

/**
 *
 * @author mcavalli
 */
public enum TipoTransicao {
    FADE_IN(1, "Fade In"),
    FADE_OUT(2, "Fade Out");

    private int key;
    private String description;

    private TipoTransicao(int key, String description) {
        this.key = key;
        this.description = description;
    }

    public static TipoTransicao get(int key) {
        for (TipoTransicao t : TipoTransicao.values()) {
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
