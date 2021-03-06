package com.example.developmentvmachine.serviciosweb.impl;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.developmentvmachine.serviciosweb.R;
import com.example.developmentvmachine.serviciosweb.model.Cerveza;
import com.example.developmentvmachine.serviciosweb.model.WSCervezasResponse;
import com.example.developmentvmachine.serviciosweb.services.CallBackService;
import com.example.developmentvmachine.serviciosweb.services.CervezasService;
import com.example.developmentvmachine.serviciosweb.utils.Constants;
import com.example.developmentvmachine.serviciosweb.utils.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Development VMachine on 26/06/2017.
 */

public class CervezasServiceImpl implements CervezasService {

    public static final String TAG = CervezasServiceImpl.class.getSimpleName();

    private Activity context;

    EditText id = null;
    EditText nombre = null;
    EditText description = null;
    EditText pais = null;
    EditText familia = null;
    EditText tipo = null;
    EditText alc = null;

    public CervezasServiceImpl(Activity context) {
        this.context = context;

        id = (EditText) context.findViewById(R.id.etId);
        nombre = (EditText) context.findViewById(R.id.etNombre);
        description = (EditText) context.findViewById(R.id.etDescription);
        pais = (EditText) context.findViewById(R.id.etCountry);
        familia = (EditText) context.findViewById(R.id.etFamily);
        tipo = (EditText) context.findViewById(R.id.etType);
        alc = (EditText) context.findViewById(R.id.etAlc);
    }

