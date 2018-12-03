package com.ap.atm.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ap.atm.R;
import com.ap.atm.utils.ApiUtils;
import com.ap.atm.utils.DialogUtils;
import com.ap.atm.utils.SessionUtils;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import me.alexrs.prefs.lib.Prefs;

import static com.ap.atm.utils.Constants.REQUEST_IMAGE_CAPTURE;
import static com.ap.atm.utils.Constants.REQUEST_IMAGE_GALERY;

public class SemaphoreActivity extends AppCompatActivity {
    private View mPicRequest;
    private String mCurrentPhotoPath;
    private Context mContext;

    //General Views
    private TextInputLayout mStreet1;
    private TextInputLayout mStreet2;
    private TextInputLayout mBarrio;
    private TextInputLayout mParroquia;
    private Spinner mInterseccion;

    //Ubicación Views
    private Spinner mSpinMarca;
    private Spinner mSpinGrupo;
    private Spinner mSpinRegulador;
    private Spinner mSpinFuente;
    private Spinner mSpinUps;
    private Spinner mSpinArmario;

    //Elementos Views
    private TextInputLayout mElemento;

    //Comentarios Views
    private TextInputLayout mInventario;
    private TextInputLayout mComentario;
    private TextInputLayout mEquipo;

    private Button mSaveButton;
    //Falta el enlace de intersecciones
    private List<String> mListMarcas;
    private List<String> mListGrupos;
    private List<String> mListReguladores;
    private List<String> mListFuentes;
    private List<String> mListUps;
    private List<String> mListArmarios;


