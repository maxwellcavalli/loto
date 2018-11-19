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
public enum TipoLoteria {
    MEGA_SENA(1, "Mega Sena", 6),
    LOTO_FACIL(2, "Loto Fácil", 15),
    QUINA(3, "Quina", 5),
    LOTO_MANIA(4, "Loto Mania", 20),
    TIME_MANIA(5, "Time Mania", 7),
    DIA_DE_SORTE(6, "Dia de Sorte", 7);

    private int key;
    private String description;
    private int numeroDezenas;

    private TipoLoteria(int key, String description, int numeroDezenas) {
        this.key = key;
        this.description = description;
        this.numeroDezenas = numeroDezenas;
    }

    public static TipoLoteria get(int key) {
        for (TipoLoteria tl : values()) {
            if (tl.getKey() == key) {
                return tl;
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

    public int getNumeroDezenas() {
        return numeroDezenas;
    }

    public void setNumeroDezenas(int numeroDezenas) {
        this.numeroDezenas = numeroDezenas;
    }

}
