package com.example.developmentvmachine.serviciosweb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.developmentvmachine.serviciosweb.utils.Constants;
import com.example.developmentvmachine.serviciosweb.utils.HttpConnection;
import com.example.developmentvmachine.serviciosweb.utils.HttpGet;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button consultar;
    Button constarId;
    Button insertar;

    Button actualizar;
    Button borrar;
    EditText identificador;
    EditText nombre;
    EditText description;
    EditText pais;
    EditText tipo;
    EditText familia;
    EditText alc;
    TextView resultado;

    HttpConnection hiloConexion;
    HttpGet httpGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        consultar = (Button)findViewById(R.id.consultar);
        constarId = (Button)findViewById(R.id.consultarid);
        insertar = (Button)findViewById(R.id.insertar);
        actualizar = (Button)findViewById(R.id.actualizar);
        borrar = (Button)findViewById(R.id.borrar);
        identificador = (EditText) findViewById(R.id.etId);
        nombre = (EditText)findViewById(R.id.etNombre);
        description = (EditText)findViewById(R.id.etDescription);
        pais = (EditText) findViewById(R.id.etCountry);
        familia = (EditText)findViewById(R.id.etFamily);
        tipo = (EditText)findViewById(R.id.etType);
        alc = (EditText)findViewById(R.id.etAlc);
        resultado = (TextView) findViewById(R.id.tvResultado);
        resultado.setMovementMethod(new ScrollingMovementMethod());

        consultar.setOnClickListener(this);
        constarId.setOnClickListener(this);
        insertar.setOnClickListener(this);
        actualizar.setOnClickListener(this);
        borrar.setOnClickListener(this);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, R.string.floating_button_message, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.consultar:
                hiloConexion = new HttpConnection(this);
                hiloConexion.execute(Constants.GET_ALL);

                break;
            case R.id.consultarid:
                if(checkIdValue(identificador.getText().toString())) {
                    hiloConexion = new HttpConnection(this);
                    hiloConexion.execute(Constants.GET_BY_ID, identificador.getText().toString());
                }else{
                    Toast toast = Toast.makeText(this, "Introduce ID para consultar", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case R.id.insertar:
                if (checkValuesForInserAndUpdate(identificador.getText().toString(), nombre.getText().toString())){
                    hiloConexion = new HttpConnection(this);
                    hiloConexion.execute(Constants.INSERT,
                            nombre.getText().toString(),
                            description.getText().toString(),
                            pais.getText().toString(),
                            tipo.getText().toString(),
                            familia.getText().toString(),
                            alc.getText().toString()
                    );
                }else{
                    Toast toast = Toast.makeText(this, "ID y Nombre no pueden ser nulos!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case R.id.actualizar:
                if (checkValuesForInserAndUpdate(identificador.getText().toString(), nombre.getText().toString())){
                    hiloConexion = new HttpConnection(this);
                    hiloConexion.execute(Constants.UPDATE,
                            identificador.getText().toString(),
                            nombre.getText().toString(),
                            description.getText().toString(),
                            pais.getText().toString(),
                            tipo.getText().toString(),
                            familia.getText().toString(),
                            alc.getText().toString()
                    );
                }else {
                    Toast toast = Toast.makeText(this, "ID y Nombre no pueden ser nulos!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case R.id.borrar:
                if (checkIdValue(identificador.getText().toString())) {
                    hiloConexion = new HttpConnection(this);
                    hiloConexion.execute(Constants.DELETE, identificador.getText().toString());
                }else{
                    Toast toast = Toast.makeText(this, "Introduce ID del registro a eliminar", Toast.LENGTH_SHORT);
                    toast.show();
                }

                break;
            default:

                break;
        }
    }

    private boolean checkIdValue(String st){
        if (TextUtils.isEmpty(st)){
            return false;
        }
        Integer id = Integer.parseInt(st);
        return id > 0;
    }

    private boolean checkValuesForInserAndUpdate(String id, String name){
        return !TextUtils.isEmpty(id) && !TextUtils.isEmpty(name);
    }

}
