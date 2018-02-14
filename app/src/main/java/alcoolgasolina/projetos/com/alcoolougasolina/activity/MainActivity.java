package alcoolgasolina.projetos.com.alcoolougasolina.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.renderscript.Double2;
import android.speech.tts.TextToSpeech;
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
import alcoolgasolina.projetos.com.alcoolougasolina.helper.InterpretaTexto;
import alcoolgasolina.projetos.com.alcoolougasolina.helper.Preferencias;

//recursos de reconhecimento de voz
import android.speech.RecognizerIntent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private EditText edtPrecoAlcool;
    private EditText edtPrecoGasolina;
    private TextView tvResultado;
    private ImageView imgBtnVoz;
    private Button btnCalcular;

    private DatabaseHelper helper;

    private Toolbar toolbar;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.CAPTURE_AUDIO_OUTPUT
    };

    //recurso de comando de voz
    private static final int REQUEST_CODE = 1234;
    private SensorManager sManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtPrecoAlcool = (EditText) findViewById(R.id.edtPrecoAlcool);
        edtPrecoGasolina = (EditText) findViewById(R.id.edtPrecoGasolina);
        tvResultado = (TextView) findViewById(R.id.txtResultado);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imgBtnVoz = (ImageView) findViewById(R.id.imgComandoVoz_main);
        btnCalcular = (Button) findViewById(R.id.btnCalcular);

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

        imgBtnVoz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executaComandoVoz();
            }
        });

        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FazerCalculoCombustivel();
            }
        });

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

    public void FazerCalculoCombustivel(){

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


    @Override
    public void onInit(int status) {

    }

    private void executaComandoVoz(){

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Informe os preços dos combustíveis. (Fale o nome do combustível e logo em seguida o preço)");
        startActivityForResult(intent, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
           ArrayList<String> matches = data.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS);
            //resultList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, matches));
            InterpretaTexto texto = new InterpretaTexto(matches.get(0).toString());

            edtPrecoAlcool.setText(texto.RetornaValor("etanol"));
            edtPrecoGasolina.setText(texto.RetornaValor("gasolina"));

            if (edtPrecoGasolina.getText().toString() != "" && edtPrecoAlcool.getText().toString() != ""){
                FazerCalculoCombustivel();
            }else{
                Toast.makeText(this, "Não foi possível identificar o que foi dito. Repita o processo falando pausadamente.", Toast.LENGTH_SHORT).show();
            }



        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