    @Override
    public void getAll() {
        HttpClient httpClient = new HttpClient(new CallBackService() {
            @Override
            public void callback(String response) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    WSCervezasResponse wsResponse = mapper.readValue(response, WSCervezasResponse.class);

                    String toastMsg = null;

                    if(Constants.STATUS_OK.equals(wsResponse.getStatus())){
                        toastMsg = context.getString(R.string.show_full_beer_list);
                    }else if(Constants.STATUS_NOK.equals(wsResponse.getStatus())) {
                        toastMsg = context.getString(R.string.connection_error);
                    }
                    Toast toast = Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT);
                    toast.show();

                    TextView txtView = (TextView) context.findViewById(R.id.tvResultado);
                    txtView.setText(printCervezasTextView(wsResponse.getCervezas()));

                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        },
        null,
        Constants.HTTP_GET
        );
        httpClient.execute(Constants.GET_ALL);
    }

    @Override
    public void getById(int id) {
        HttpClient httpClient = new HttpClient(new CallBackService() {
                @Override
                public void callback(String response) {
                    WSCervezasResponse wsResponse = null;
                    ObjectMapper mapper = new ObjectMapper();
                    String toastMsg = "";
                    try{
                        wsResponse = mapper.readValue(response, WSCervezasResponse.class);

                        if(Constants.STATUS_OK.equals(wsResponse.getStatus())){
                            List<Cerveza> cervezas = wsResponse.getCervezas();

                            if (cervezas != null && cervezas.size() == 1){
                                toastMsg = context.getString(R.string.beer_found);
                                fillBeerFields(wsResponse.getCervezas().get(0));
                                ImageView imageView = (ImageView) context.findViewById(R.id.ivImagen);
                                String imageUrl = Constants.BASE_URL + "/" + wsResponse.getCervezas().get(0).getImagePath();
                                Glide.with(context)
                                        .load(imageUrl)
                                        .crossFade()
                                        .placeholder(android.R.drawable.ic_menu_report_image)
                                        .into(imageView);
                            }else{
                                toastMsg = String.format(context.getString(R.string.more_than_one_id), cervezas.get(0).getId());
                            }
                        } else if(Constants.STATUS_NOK.equals(wsResponse.getStatus())) {
                            toastMsg = wsResponse.getMensaje();
                            Log.i(TAG, wsResponse.getMensaje());
                        }
                        Toast toast = Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT);
                        toast.show();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                    Log.i(TAG, "GetAll - end");
                }
            },
            null,
            Constants.HTTP_GET);
        httpClient.execute(Constants.GET_BY_ID+id);
    }

    @Override
    public void updateCerveza(HashMap<String, String> fieldsMap) {
        Log.i(TAG, "Update - Ini");
        HttpClient client = new HttpClient(new CallBackService() {
                @Override
                public void callback(String response) {
                    Log.i(TAG, "Update Callback - ini");
                    Log.i(TAG, "Update Callback Response: " + response);
                    WSCervezasResponse wsResponse = null;
                    ObjectMapper mapper = new ObjectMapper();
                    String toastMsg = "";

                    try {
                        wsResponse = mapper.readValue(response, WSCervezasResponse.class);

                        if(Constants.STATUS_OK.equals(wsResponse.getStatus())){
                            toastMsg = context.getString(R.string.update_success);
                        }else if(Constants.STATUS_NOK.equals(wsResponse.getStatus())){
                            toastMsg = context.getString(R.string.error_updating_beer) + wsResponse.getMensaje();
                            Log.i(TAG, "Update Callback error message: " + wsResponse.getMensaje());
                        }
                        Toast toast = Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT);
                        toast.show();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                    Log.i(TAG, "Update Callback - end");
                }
            },
            fieldsMap,
            Constants.HTTP_POST);
        client.execute(Constants.UPDATE);
        clearFields();
        Log.i(TAG, "Update - end");
    }

    @Override
    public void deleteCerveza(int id) {
        Log.i(TAG, "Delete - Ini");
        HashMap<String, String> idMap = new HashMap<>();
        idMap.put("id", String.valueOf(id));
        HttpClient client = new HttpClient(new CallBackService() {
            @Override
            public void callback(String response) {
                Log.i(TAG, "Delete Callback - ini");
                Log.i(TAG, "Delete Callback Response: " + response);
                WSCervezasResponse wsResponse = null;
                ObjectMapper mapper = new ObjectMapper();
                String toastMsg = "";

                try {
                    wsResponse = mapper.readValue(response, WSCervezasResponse.class);

                    if(Constants.STATUS_OK.equals(wsResponse.getStatus())){
                        toastMsg = context.getString(R.string.delete_success);
                    }else if(Constants.STATUS_NOK.equals(wsResponse.getStatus())){
                        toastMsg = context.getString(R.string.error_delete) + wsResponse.getMensaje();
                        Log.i(TAG, "Delete Callback error message: " + wsResponse.getMensaje());
                    }
                    Toast toast = Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT);
                    toast.show();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                Log.i(TAG, "Delete Callback - end");
            }
        },
                idMap,
                Constants.HTTP_POST);
        client.execute(Constants.DELETE);
        clearFields();
        Log.i(TAG, "Delete - end");
    }

    @Override
    public void insertCerveza(HashMap<String, String> fieldsMap) {
        Log.i(TAG, "Insert - Ini");
        HttpClient client = new HttpClient(new CallBackService() {
                @Override
                public void callback(String response) {
                    Log.i(TAG, "Insert Callback - ini");
                    Log.i(TAG, "Insert Callback Response: " + response);
                    WSCervezasResponse wsResponse = null;
                    ObjectMapper mapper = new ObjectMapper();
                    String toastMsg = "";

                    try {
                        wsResponse = mapper.readValue(response, WSCervezasResponse.class);

                        if(Constants.STATUS_OK.equals(wsResponse.getStatus())){
                            toastMsg = context.getString(R.string.insert_success);
                        }else if(Constants.STATUS_NOK.equals(wsResponse.getStatus())){
                            toastMsg = context.getString(R.string.error_inserting_beer) + wsResponse.getMensaje();
                            Log.i(TAG, "Insert Callback error message: " + wsResponse.getMensaje());
                        }
                        Toast toast = Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT);
                        toast.show();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                    Log.i(TAG, "Insert Callback - end");
                }
            },
            fieldsMap,
            Constants.HTTP_POST);
        client.execute(Constants.INSERT);
        clearFields();
        Log.i(TAG, "Insert - end");
    }

    private String printCervezasTextView(List<Cerveza> cervezas) {
        String output = "";
        for (Cerveza cerveza : cervezas) {
            output += "ID: " + cerveza.getId() + "\n" +
                    "Nombre: " + cerveza.getName() + "\n" +
                    "Description: " +cerveza.getDescription() + "\n" +
                    "Familia: " + cerveza.getFamily() + "\n" +
                    "Pais: " + cerveza.getCountry() + "\n" +
                    "Alcohol: " + cerveza.getAlc() + "%" + "\n";
        }
        return output;
    }

    private void fillBeerFields(Cerveza beer){
        id.setText(String.valueOf(beer.getId()));
        nombre.setText(beer.getName());
        description.setText(beer.getDescription());
        pais.setText(beer.getCountry());
        familia.setText(beer.getFamily());
        tipo.setText(beer.getType());
        alc.setText(String.valueOf(beer.getAlc()));
    }

    private void clearFields() {
        id.getText().clear();
        nombre.getText().clear();
        description.getText().clear();
        pais.getText().clear();
        familia.getText().clear();
        tipo.getText().clear();
        alc.getText().clear();
    }
}
