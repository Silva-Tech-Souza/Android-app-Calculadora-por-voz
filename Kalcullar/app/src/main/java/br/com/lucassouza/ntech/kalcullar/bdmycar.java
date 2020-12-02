package br.com.lucassouza.ntech.kalcullar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import br.com.lucassouza.ntech.kalcullar.entidades.Carro;


public class bdmycar extends SQLiteOpenHelper {
    public bdmycar(Context context) {
        super(context, "DADOS", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql_carro = "CREATE TABLE BDMICROFONE( CODIGO INTEGER PRIMARY KEY AUTOINCREMENT, MICROFONE TEXT);";

        db.execSQL(sql_carro);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String sql_carro = "DROP TABLE IF EXISTS BDMICROFONE;";

        db.execSQL(sql_carro);
        onCreate(db);
    }

    public String Inserecarro(Carro carro){
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues dados_carros = new ContentValues();

            dados_carros.put("MICROFONE", carro.getMICROFONE());


            db.insertOrThrow("BDMICROFONE", null, dados_carros);
            db.close();
        }catch (SQLException K){
            return "erro == > " + K;
        }

        return "Sucesso";
    }
    public String Updatecarro(Carro carro){
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues dados_carros = new ContentValues();

            dados_carros.put("MICROFONE", carro.getMICROFONE());


            db.update("BDMICROFONE", dados_carros, null, null);
            db.close();
        }catch (SQLException K){
            return "erro == > " + K;
        }

        return "Atualizado";
    }

    public String Buscarcarro(){
        try{
            Carro carro = new Carro();
            String sql_busca_carro = "SELECT CITYGASOLINA, CITYETANOL FROM BDMICROFONE;";
            SQLiteDatabase db = getReadableDatabase();
            Cursor c = db.rawQuery(sql_busca_carro, null);
            c.moveToFirst();

            do{
                if (c.getCount() > 0){
                    carro.setMICROFONE(c.getString(c.getColumnIndexOrThrow("MICROFONE")));
                    db.close();
                    c.close();
                    return  ""+ carro.getMICROFONE().toString()+ " " + carro.getMICROFONE().toString()+ "";
                }
            }
            while(c.moveToNext());

            db.close();
            c.close();
        }catch (SQLException k){
            return ""+k;
        }
       return "";
    }
}
