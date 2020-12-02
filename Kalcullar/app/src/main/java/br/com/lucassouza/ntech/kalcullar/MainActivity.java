package br.com.lucassouza.ntech.kalcullar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import br.com.lucassouza.ntech.kalcullar.calculadora.Calculadora;
import br.com.lucassouza.ntech.kalcullar.calculadora.Operacao;
import br.com.lucassouza.ntech.kalcullar.entidades.Carro;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

import static java.lang.Character.*;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private Calculadora calculadora = new Calculadora();
    private TextView visor;
    private TextView visorPrincipal;
    public int contagem =0;
    public Carro carro;
    public bdmycar BDmycar;
    Boolean microfone_ligado = false, falado = false;
    TextToSpeech texto_falado;
    ImageButton img_btn;
    String carro_recebe, valorum = "", valordois = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        texto_falado = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR){
                    texto_falado.setLanguage(Locale.getDefault());
                    texto_falado.setSpeechRate(0.7f);
                }
            }
        });
        img_btn = findViewById(R.id.microfone1);
        this.visor = (TextView) findViewById(R.id.visor1);
        this.visorPrincipal = (TextView) findViewById(R.id.visorPrincipa1);
        atualizarVisor();
        Conexao();
        img_btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, "Pode Falar", Toast.LENGTH_SHORT).show();
                speak();
                return false;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("calculadora", this.calculadora);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getSerializable("calculadora") != null) {
            this.calculadora = (Calculadora) savedInstanceState.getSerializable("calculadora");
            atualizarVisor();
        }
    }
    private void setCaracter(String caracter) {

        try {
            if (caracter.toString().equals("duas ")){
                calculadora.setCaracter("2");
            }else{
                calculadora.setCaracter(caracter);
            }

            atualizarVisor();
        } catch (Exception e) {
            e.printStackTrace();
            // Toast.makeText(getBaseContext(), "O!", Toast.LENGTH_SHORT).show();
        }

    }
    public void Conexao(){
        try {
            carro = new Carro();
            BDmycar = new bdmycar(MainActivity.this);
            carro_recebe = BDmycar.Buscarcarro();
            if (carro_recebe.toString().equals("")){
            }else{

                if (carro_recebe.toString().equals("s")) {
                    img_btn.setImageResource(R.drawable.ic_baseline_mic_off_24);
                    microfone_ligado = false;
                    Toast.makeText(this, "Modo de áudio Desligado, caso queira ativar o calculo por vóz precione o botão do microfone", Toast.LENGTH_SHORT).show();
                } else {
                    img_btn.setImageResource(R.drawable.ic_baseline_mic_24);
                    microfone_ligado = true;
                    Toast.makeText(this, "Modo de áudio Ligado, caso queira ativar o calculo por vóz precione o botão do microfone", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception j){

        }
    }
    private void atualizarVisor() {

        if (this.calculadora != null) {
            visor.setText(calculadora.getValorTexto());
            visorPrincipal.setText( calculadora.getValorTextoPrincipal());

        } else {
            visor.setText("");
            visorPrincipal.setText("0");
        }

    }


    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale a sua conta");
        try {

            startActivityForResult(intent, REQUEST_CODE);
        }catch (Exception j){

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)  {
        super.onActivityResult(requestCode, resultCode, data);
        calculadora = new Calculadora();
        atualizarVisor();
        contagem=0;
        switch (requestCode){
            case REQUEST_CODE:{
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String valor_resultado = result.get(0).toString();
                    try {
                        String[] valor_separdo =  valor_resultado.split("-");
                        if (valor_separdo[0].toString().length() <=12 && valor_separdo[1].toString().length() <=12 ){
                            String valor1= NumeroEscrito(valor_separdo[0].toString());
                            setCaracter(valor1);
                            setOperacao(Operacao.SUBTRACAO);
                            contagem=0;
                            String valor2= NumeroEscrito(valor_separdo[1].toString());
                            setCaracter(valor2);
                            calculadora.calcular();
                            falado=true;
                            atualizarVisor();
                        }

                    }catch (Exception e){
                        try{
                            String[] valor_separdo =  valor_resultado.split("x");

                            if (valor_separdo[0].toString().length() <=12 && valor_separdo[1].toString().length() <=12){
                                String duas = valor_separdo[0];
                                String duas2 = valor_separdo[1];
                                if (duas == "duas " || duas.equals("duas ")){
                                    setCaracter("2");
                                }else{
                                    String valor1= NumeroEscrito(valor_separdo[0].toString());
                                    setCaracter(valor1);
                                }
                                setOperacao(Operacao.MULTIPLICACAO);
                                contagem=0;
                                if (duas2 == "duas " || duas2.equals("duas ")){
                                    setCaracter("2");
                                }else{
                                    String valor2 = NumeroEscrito(valor_separdo[1].toString());
                                    setCaracter(valor2);
                                }
                                calculadora.calcular();
                                falado=true;
                                atualizarVisor();
                            }
                        }catch (Exception k){

                            try{
                                String[] valor_separdo =  valor_resultado.split("/");
                                String[] valor_separdo2 =  valor_separdo[1].toLowerCase().split("POR".toLowerCase());

                                if (valor_separdo[0].toString().length() <=12 && valor_separdo[1].toString().length() <=12){
                                    if (!valor_separdo2.toString().equals(null) && !valor_separdo2.toString().equals("") && valor_separdo2.toString().length() >= 1){
                                        String valor1= NumeroEscrito(valor_separdo[0].toString());
                                        setCaracter(valor1);
                                        setOperacao(Operacao.DIVISAO);
                                        contagem=0;
                                        String valor2 = NumeroEscrito(valor_separdo[1].toString());
                                        setCaracter(valor2);
                                        calculadora.calcular();
                                        falado=true;
                                        atualizarVisor();
                                    }else{
                                        String valor1= NumeroEscrito(valor_separdo[0].toString());
                                        setCaracter(valor1);
                                        setOperacao(Operacao.DIVISAO);
                                        contagem=0;
                                        String valor2= NumeroEscrito(valor_separdo[1].toString());
                                        setCaracter(valor2);
                                        calculadora.calcular();
                                        falado=true;
                                        atualizarVisor();
                                    }

                                }

                            }catch (Exception m){

                                try{
                                    String[] valor_separdo =  valor_resultado.split("["+ Pattern.quote("+") + "]+");

                                    if (valor_separdo[0].toString().length() <=12 && valor_separdo[1].toString().length() <=12){
                                        String valor1= NumeroEscrito(valor_separdo[0].toString());
                                        setCaracter(valor1);
                                        setOperacao(Operacao.ADICAO);
                                        contagem=0;
                                        String valor2= NumeroEscrito(valor_separdo[1].toString());
                                        setCaracter(valor2);
                                        calculadora.calcular();
                                        falado=true;
                                        atualizarVisor();

                                    }
                                }catch (Exception n){

                                    try {
                                        String[] valor_separdo =  valor_resultado.toLowerCase().split("MENOS".toLowerCase());

                                        if (valor_separdo[0].toString().length() <=12 && valor_separdo[1].toString().length() <=12){
                                            String valor1= NumeroEscrito(valor_separdo[0].toString());
                                            setCaracter(valor1);
                                            setOperacao(Operacao.SUBTRACAO);
                                            contagem=0;
                                            String valor2= NumeroEscrito(valor_separdo[1].toString());
                                            setCaracter(valor2);
                                            calculadora.calcular();
                                            falado=true;
                                            atualizarVisor();
                                        }
                                    }catch (Exception p) {

                                        try{
                                            String[] valor_separdo =  valor_resultado.toLowerCase().split("VEZES".toLowerCase());
                                            String duas = valor_separdo[0];
                                            String duas2 = valor_separdo[1];
                                            if (valor_separdo[0].toString().length() <=12 && valor_separdo[1].toString().length() <=12){
                                                if (duas == "duas " || duas.equals("duas ")){
                                                    setCaracter("2");
                                                }else{
                                                    String valor1= NumeroEscrito(valor_separdo[0].toString());
                                                    setCaracter(valor1);
                                                }
                                                setOperacao(Operacao.MULTIPLICACAO);
                                                contagem=0;
                                                if (duas2 == "duas " || duas2.equals("duas ")){
                                                    setCaracter("2");
                                                }else{
                                                    String valor2= NumeroEscrito(valor_separdo[1].toString());
                                                    setCaracter(valor2);
                                                }
                                                calculadora.calcular();
                                                falado=true;
                                                atualizarVisor();
                                                contagem=0;
                                            }
                                        }catch (Exception q) {

                                            try{
                                                String[] valor_separdo =  valor_resultado.toLowerCase().split("DIVIDIDO".toLowerCase());
                                                if (valor_separdo[0].toString().length() <=12 && valor_separdo[1].toString().length() <=12){
                                                    String valor1= NumeroEscrito(valor_separdo[0].toString());
                                                    setCaracter(valor1);
                                                    setOperacao(Operacao.DIVISAO);
                                                    contagem=0;
                                                    String valor2= NumeroEscrito(valor_separdo[1].toString());
                                                    setCaracter(valor2);
                                                    calculadora.calcular();
                                                    falado=true;
                                                    atualizarVisor();
                                                    contagem=0;
                                                }
                                            }catch (Exception r) {

                                                try{
                                                    String[] valor_separdo =  valor_resultado.toLowerCase().split("MAIS".toLowerCase());

                                                    if (valor_separdo[0].toString().length() <=12 && valor_separdo[1].toString().length() <=12){
                                                        String valor1= NumeroEscrito(valor_separdo[0].toString());
                                                        setCaracter(valor1);
                                                        setOperacao(Operacao.ADICAO);
                                                        contagem=0;
                                                        String valor2= NumeroEscrito(valor_separdo[1].toString());
                                                        setCaracter(valor2);
                                                        calculadora.calcular();
                                                        falado=true;
                                                        atualizarVisor();
                                                        contagem=0;
                                                    }

                                                }catch (Exception o) {

                                                    try{
                                                        String[] valor_separdo =  valor_resultado.toLowerCase().split("%");
                                                        String[] valor_separdo2 =  valor_separdo[1].split("de ");
                                                        if (valor_separdo[0].toString().length() <=12 && valor_separdo[1].toString().length() <=12){
                                                            String valor1= NumeroEscrito(valor_separdo[0].toString());
                                                            setCaracter(valor1);
                                                            setOperacao(Operacao.PORCENTAGEM);
                                                            contagem=0;
                                                            String valor2= NumeroEscrito(valor_separdo2[1].toString());
                                                            setCaracter(valor2);
                                                            calculadora.calcular();
                                                            falado=true;
                                                            atualizarVisor();
                                                            contagem=0;
                                                        }
                                                    }catch (Exception h) {

                                                        try{
                                                            String[] valor_separdo =  valor_resultado.toLowerCase().split("PORCENTO".toLowerCase());
                                                            String[] valor_separdo2 =  valor_separdo[1].split("de ");
                                                            if (valor_separdo[0].toString().length() <=12 && valor_separdo[1].toString().length() <=12){
                                                                String valor1= NumeroEscrito(valor_separdo[0].toString());
                                                                setCaracter(valor1);
                                                                setOperacao(Operacao.PORCENTAGEM);
                                                                contagem=0;
                                                                String valor2= NumeroEscrito(valor_separdo2[1].toString());
                                                                setCaracter(valor2);
                                                                calculadora.calcular();
                                                                falado=true;
                                                                atualizarVisor();
                                                                contagem=0;
                                                            }

                                                        }catch (Exception j) {

                                                            if (valor_resultado.toLowerCase().equals("LIMPAR TELA".toLowerCase())){

                                                                calculadora = new Calculadora();
                                                                falado=true;
                                                                atualizarVisor();
                                                                contagem=0;

                                                            }else if(valor_resultado.toLowerCase().equals("DESFAZER".toLowerCase())){

                                                                try {
                                                                    calculadora.removerUltimoCaracter();
                                                                    falado=true;
                                                                    atualizarVisor();
                                                                    contagem=0;
                                                                } catch (Exception y) {

                                                                    //Toast.makeText(getBaseContext(), "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }else{

                                                                calculadora = new Calculadora();
                                                                atualizarVisor();
                                                                contagem=0;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }break;
        }
    }

    public String NumeroEscrito(String numero) {
        String valor = numero;
        if(numero.equals("zero")){
            numero = "0";
        }else if(numero.equals("um")){
            numero = "1";
        }else if(numero.equals("dois")){
            numero = "2";
        }else if(numero.equals("três")){
            numero = "3";
        }else if(numero.equals("quatro")){
            numero = "4";
        }else if(numero.equals("cinco")){
            numero = "5";
        }else if(numero.equals("seis")){
            numero = "6";
        }else if(numero.equals("sete")){
            numero = "7";
        }else if(numero.equals("oito")){
            numero = "8";
        }else if(numero.equals("nove")){
            numero = "9";
        }else{
            for (int i = 0; i < numero.length(); i++){
                if (isDigit(numero.charAt(i))){
                    return valor;
                }else{
                 //   numero="0";
                }
            }
        }
        return numero;
    }



    private void setOperacao(Operacao operation) throws ParseException {
        calculadora.setOperacao(operation);
        atualizarVisor();
    }

    public void handleButtonUm(View view) {
        if (visorPrincipal.getText().toString().length() <= 12){
            if (microfone_ligado){
                texto_falado.speak("1", TextToSpeech.QUEUE_FLUSH,null);
            }

        }
        falado=false;
        setCaracter("1");
    }

    public void handleButtonDois(View view) {
        if (visorPrincipal.getText().toString().length() <= 12){
            if (microfone_ligado){
                texto_falado.speak("2", TextToSpeech.QUEUE_FLUSH,null);
            }

        }

        setCaracter("2");
    }

    public void handleButtonTres(View view) {
        if (visorPrincipal.getText().toString().length() <= 12){
            if (microfone_ligado){
                texto_falado.speak("3", TextToSpeech.QUEUE_FLUSH,null);
            }

        }

        setCaracter("3");
    }

    public void handleButtonQuatro(View view) {
        if (visorPrincipal.getText().toString().length() <= 12){
            if (microfone_ligado){
                texto_falado.speak("4", TextToSpeech.QUEUE_FLUSH,null);
            }

        }

        setCaracter("4");
    }

    public void handleButtonCinco(View view) {
        if (visorPrincipal.getText().toString().length() <= 12){
            if (microfone_ligado){
                texto_falado.speak("5", TextToSpeech.QUEUE_FLUSH,null);
            }

        }
        setCaracter("5");

    }

    public void handleButtonSeis(View view) {
        if (visorPrincipal.getText().toString().length() <= 12){
            if (microfone_ligado){
                texto_falado.speak("6", TextToSpeech.QUEUE_FLUSH,null);
            }

        }
        setCaracter("6");
    }

    public void handleButtonSete(View view) {
        if (visorPrincipal.getText().toString().length() <= 12){
            if (microfone_ligado){
                texto_falado.speak("7", TextToSpeech.QUEUE_FLUSH,null);
            }

        }

        setCaracter("7");
    }

    public void handleButtonOito(View view) {
        if (visorPrincipal.getText().toString().length() <= 12){
            if (microfone_ligado){
                texto_falado.speak("8", TextToSpeech.QUEUE_FLUSH,null);
            }

        }

        setCaracter("8");
    }

    public void handleButtonNove(View view) {
        if (visorPrincipal.getText().toString().length() <= 12){
            if (microfone_ligado){
                texto_falado.speak("9", TextToSpeech.QUEUE_FLUSH,null);
            }

        }

        setCaracter("9");
    }

    public void handleButtonZero(View view) {
        if (visorPrincipal.getText().toString().length() <= 12){
            if (microfone_ligado){
                texto_falado.speak("0", TextToSpeech.QUEUE_FLUSH,null);
            }

        }
        setCaracter("0");
    }

    public void handleButtonSoma(View view)throws ParseException {
        if (microfone_ligado){
            texto_falado.speak("Mais", TextToSpeech.QUEUE_FLUSH,null);
        }

        setOperacao(Operacao.ADICAO);
        contagem=0;
    }

    public void handleButtonSubtrai(View view) throws ParseException{
        if (microfone_ligado){
            texto_falado.speak("Menos", TextToSpeech.QUEUE_FLUSH,null);
        }

        setOperacao(Operacao.SUBTRACAO);
        contagem=0;
    }

    public void handleButtonMultiplica(View view)throws ParseException {

        if (microfone_ligado){
            texto_falado.speak("Vezes", TextToSpeech.QUEUE_FLUSH,null);
        }
        setOperacao(Operacao.MULTIPLICACAO);
        contagem=0;
    }

    public void handleButtonDivide(View view) throws ParseException{

        if (microfone_ligado){
            texto_falado.speak("Divisão", TextToSpeech.QUEUE_FLUSH,null);
        }
        setOperacao(Operacao.DIVISAO);
        contagem=0;
    }

    public void handleButtonPorcentagem(View view) throws ParseException{
        if (microfone_ligado){
            texto_falado.speak("Porcentagem", TextToSpeech.QUEUE_FLUSH,null);
        }

        setOperacao(Operacao.PORCENTAGEM);
        contagem=0;
    }

    public void handleButtonVirgula(View view) {

        if (contagem==0){
            if (visorPrincipal.getText().toString().length() <= 12){
                if (microfone_ligado){
                    texto_falado.speak("Virgula", TextToSpeech.QUEUE_FLUSH,null);
                }

            }
            setCaracter(",");
        }
        contagem=1;
    }

    public void handleButtonConfig(View view) {
      ///  if (microfone_ligado){
    Conexao();

            if (carro_recebe.toString().equals("")){
                carro.setMICROFONE("s");
                String resultado =  BDmycar.Inserecarro(carro);
                img_btn.setImageResource(R.drawable.ic_baseline_mic_24);
                microfone_ligado = true;
                Toast.makeText(this, "Modo de áudio Ligado, caso queira ativar o calculo por vóz precione o botão do microfone", Toast.LENGTH_SHORT).show();
            }else {
                try {
                    if (carro_recebe.toString().equals("s")) {
                        carro.setMICROFONE("n");
                        img_btn.setImageResource(R.drawable.ic_baseline_mic_off_24);
                        microfone_ligado = false;
                        Toast.makeText(this, "Modo de áudio Desligado, caso queira ativar o calculo por vóz precione o botão do microfone", Toast.LENGTH_SHORT).show();
                    } else {
                        carro.setMICROFONE("s");
                        img_btn.setImageResource(R.drawable.ic_baseline_mic_24);
                        microfone_ligado = true;
                        Toast.makeText(this, "Modo de áudio Ligado, caso queira ativar o calculo por vóz precione o botão do microfone", Toast.LENGTH_SHORT).show();
                    }
                    String resultado = BDmycar.Updatecarro(carro);
                } catch (Exception s) {

              }
            }

        String fala = "Modo de áudio Ligado, caso queira ativar o cálculo por vóz precione o botão do microfone";
        if (microfone_ligado){
            Toast.makeText(this, "Modo de áudio Ligado,  caso queira ativar o calculo por vóz precione o botão do microfone", Toast.LENGTH_SHORT).show();
            texto_falado.speak(fala, TextToSpeech.QUEUE_FLUSH,null);
        }

    }

    public void handleButtonResultado(View view) {
        Conexao();
        calculadora.calcular();
        atualizarVisor();
        if (microfone_ligado){
            texto_falado.speak("Igual a " + calculadora.getValorTextoPrincipal(), TextToSpeech.QUEUE_FLUSH,null);
        }
        contagem=0;
    }

    public void handleButtonLimpar(View view) {
        if (microfone_ligado){
            texto_falado.speak("Tela Limpa", TextToSpeech.QUEUE_FLUSH,null);
        }

        calculadora = new Calculadora();
        atualizarVisor();
        contagem=0;
    }

    public void handleButtonDesfazer(View view) {
        try {
            if (visorPrincipal.getText().toString().length()>=1 &&  !visorPrincipal.getText().toString().equals("0")){
                if (microfone_ligado){
                    texto_falado.speak("Desfazer", TextToSpeech.QUEUE_FLUSH,null);
                }

            }
            calculadora.removerUltimoCaracter();
            atualizarVisor();
            contagem=0;
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(getBaseContext(), "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onPause() {
        if (texto_falado != null){
            texto_falado.stop();
            texto_falado.shutdown();
        }
        super.onPause();
    }

}