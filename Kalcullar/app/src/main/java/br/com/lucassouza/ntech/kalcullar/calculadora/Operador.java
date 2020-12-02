package br.com.lucassouza.ntech.kalcullar.calculadora;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;

public class Operador implements Serializable {

    private double valor = 0;
    private String texto = "";
    private NumberFormat nf = NumberFormat.getNumberInstance();

    public void setCaracter(String caracter) throws ParseException {
        String valor_exibido = texto;

            String[] valor_cortado = texto.split(",");

        if (valor_cortado.length >=2){
            if (valor_cortado[1].toString().length() <= 2){
                texto += caracter;
            }
        }else if(valor_exibido.length() <= 12){
            if (caracter.toString().equals("duas ")){
                texto += "2";
            }else{
                texto += caracter;
            }

        }
            valor = +nf.parse( texto).doubleValue();
    }

    public String getValorTexto() {

        return nf.format(valor);
    }

    public double getValor() {
        return this.valor;
    }

    public void removerUltimoCaracter() throws ParseException {
        if (texto.length() > 1) {
            texto = texto.substring(0, texto.length() - 1);
            valor = nf.parse(texto).doubleValue();
        } else {
            texto = "0";
            valor = 0;
        }
    }
}
