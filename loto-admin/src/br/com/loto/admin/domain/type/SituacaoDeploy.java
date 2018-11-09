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
public enum SituacaoDeploy {

    CADASTRANDO(1, "Cadastrando"),
    PRONTO_DEPLOY(2, "Pronto para Deploy"),
    IMPLANTADO(3, "Implantado");

    private int key;
    private String description;

    private SituacaoDeploy(int key, String description) {
        this.key = key;
        this.description = description;
    }

    public static SituacaoDeploy get(int key) {
        for (SituacaoDeploy s : values()) {
            if (s.getKey() == key) {
                return s;
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
