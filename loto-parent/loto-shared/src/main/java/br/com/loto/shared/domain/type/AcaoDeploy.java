/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.shared.domain.type;

/**
 *
 * @author maxwe
 */
public enum AcaoDeploy {

    INSERCAO(1),
    ALTERACAO(2),
    EXCLUSAO(3),
    NADA(4);

    private int code;

    private AcaoDeploy(int code) {
        this.code = code;
    }

    public static AcaoDeploy get(int code) {
        for (AcaoDeploy a : values()) {
            if (a.getCode() == code) {
                return a;
            }
        }

        return null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
