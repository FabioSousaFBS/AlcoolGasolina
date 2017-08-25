package alcoolgasolina.projetos.com.alcoolougasolina.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.*;
import alcoolgasolina.projetos.com.alcoolougasolina.R;
import alcoolgasolina.projetos.com.alcoolougasolina.helper.ConfiguracaoBanco;
import alcoolgasolina.projetos.com.alcoolougasolina.helper.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private EditText edtPrecoAlcool;
    private EditText edtPrecoGasolina;
    private TextView tvResultado;

    private DatabaseHelper helper;

    private Toolbar toolbar;

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
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT descricao FROM Carros WHERE descricao = 'Veiculo Padrão'", null);
        cursor.moveToFirst();
        toolbar.setTitle(cursor.getString(0));
        cursor.close();
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
                Toast.makeText(this, "Adicionar novo Veiculo", Toast.LENGTH_SHORT).show();
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
}
