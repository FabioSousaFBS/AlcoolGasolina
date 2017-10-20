package alcoolgasolina.projetos.com.alcoolougasolina.activity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.renderscript.Double2;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import alcoolgasolina.projetos.com.alcoolougasolina.R;
import alcoolgasolina.projetos.com.alcoolougasolina.helper.ConfiguracaoBanco;
import alcoolgasolina.projetos.com.alcoolougasolina.helper.DatabaseHelper;
import alcoolgasolina.projetos.com.alcoolougasolina.helper.Preferencias;

public class MainActivity extends AppCompatActivity {

    private EditText edtPrecoAlcool;
    private EditText edtPrecoGasolina;
    private TextView tvResultado;

    private DatabaseHelper helper;

    private Toolbar toolbar;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.CAPTURE_AUDIO_OUTPUT
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtPrecoAlcool = (EditText) findViewById(R.id.edtPrecoAlcool);
        edtPrecoGasolina = (EditText) findViewById(R.id.edtPrecoGasolina);
        tvResultado = (TextView) findViewById(R.id.txtResultado);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //INSTANCIA DO BANCO
        helper = new DatabaseHelper(this);

        //CONFIGURA A TOOLBAR
        if (VerificaVeiculoPadrao() == false ){
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT descricao, fator FROM Carros WHERE descricao = 'Veiculo Padrão'", null);
            cursor.moveToFirst();
            toolbar.setTitle(cursor.getString(0));

            //ATUALIZA AS PREFERENCIAS
            Preferencias preferencias = new Preferencias(MainActivity.this);
            preferencias.SalvarDados(cursor.getString(0), String.valueOf(cursor.getDouble(1)));

            cursor.close();
        }else{
            Preferencias preferencias = new Preferencias(MainActivity.this);

            toolbar.setTitle(preferencias.getVeiculoPadrao());
        }

        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.item_adicionar_veiculo:
                Intent intent = new Intent(MainActivity.this, VeiculosActivity.class);
                startActivity(intent);

                return true;
            case R.id.item_sair:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }


    private boolean VerificaVeiculoPadrao(){
        String strVeiculo;
        Preferencias preferencias = new Preferencias(MainActivity.this);
        strVeiculo = preferencias.getVeiculoPadrao();

        if (strVeiculo == null){
            return false;
        }

        return true;
    }

    public void FazerCalculoCombustivel(View view){

        double dblResultado;
        double dblPrecoEtanol;
        double dblPrecoGasolina;
        double dblFator;

        Preferencias preferencias = new Preferencias(MainActivity.this);

        dblFator = Double.parseDouble(preferencias.getFatorVeiculoPadrao());
        dblPrecoEtanol = Double.parseDouble(edtPrecoAlcool.getText().toString());
        dblPrecoGasolina = Double.parseDouble(edtPrecoGasolina.getText().toString());

        dblResultado = dblPrecoEtanol / dblPrecoGasolina;

        if (dblResultado < dblFator){
            tvResultado.setText("Abasteça com Etanol!");
        }else{
            tvResultado.setText("Abasteça com Gasolina!");
        }
    }


}
