package alcoolgasolina.projetos.com.alcoolougasolina.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import alcoolgasolina.projetos.com.alcoolougasolina.R;
import alcoolgasolina.projetos.com.alcoolougasolina.adapter.VeiculosAdapter;
import alcoolgasolina.projetos.com.alcoolougasolina.helper.ConfiguracaoBanco;
import alcoolgasolina.projetos.com.alcoolougasolina.helper.DatabaseHelper;
import alcoolgasolina.projetos.com.alcoolougasolina.model.Carros;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.*;

import java.util.ArrayList;

public class VeiculosActivity extends AppCompatActivity {

    private ListView listView;
    private Toolbar toolbar;

    private ArrayList<Carros> carros;
    private ArrayAdapter<Carros> adapter;

    private SQLiteDatabase banco;
    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veiculos);

        toolbar = (Toolbar) findViewById(R.id.toolbarVeiculos);
        listView = (ListView) findViewById(R.id.lsvVeiculos);

        toolbar.setTitle("Lista de Veiculos");
        toolbar.setNavigationIcon(R.drawable.ic_voltar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        recuperaVeiculos();

    }

    private void recuperaVeiculos(){
        try{

            carros = new ArrayList<>();
            adapter = new VeiculosAdapter(VeiculosActivity.this, carros);
            listView.setAdapter(adapter);

            carros.clear();

            //INSTANCIA DO BANCO
            helper = new DatabaseHelper(this);

            //BANCO DE DADOS
            banco = helper.getReadableDatabase();

            Cursor cursor = banco.rawQuery("SELECT * FROM Carros", null);
            cursor.moveToFirst();

            for(int i = 0; i < cursor.getCount(); i++){

                int indexID = cursor.getColumnIndex("idCarro");
                int indexDS = cursor.getColumnIndex("descricao");
                int indexETANOL = cursor.getColumnIndex("cmEtanol");
                int indexGASOLINA = cursor.getColumnIndex("cmGasolina");
                int indexFATOR = cursor.getColumnIndex("fator");

                Carros carro = new Carros(
                        cursor.getInt(indexID),
                        cursor.getString(indexDS),
                        cursor.getDouble(indexETANOL),
                        cursor.getDouble(indexGASOLINA),
                        cursor.getDouble(indexFATOR)
                );

                carros.add(carro);

                cursor.moveToNext();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.veiculos_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.item_adicionar_veiculo:
                Intent intent = new Intent(VeiculosActivity.this, CadastroVeiculoActivity.class);
                startActivity(intent);

                return true;
            case R.id.item_sair:
                finish();
                return true;

            case android.R.id.home:
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

    @Override
    protected void onStop() {
        helper.close();
        super.onStop();
    }

    @Override
    protected void onRestart() {

        recuperaVeiculos();

        super.onRestart();
    }
}
