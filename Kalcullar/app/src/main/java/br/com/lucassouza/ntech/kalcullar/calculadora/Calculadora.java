package br.com.lucassouza.ntech.kalcullar.calculadora;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;

public class Calculadora implements Serializable {

    int igual = 0;
    private Operador operador1 = new Operador();
    private Operador operador2 = new Operador();
    private Operacao operacao = null;
    private boolean finalizado = false;
    int valoramis= 0 ;
    double resultado = 0;
    static String resultado_conta = "";
    private NumberFormat nf = NumberFormat.getNumberInstance();

    public void setCaracter(String caracter) throws ParseException {
       if(finalizado) {
            operador1 = new Operador();
            operador2 = new Operador();
            operacao = null;
            finalizado = false;

            //operador1.setCaracter(String.valueOf(resultado));
        }
        else
        if (operacao == null) {
            if (caracter.toString().equals("duas ")){
                operador1.setCaracter("2");
            }else{
                operador1.setCaracter(caracter);
            }

        } else if (!finalizado) {
            operador2.setCaracter(caracter);
        }
    }

    public void setOperacao(Operacao operacao) throws ParseException {
        this.operacao = operacao;
        if(finalizado) {
            operador2 = new Operador();
            operador1 = new Operador();
            operador1.setCaracter(resultado_conta);
            valoramis=1;
            finalizado = false;

            //operador1.setCaracter(String.valueOf(resultado));
        }
    }


    public String getValorTexto() {

        if(valoramis == 1){
            String op1 = operador1.getValorTexto();
            String op2 = operador2.getValorTexto();
            String texto = "";
          if (operacao == null) {
                if(op1.length() <= 12 && op2.length() <= 12){
                    texto += "";
                }

            } else if (!finalizado) {

                texto += op1 + operacao;

                valoramis=0;
                finalizado = false;
            }else  {

                texto += op1 + operacao + op2;
            }

            return texto;
        }else{
            String op1 = operador1.getValorTexto();
            String op2 = operador2.getValorTexto();
            String texto = "";
            if (valoramis == 1){
                texto += op1 + operacao;
                valoramis=0;
                finalizado = false;
            }else if (operacao == null) {
                if(op1.length() <= 12 && op2.length() <= 12){
                    texto += "";
                }

            } else if (!finalizado) {

                texto += op1 + operacao;


            }else  {

                texto += op1 + operacao + op2;
            }

            return texto;
        }




    }

    public String getValorTextoPrincipal() {
        String op1 = operador1.getValorTexto();
        String op2 = operador2.getValorTexto();

        String texto = "";

        if (operacao == null) {
            texto += op1;
        } else if (!finalizado) {

            texto += op2;
        } else {
            texto += getResultado();
            resultado_conta = texto;
        }

        return texto;
    }

    public String getResultado() {
    if(valoramis == 1){
        double op1 = operador1.getValor();
        double op2 = operador2.getValor();
        resultado = 0;
        if (operacao == Operacao.ADICAO) {
            resultado = op1 + op2;
        } else if (operacao == Operacao.SUBTRACAO) {
            resultado = op1 - op2;
        } else if (operacao == Operacao.MULTIPLICACAO) {
            resultado = op1 * op2;
        } else if (operacao == Operacao.DIVISAO) {
            if(op1 == 0 && op2 == 0){
                resultado =0;
            }else{
                resultado = op1 / op2;
            }

        } else if (operacao == Operacao.PORCENTAGEM) {
            resultado = op1 * op2 / 100;
        } else {
            throw new UnsupportedOperationException("Operação não implementada.");
        }
        return nf.format(resultado);
        }else{
        double op1 = operador1.getValor();
        double op2 = operador2.getValor();
        resultado = 0;
        if (operacao == Operacao.ADICAO) {
            resultado = op1 + op2;
        } else if (operacao == Operacao.SUBTRACAO) {
            resultado = op1 - op2;
        } else if (operacao == Operacao.MULTIPLICACAO) {
            resultado = op1 * op2;
        } else if (operacao == Operacao.DIVISAO) {
            if(op1 == 0 && op2 == 0){
                resultado =0;
            }else{
                resultado = op1 / op2;
            }

        } else if (operacao == Operacao.PORCENTAGEM) {
            resultado = op1 * op2 / 100;
        } else {
            throw new UnsupportedOperationException("Operação não implementada.");
        }
        return nf.format(resultado);
    }

    }

    public void calcular() {
        this.finalizado = true;
    }

    public void removerUltimoCaracter() throws ParseException {
        if (operacao == null) {
            operador1.removerUltimoCaracter();
        } else if (!finalizado) {
            operador2.removerUltimoCaracter();
        }
    }

    @Override
    public String toString() {
        String texto = getValorTexto();
        if (getValorTextoPrincipal().trim().length() > 0) {
            texto += " = " + getValorTextoPrincipal();
        }
        return texto;
    }
}