    private ArrayAdapter<String> mMarcasAdapter;
    private ArrayAdapter<String> mGruposAdapter;
    private ArrayAdapter<String> mReguladoresAdapter;
    private ArrayAdapter<String> mFuentesAdapter;
    private ArrayAdapter<String> mUpsAdapter;
    private ArrayAdapter<String> mArmariosAdapter;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semaphore);
        getSupportActionBar().setTitle(getString(R.string.semaforo));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;
        initViews();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            Toast.makeText(mContext, mCurrentPhotoPath, Toast.LENGTH_LONG).show();
        }else if(requestCode == REQUEST_IMAGE_GALERY && resultCode == Activity.RESULT_OK){
            Uri selectedImage = intent.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mCurrentPhotoPath = cursor.getString(columnIndex);
            cursor.close();
            Toast.makeText(mContext, mCurrentPhotoPath, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(mContext, "No se recibieron datos", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initViews(){
        mPicRequest = findViewById(R.id.mSemPicReq);



        //General Views
        mStreet1 = findViewById(R.id.mSemaphoreInpStreet1);
        mStreet2 = findViewById(R.id.mSemaphoreInpStreet2);
        mBarrio = findViewById(R.id.mSemaphoreInpBarrio);
        mParroquia = findViewById(R.id.mSemaphoreInpParroquia);
        mInterseccion = findViewById(R.id.mSemaphoreSpnInterseccion);

        //Ubicación Views
        mSpinMarca = findViewById(R.id.mSemaphoreSpnMarca);
        mSpinGrupo = findViewById(R.id.mSemaphoreSpnGrupo);
        mSpinRegulador = findViewById(R.id.mSemaphoreSpnRegulador);
        mSpinFuente = findViewById(R.id.mSemaphoreSpnFuente);
        mSpinUps = findViewById(R.id.mSemaphoreSpnUps);
        mSpinArmario = findViewById(R.id.mSemaphoreSpnArmario);

        //Elementos Views
        mElemento = findViewById(R.id.mSemaphoreInpElementos);

        //Comentarios Views
        mInventario = findViewById(R.id.mSemaphoreInpInventario);
        mComentario = findViewById(R.id.mSemaphoreInpComentario);
        mEquipo = findViewById(R.id.mSemaphoreInpEquipment);

        mSaveButton = findViewById(R.id.mSemaphoreBtnSave);

        initActivity();
    }
    private void initActivity(){
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.marcas.name(), "").isEmpty()){
            showMarcas();
        }
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.grupos.name(), "").isEmpty()){
            showGrupos();
        }
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.reguladores.name(), "").isEmpty()){
            showReguladores();
        }
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.fuentes.name(), "").isEmpty()){
            showFuentes();
        }
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.ups.name(), "").isEmpty()){
            showUps();
        }
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.armarios.name(), "").isEmpty()){
            showArmarios();
        }
        getMarcas();
        getGrupos();
        getReguladores();
        getFuentes();
        getUps();
        getArmarios();
        mPicRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateForm();
            }
        });
    }

    private void checkPermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted()){
                    dialogGetImage();
                }else{
                    Toast.makeText(mContext, getString(R.string.error_permissions),
                            Toast.LENGTH_LONG).show();
                }
            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
    }

    private void dialogGetImage(){
        final MaterialDialog mDialog = DialogUtils.showListDialog(getString(R.string.label_photo_select), R.array.photo_select, mContext);
        mDialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        dispatchTakePictureIntent();
                        break;
                    case 1:
                        getImageGalery();
                        break;
                }
                mDialog.dismiss();
            }
        });
    }
    private void getImageGalery(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALERY);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName()+".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "ATM_PIC_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void getMarcas(){
        String mUrl = ApiUtils.API_URL;
        RequestParams mParams = new RequestParams();
        mParams.put(ApiUtils.parameters.action.name(), ApiUtils.actions.getCombo.name());
        mParams.put(ApiUtils.parameters.code.name(), ApiUtils.codes.marca.name());
        new AsyncHttpClient().get(mUrl, mParams, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    JSONObject mData = mJson.getJSONObject("0");
                    String mRecords = mData.getString(ApiUtils.responses.records.name()).replace("\\", "");
                    JSONObject mJsonRecords = new JSONObject(mRecords);
                    int count = 1;
                    boolean found = true;
                    List<String> mListAux = new ArrayList<>();
                    do{
                        if(mJsonRecords.has(String.valueOf(count))){
                            mListAux.add(mJsonRecords.getString(String.valueOf(count)));
                            count++;
                        }else{
                            found = false;
                        }
                    }while (found);
                    if(mListAux.size() > 0 && !Prefs.with(mContext).getString(SessionUtils.prefs.marcas.name(), "").contentEquals(new Gson().toJson(mListAux))){
                        Prefs.with(mContext).save(SessionUtils.prefs.marcas.name(), new Gson().toJson(mListAux));
                        showMarcas();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {

            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void showMarcas(){
        mListMarcas = SessionUtils.getListStrings(mContext, SessionUtils.prefs.marcas.name());
        mListMarcas.add(0, "Marca");
        mMarcasAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListMarcas);
        mSpinMarca.setAdapter(mMarcasAdapter);
    }

    private void getGrupos(){
        String mUrl = ApiUtils.API_URL;
        RequestParams mParams = new RequestParams();
        mParams.put(ApiUtils.parameters.action.name(), ApiUtils.actions.getCombo.name());
        mParams.put(ApiUtils.parameters.code.name(), ApiUtils.codes.grupo.name());
        new AsyncHttpClient().get(mUrl, mParams, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    JSONObject mData = mJson.getJSONObject("0");
                    String mRecords = mData.getString(ApiUtils.responses.records.name()).replace("\\", "");
                    JSONObject mJsonRecords = new JSONObject(mRecords);
                    int count = 1;
                    boolean found = true;
                    List<String> mListAux = new ArrayList<>();
                    do{
                        if(mJsonRecords.has(String.valueOf(count))){
                            mListAux.add(mJsonRecords.getString(String.valueOf(count)));
                            count++;
                        }else{
                            found = false;
                        }
                    }while (found);
                    if(mListAux.size() > 0 && !Prefs.with(mContext).getString(SessionUtils.prefs.grupos.name(), "").contentEquals(new Gson().toJson(mListAux))){
                        Prefs.with(mContext).save(SessionUtils.prefs.grupos.name(), new Gson().toJson(mListAux));
                        showGrupos();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {

            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void showGrupos(){
        mListGrupos = SessionUtils.getListStrings(mContext, SessionUtils.prefs.grupos.name());
        mListGrupos.add(0, "Grupo");
        mGruposAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListGrupos);
        mSpinGrupo.setAdapter(mGruposAdapter);
    }

    private void getReguladores(){
        String mUrl = ApiUtils.API_URL;
        RequestParams mParams = new RequestParams();
        mParams.put(ApiUtils.parameters.action.name(), ApiUtils.actions.getCombo.name());
        mParams.put(ApiUtils.parameters.code.name(), ApiUtils.codes.regulador.name());
        new AsyncHttpClient().get(mUrl, mParams, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    JSONObject mData = mJson.getJSONObject("0");
                    String mRecords = mData.getString(ApiUtils.responses.records.name()).replace("\\", "");
                    JSONObject mJsonRecords = new JSONObject(mRecords);
                    int count = 1;
                    boolean found = true;
                    List<String> mListAux = new ArrayList<>();
                    do{
                        if(mJsonRecords.has(String.valueOf(count))){
                            mListAux.add(mJsonRecords.getString(String.valueOf(count)));
                            count++;
                        }else{
                            found = false;
                        }
                    }while (found);
                    if(mListAux.size() > 0 && !Prefs.with(mContext).getString(SessionUtils.prefs.reguladores.name(), "").contentEquals(new Gson().toJson(mListAux))){
                        Prefs.with(mContext).save(SessionUtils.prefs.reguladores.name(), new Gson().toJson(mListAux));
                        showReguladores();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {

            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void showReguladores(){
        mListReguladores = SessionUtils.getListStrings(mContext, SessionUtils.prefs.reguladores.name());
        mListReguladores.add(0, "Regulador");
        mReguladoresAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListReguladores);
        mSpinRegulador.setAdapter(mReguladoresAdapter);
    }

    private void getFuentes(){
        String mUrl = ApiUtils.API_URL;
        RequestParams mParams = new RequestParams();
        mParams.put(ApiUtils.parameters.action.name(), ApiUtils.actions.getCombo.name());
        mParams.put(ApiUtils.parameters.code.name(), ApiUtils.codes.fuente.name());
        new AsyncHttpClient().get(mUrl, mParams, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    JSONObject mData = mJson.getJSONObject("0");
                    String mRecords = mData.getString(ApiUtils.responses.records.name()).replace("\\", "");
                    JSONObject mJsonRecords = new JSONObject(mRecords);
                    int count = 1;
                    boolean found = true;
                    List<String> mListAux = new ArrayList<>();
                    do{
                        if(mJsonRecords.has(String.valueOf(count))){
                            mListAux.add(mJsonRecords.getString(String.valueOf(count)));
                            count++;
                        }else{
                            found = false;
                        }
                    }while (found);
                    if(mListAux.size() > 0 && !Prefs.with(mContext).getString(SessionUtils.prefs.fuentes.name(), "").contentEquals(new Gson().toJson(mListAux))){
                        Prefs.with(mContext).save(SessionUtils.prefs.fuentes.name(), new Gson().toJson(mListAux));
                        showFuentes();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {

            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void showFuentes(){
        mListFuentes = SessionUtils.getListStrings(mContext, SessionUtils.prefs.fuentes.name());
        mListFuentes.add(0, "Fuente");
        mFuentesAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListFuentes);
        mSpinFuente.setAdapter(mFuentesAdapter);
    }

    private void getUps(){
        String mUrl = ApiUtils.API_URL;
        RequestParams mParams = new RequestParams();
        mParams.put(ApiUtils.parameters.action.name(), ApiUtils.actions.getCombo.name());
        mParams.put(ApiUtils.parameters.code.name(), ApiUtils.codes.ups.name());
        new AsyncHttpClient().get(mUrl, mParams, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    JSONObject mData = mJson.getJSONObject("0");
                    String mRecords = mData.getString(ApiUtils.responses.records.name()).replace("\\", "");
                    JSONObject mJsonRecords = new JSONObject(mRecords);
                    int count = 1;
                    boolean found = true;
                    List<String> mListAux = new ArrayList<>();
                    do{
                        if(mJsonRecords.has(String.valueOf(count))){
                            mListAux.add(mJsonRecords.getString(String.valueOf(count)));
                            count++;
                        }else{
                            found = false;
                        }
                    }while (found);
                    if(mListAux.size() > 0 && !Prefs.with(mContext).getString(SessionUtils.prefs.ups.name(), "").contentEquals(new Gson().toJson(mListAux))){
                        Prefs.with(mContext).save(SessionUtils.prefs.ups.name(), new Gson().toJson(mListAux));
                        showUps();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {

            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void showUps(){
        mListUps = SessionUtils.getListStrings(mContext, SessionUtils.prefs.ups.name());
        mListUps.add(0, "UPS");
        mUpsAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListUps);
        mSpinUps.setAdapter(mUpsAdapter);
    }

    private void getArmarios(){
        String mUrl = ApiUtils.API_URL;
        RequestParams mParams = new RequestParams();
        mParams.put(ApiUtils.parameters.action.name(), ApiUtils.actions.getCombo.name());
        mParams.put(ApiUtils.parameters.code.name(), ApiUtils.codes.armario.name());
        new AsyncHttpClient().get(mUrl, mParams, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    JSONObject mData = mJson.getJSONObject("0");
                    String mRecords = mData.getString(ApiUtils.responses.records.name()).replace("\\", "");
                    JSONObject mJsonRecords = new JSONObject(mRecords);
                    int count = 1;
                    boolean found = true;
                    List<String> mListAux = new ArrayList<>();
                    do{
                        if(mJsonRecords.has(String.valueOf(count))){
                            mListAux.add(mJsonRecords.getString(String.valueOf(count)));
                            count++;
                        }else{
                            found = false;
                        }
                    }while (found);
                    if(mListAux.size() > 0 && !Prefs.with(mContext).getString(SessionUtils.prefs.armarios.name(), "").contentEquals(new Gson().toJson(mListAux))){
                        Prefs.with(mContext).save(SessionUtils.prefs.armarios.name(), new Gson().toJson(mListAux));
                        showArmarios();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {

            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void showArmarios(){
        mListArmarios = SessionUtils.getListStrings(mContext, SessionUtils.prefs.armarios.name());
        mListArmarios.add(0, "Armario");
        mArmariosAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListArmarios);
        mSpinArmario.setAdapter(mArmariosAdapter);
    }

    private void validateForm(){
        sendData();
    }

    private void sendData(){

    }
}
