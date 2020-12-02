package br.com.lucassouza.ntech.kalcullar.entidades;

import java.io.Serializable;

public class Carro implements Serializable {

     int CODIGO;
     String MICROFONE;


    public int getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(int CODIGO) {
        this.CODIGO = CODIGO;
    }

    public String getMICROFONE() {
        return MICROFONE;
    }

    public void setMICROFONE(String CITYGASOLINA) {
        this.MICROFONE = CITYGASOLINA;
    }


}
