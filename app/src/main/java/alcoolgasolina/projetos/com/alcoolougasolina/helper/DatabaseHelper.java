package alcoolgasolina.projetos.com.alcoolougasolina.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String BANCO_DADOS = "AlcoolGasolina";
    private static final int VERSAO = 1;

    public DatabaseHelper(Context context) {
        super(context, BANCO_DADOS, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Carros(" +
                "    idCarro INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    descricao VARCHAR(30)," +
                "    cmEtanol NUMERIC(10,2)," +
                "    cmGasolina NUMERIC(10,2)," +
                "    fator NUMERIC(10,2));");

        //INSERINDO O VEICULO PADRÃO APÓS CRIAR A TABELA CARROS
       db.execSQL(" INSERT INTO  Carros(descricao, cmEtanol, cmGasolina, fator) " +
               "    SELECT temp.descricao, temp.cmEtanol, temp.cmGasolina, temp.fator FROM (SELECT 'Veiculo Padrão' AS descricao, 0.0 AS cmEtanol, 0.0 AS cmGasolina, 0.70 AS fator) AS temp " +
               " WHERE 0 = (SELECT COUNT(*) FROM Carros);");

        db.execSQL("CREATE TABLE IF NOT EXISTS Historico(" +
                "    idHistorico INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    data DATETIME," +
                "    vlAlcool NUMERIC(10,2)," +
                "    vlGasolina NUMERIC(10,2)," +
                "    idCarro_fk INTEGER," +
                "    melhorOP VARCHAR(10)," +
                "    fator NUMERIC(10,2)," +
                "    FOREIGN KEY (idCarro_fk) REFERENCES Carros(idCarro));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
