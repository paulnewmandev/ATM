package com.ap.atm.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ap.atm.R;
import com.ap.atm.models.DirectionModel;
import com.ap.atm.models.ElementSemaphoreModel;
import com.ap.atm.models.ImageModel;
import com.ap.atm.models.SemaphoreModel;
import com.ap.atm.models.TypesElements;
import com.ap.atm.ui.adapters.ElementsAdapter;
import com.ap.atm.ui.adapters.ImagesAdapter;
import com.ap.atm.utils.ApiUtils;
import com.ap.atm.utils.DialogUtils;
import com.ap.atm.utils.FormUtils;
import com.ap.atm.utils.SessionUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import me.alexrs.prefs.lib.Prefs;

import static com.ap.atm.utils.Constants.PLACE_PICKER_REQUEST;
import static com.ap.atm.utils.Constants.REQUEST_IMAGE_CAPTURE;
import static com.ap.atm.utils.Constants.REQUEST_IMAGE_GALERY;

public class AddSemaphoreActivity extends AppCompatActivity {
    private String mCurrentPhotoPath;
    private Context mContext;

    //Ubicacion Views
    private TextInputLayout mGoogleAddress, mLatitude, mLongitude;

    //Direccion Views
    private TextInputLayout mStreet1;
    private TextInputLayout mStreet2;
    private TextInputLayout mBarrio;
    private TextInputLayout mParroquia;
    private Spinner mSpinnInterseccion;

    //Caracteristicas Views
    private Spinner mSpinMarca;
    private Spinner mSpinGrupo;
    private Spinner mSpinRegulador;
    private Spinner mSpinFuente;
    private Spinner mSpinUps;
    private Spinner mSpinArmario;

    //Elementos Views
    private TextView mAddElemento;
    private Spinner mSpinElement;
    private Spinner mSpinType;
    private Spinner mSpinStatus;
    private Spinner mSpinDirection;
    private RecyclerView mRecyclerElements;
    private ElementsAdapter mRecyclerElementsAdapter;
    private List<ElementSemaphoreModel> mListElementsObjects = new ArrayList<>();



    //Comentarios Views
    private TextInputLayout mInventario;
    private TextInputLayout mComentario;
    private TextInputLayout mEquipo;

    //ImagesViews
    private RecyclerView mRecyclerImages;
    private List<ImageModel> mListImages = new ArrayList<>();
    private ImagesAdapter mRecyclerImagesAdapter;

    private Button mSaveButton;

    //Sources Spinners
    private List<String> mListIntersections;
    private List<String> mListMarcas;
    private List<String> mListGrupos;
    private List<String> mListReguladores;
    private List<String> mListFuentes;
    private List<String> mListUps;
    private List<String> mListArmarios;
    private List<String> mListElements;
    private List<String> mListTypes;
    private List<String> mListStatus;
    private List<String> mListDirection;


    //Adapters Spinners
    private ArrayAdapter<String> mIntersectionsAdapter;
    private ArrayAdapter<String> mMarcasAdapter;
    private ArrayAdapter<String> mGruposAdapter;
    private ArrayAdapter<String> mReguladoresAdapter;
    private ArrayAdapter<String> mFuentesAdapter;
    private ArrayAdapter<String> mUpsAdapter;
    private ArrayAdapter<String> mArmariosAdapter;
    private ArrayAdapter<String> mElementsAdapter;
    private ArrayAdapter<String> mTypesAdapter;
    private ArrayAdapter<String> mStatusAdapter;
    private ArrayAdapter<String> mDirectionsAdapter;

    private String mLabelResult = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_semaphore);
        Toolbar toolbar = findViewById(R.id.mSimpleToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("NUEVO "+getString(R.string.semaforo));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_add_menu, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK){
            Place place = PlacePicker.getPlace(mContext, intent);
            DecimalFormat df = new DecimalFormat("#.######");
            df.setRoundingMode(RoundingMode.CEILING);
            String strLatitude = df.format(place.getLatLng().latitude).replace(",", ".");
            String strLongitude = df.format(place.getLatLng().longitude).replace(",", ".");
            mLatitude.getEditText().setText(strLatitude);
            mLongitude.getEditText().setText(strLongitude);
            mGoogleAddress.getEditText().setText(place.getAddress());
            getIntersections(strLatitude, strLongitude);
        }else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            //Toast.makeText(mContext, mCurrentPhotoPath, Toast.LENGTH_LONG).show();
            File image = new File(mCurrentPhotoPath);
            Bitmap mBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
            mListImages.add(new ImageModel(mCurrentPhotoPath, mBitmap));
            mRecyclerImagesAdapter.notifyDataSetChanged();
        }else if(requestCode == REQUEST_IMAGE_GALERY && resultCode == Activity.RESULT_OK){
            Uri selectedImage = intent.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mCurrentPhotoPath = cursor.getString(columnIndex);
            cursor.close();
            //Toast.makeText(mContext, mCurrentPhotoPath, Toast.LENGTH_LONG).show();
            File image = new File(mCurrentPhotoPath);
            Bitmap mBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
            mListImages.add(new ImageModel(mCurrentPhotoPath, mBitmap));
            mRecyclerImagesAdapter.notifyDataSetChanged();
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
            case R.id.action_take_picture:
                checkPermissions();
                break;
            case R.id.action_take_location:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(AddSemaphoreActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews(){
        //LocationViews
        mLatitude = findViewById(R.id.mSemaphoreInpLatitude);
        mLongitude = findViewById(R.id.mSemaphoreInpLongitude);
        mGoogleAddress = findViewById(R.id.mSemaphoreInpGoogleAddress);

        //Direccion Views
        mStreet1 = findViewById(R.id.mSemaphoreInpStreet1);
        mStreet2 = findViewById(R.id.mSemaphoreInpStreet2);
        mBarrio = findViewById(R.id.mSemaphoreInpBarrio);
        mParroquia = findViewById(R.id.mSemaphoreInpParroquia);
        mSpinnInterseccion = findViewById(R.id.mSemaphoreSpnInterseccion);

        //Caracteristicas Views
        mSpinMarca = findViewById(R.id.mSemaphoreSpnMarca);
        mSpinGrupo = findViewById(R.id.mSemaphoreSpnGrupo);
        mSpinRegulador = findViewById(R.id.mSemaphoreSpnRegulador);
        mSpinFuente = findViewById(R.id.mSemaphoreSpnFuente);
        mSpinUps = findViewById(R.id.mSemaphoreSpnUps);
        mSpinArmario = findViewById(R.id.mSemaphoreSpnArmario);

        //Elementos Views
        mAddElemento = findViewById(R.id.mSemaphoreAddElement);
        mRecyclerElements = findViewById(R.id.mSemaphoreRecyclerElements);
        mRecyclerElementsAdapter = new ElementsAdapter(mContext, mListElementsObjects, false);

        //Comentarios Views
        mInventario = findViewById(R.id.mSemaphoreInpInventario);
        mComentario = findViewById(R.id.mSemaphoreInpComentario);
        mEquipo = findViewById(R.id.mSemaphoreInpEquipment);

        //Images Views
        mRecyclerImages = findViewById(R.id.mSemaphoreRecyclerImages);
        mRecyclerImagesAdapter = new ImagesAdapter(mContext, mListImages);

        mSaveButton = findViewById(R.id.mSemaphoreBtnSave);

        initActivity();
    }

    private void initActivity(){
        //if(!Prefs.with(mContext).getString(SessionUtils.prefs.intersections.name(), "").isEmpty()){
        //    showIntersections();
        //}else{
        //    getIntersections();
        //}

        if(!Prefs.with(mContext).getString(SessionUtils.prefs.marcas.name(), "").isEmpty()){
            showMarcas();
        }else{
            getMarcas();
        }

        if(!Prefs.with(mContext).getString(SessionUtils.prefs.grupos.name(), "").isEmpty()){
            showGrupos();
        }else{
            getGrupos();
        }

        if(!Prefs.with(mContext).getString(SessionUtils.prefs.reguladores.name(), "").isEmpty()){
            showReguladores();
        }else{
            getReguladores();
        }

        if(!Prefs.with(mContext).getString(SessionUtils.prefs.fuentes.name(), "").isEmpty()){
            showFuentes();
        }else{
            getFuentes();
        }

        if(!Prefs.with(mContext).getString(SessionUtils.prefs.ups.name(), "").isEmpty()){
            showUps();
        }else{
            getUps();
        }

        if(!Prefs.with(mContext).getString(SessionUtils.prefs.armarios.name(), "").isEmpty()){
            showArmarios();
        }
        else{
            getArmarios();
        }

        //showElements();

        if(Prefs.with(mContext).getString(SessionUtils.prefs.states.name(), "").isEmpty()){
            getEstados();
        }

        mAddElemento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddElement();
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerElements.setLayoutManager(llm);
        mRecyclerElements.setAdapter(mRecyclerElementsAdapter);

        LinearLayoutManager llm2 = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerImages.setLayoutManager(llm2);
        mRecyclerImages.setAdapter(mRecyclerImagesAdapter);


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()){
                    sendData();
                }
            }
        });

        if(getIntent() != null){
            if(getIntent().hasExtra(SessionUtils.params.semaphore.name())){
                getSupportActionBar().setTitle("EDITAR "+getString(R.string.semaforo));
                loadSemaphore();
            }
        }
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

    private void getIntersections(String latitude, String longitude){
        final MaterialDialog mDialog = DialogUtils.showProgress(mContext, getString(R.string.title_get_data),
                getString(R.string.content_get_data));
        String mUrl = ApiUtils.API_URL + ApiUtils.VIEW_INTERSECTIONS;
        RequestParams mParams = new RequestParams();
        mParams.put("latitude", latitude);
        mParams.put("longitude", longitude);
        new AsyncHttpClient().post(mUrl, mParams, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                mDialog.dismiss();
                Prefs.with(mContext).save(SessionUtils.prefs.intersections.name(), rawJsonResponse);
                showIntersections();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                mDialog.dismiss();
                Toast.makeText(mContext, "Error al obtener intersecciones", Toast.LENGTH_LONG).show();
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void getMarcas(){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_COMBO + ApiUtils.codes.marca.name();
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    int count = 1;
                    boolean found = true;
                    List<String> mListAux = new ArrayList<>();
                    do{
                        if(mJson.has(String.valueOf(count))){
                            mListAux.add(mJson.getString(String.valueOf(count)));
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

    private void getGrupos(){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_COMBO + ApiUtils.codes.grupo.name();
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    int count = 1;
                    boolean found = true;
                    List<String> mListAux = new ArrayList<>();
                    do{
                        if(mJson.has(String.valueOf(count))){
                            mListAux.add(mJson.getString(String.valueOf(count)));
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

    private void getReguladores(){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_COMBO + ApiUtils.codes.regulador.name();
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    int count = 1;
                    boolean found = true;
                    List<String> mListAux = new ArrayList<>();
                    do{
                        if(mJson.has(String.valueOf(count))){
                            mListAux.add(mJson.getString(String.valueOf(count)));
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

    private void getFuentes(){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_COMBO + ApiUtils.codes.fuente.name();
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    int count = 1;
                    boolean found = true;
                    List<String> mListAux = new ArrayList<>();
                    do{
                        if(mJson.has(String.valueOf(count))){
                            mListAux.add(mJson.getString(String.valueOf(count)));
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

    private void getUps(){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_COMBO + ApiUtils.codes.ups.name();
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    int count = 1;
                    boolean found = true;
                    List<String> mListAux = new ArrayList<>();
                    do{
                        if(mJson.has(String.valueOf(count))){
                            mListAux.add(mJson.getString(String.valueOf(count)));
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

    private void getArmarios(){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_COMBO + ApiUtils.codes.armario.name();
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    int count = 1;
                    boolean found = true;
                    List<String> mListAux = new ArrayList<>();
                    do{
                        if(mJson.has(String.valueOf(count))){
                            mListAux.add(mJson.getString(String.valueOf(count)));
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

    private void getEstados(){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_COMBO + ApiUtils.codes.estado.name();
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    int count = 1;
                    boolean found = true;
                    List<String> mListAux = new ArrayList<>();
                    do{
                        if(mJson.has(String.valueOf(count))){
                            mListAux.add(mJson.getString(String.valueOf(count)));
                            count++;
                        }else{
                            found = false;
                        }
                    }while (found);
                    if(mListAux.size() > 0 && !Prefs.with(mContext).getString(SessionUtils.prefs.states.name(), "").contentEquals(new Gson().toJson(mListAux))){
                        Prefs.with(mContext).save(SessionUtils.prefs.states.name(), new Gson().toJson(mListAux));
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

    private void showIntersections(){
        mListIntersections = SessionUtils.getListStringIntersections(mContext);
        mListIntersections.add(0, "Intersección");
        mIntersectionsAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListIntersections);
        mSpinnInterseccion.setAdapter(mIntersectionsAdapter);
    }

    private void showMarcas(){
        mListMarcas = SessionUtils.getListStrings(mContext, SessionUtils.prefs.marcas.name());
        mListMarcas.add(0, "Marca");
        mMarcasAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListMarcas);
        mSpinMarca.setAdapter(mMarcasAdapter);
    }

    private void showGrupos(){
        mListGrupos = SessionUtils.getListStrings(mContext, SessionUtils.prefs.grupos.name());
        mListGrupos.add(0, "Grupo");
        mGruposAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListGrupos);
        mSpinGrupo.setAdapter(mGruposAdapter);
    }

    private void showReguladores(){
        mListReguladores = SessionUtils.getListStrings(mContext, SessionUtils.prefs.reguladores.name());
        mListReguladores.add(0, "Controlador");
        mReguladoresAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListReguladores);
        mSpinRegulador.setAdapter(mReguladoresAdapter);
    }

    private void showFuentes(){
        mListFuentes = SessionUtils.getListStrings(mContext, SessionUtils.prefs.fuentes.name());
        mListFuentes.add(0, "Fuente");
        mFuentesAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListFuentes);
        mSpinFuente.setAdapter(mFuentesAdapter);
    }

    private void showUps(){
        mListUps = SessionUtils.getListStrings(mContext, SessionUtils.prefs.ups.name());
        mListUps.add(0, "UPS");
        mUpsAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListUps);
        mSpinUps.setAdapter(mUpsAdapter);
    }

    private void showArmarios(){
        mListArmarios = SessionUtils.getListStrings(mContext, SessionUtils.prefs.armarios.name());
        mListArmarios.add(0, "Armario");
        mArmariosAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListArmarios);
        mSpinArmario.setAdapter(mArmariosAdapter);
    }

    private void showElements(){
        mListElements = ElementSemaphoreModel.mElements();
        mElementsAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListElements);
        mSpinElement.setAdapter(mElementsAdapter);
        mSpinElement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showTypeElement(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        showTypeElement(0);
    }

    private void showTypeElement(int position){
        switch (position){
            case 0:
                mListTypes = TypesElements.mTypesVehicular();
                break;
            case 1:
                mListTypes = TypesElements.mTypesPeatonal();
                break;
            case 2:
                mListTypes = TypesElements.mTypesMontaje();
                break;
            default:
                mListTypes = TypesElements.mTypesOtro();
                break;
        }
        mTypesAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListTypes);
        mSpinType.setAdapter(mTypesAdapter);
    }

    private void showEstados(){
        mListStatus = SessionUtils.getListStrings(mContext, SessionUtils.prefs.states.name());
        Collections.reverse(mListStatus);
        mListStatus.add(0, "Estado");
        mStatusAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListStatus);
        mSpinStatus.setAdapter(mStatusAdapter);
    }

    private void showDirections(){
        mListDirection = DirectionModel.mDirections();
        mListDirection.add(0, "Dirección");
        mDirectionsAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListDirection);
        mSpinDirection.setAdapter(mDirectionsAdapter);
    }

    private boolean validateForm(){
        if(FormUtils.sanitazeInput(mLatitude).isEmpty()){
            mLatitude.setError(getString(R.string.error_field_required));
            return false;
        }

        if(FormUtils.sanitazeInput(mStreet1).isEmpty()){
            mStreet1.setError(getString(R.string.error_field_required));
            return false;
        }

        if(FormUtils.sanitazeInput(mStreet2).isEmpty()){
            mStreet2.setError(getString(R.string.error_field_required));
            return false;
        }

        if(FormUtils.sanitazeInput(mBarrio).isEmpty()){
            mBarrio.setError(getString(R.string.error_field_required));
            return false;
        }

        if(FormUtils.sanitazeInput(mParroquia).isEmpty()){
            mParroquia.setError(getString(R.string.error_field_required));
            return false;
        }

        if(mListIntersections.size() > 1){
            if(mSpinnInterseccion.getSelectedItemPosition() == 0){
                Toast.makeText(mContext, "Debe seleccionar una intersección válida", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if(mSpinMarca.getSelectedItemPosition() == 0){
            Toast.makeText(mContext, "Debe seleccionar una marca válida", Toast.LENGTH_LONG).show();
            return false;
        }

        if(mSpinGrupo.getSelectedItemPosition() == 0){
            Toast.makeText(mContext, "Debe seleccionar un grupo válido", Toast.LENGTH_LONG).show();
            return false;
        }

        if(mSpinRegulador.getSelectedItemPosition() == 0){
            Toast.makeText(mContext, "Debe seleccionar un regulador válido", Toast.LENGTH_LONG).show();
            return false;
        }

        if(mSpinFuente.getSelectedItemPosition() == 0){
            Toast.makeText(mContext, "Debe seleccionar una fuente válida", Toast.LENGTH_LONG).show();
            return false;
        }

        if(mSpinUps.getSelectedItemPosition() == 0){
            Toast.makeText(mContext, "Debe seleccionar un UPS válido", Toast.LENGTH_LONG).show();
            return false;
        }

        if(mSpinArmario.getSelectedItemPosition() == 0){
            Toast.makeText(mContext, "Debe seleccionar un armario válido", Toast.LENGTH_LONG).show();
            return false;
        }

        if(mListElementsObjects.isEmpty()){
            Toast.makeText(mContext, "Debe agregar al menos un Elemento", Toast.LENGTH_LONG).show();
            return false;
        }

        if(FormUtils.sanitazeInput(mInventario).isEmpty()){
            mInventario.setError(getString(R.string.error_field_required));
            return false;
        }

        if(FormUtils.sanitazeInput(mComentario).isEmpty()){
            mComentario.setError(getString(R.string.error_field_required));
            return false;
        }

        if(FormUtils.sanitazeInput(mEquipo).isEmpty()){
            mEquipo.setError(getString(R.string.error_field_required));
            return false;
        }

        if(getIntent() == null){
            if(mListImages.isEmpty()){
                Toast.makeText(mContext, "Debe agregar al menos una imagen", Toast.LENGTH_LONG).show();
                return false;
            }
        }else if(!getIntent().hasExtra(SessionUtils.params.semaphore.name())){
            if(mListImages.isEmpty()){
                Toast.makeText(mContext, "Debe agregar al menos una imagen", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private void sendData(){
        final MaterialDialog mDialog = DialogUtils.showProgress(mContext, getString(R.string.title_send_data), getString(R.string.content_send_data));
        String mUrl = ApiUtils.API_URL+ApiUtils.ADD_SEMAFORO;
        RequestParams mParams = new RequestParams();
        if(getIntent() != null){
            if(getIntent().hasExtra(SessionUtils.params.semaphore.name())){
                SemaphoreModel mModel = (SemaphoreModel) getIntent().getSerializableExtra(SessionUtils.params.semaphore.name());
                mParams.put("id", mModel.id);
                mUrl = ApiUtils.API_URL+ApiUtils.EDIT_SEMAFORO;
            }
        }
        mParams.put(ApiUtils.parameters.user_id.name(), SessionUtils.getUser(mContext).id);
        mParams.put(ApiUtils.parameters.latitude.name(), FormUtils.sanitazeInput(mLatitude));
        mParams.put(ApiUtils.parameters.longitude.name(), FormUtils.sanitazeInput(mLongitude));
        mParams.put(ApiUtils.parameters.address_google.name(), FormUtils.sanitazeInput(mGoogleAddress));
        mParams.put(ApiUtils.parameters.street1.name(), FormUtils.sanitazeInput(mStreet1));
        mParams.put(ApiUtils.parameters.street2.name(), FormUtils.sanitazeInput(mStreet2));
        mParams.put(ApiUtils.parameters.neighborhood.name(), FormUtils.sanitazeInput(mBarrio));
        mParams.put(ApiUtils.parameters.parish.name(), FormUtils.sanitazeInput(mParroquia));
        if(mListIntersections.size() > 1){
            mParams.put(ApiUtils.parameters.intersectios_id.name(), SessionUtils.getIntersections(mContext).get(mSpinnInterseccion.getSelectedItemPosition()-1).id);
        }else{
            mParams.put(ApiUtils.parameters.intersectios_id.name(), 0);
        }
        mParams.put(ApiUtils.parameters.brand.name(), mSpinMarca.getSelectedItemPosition()-1);
        mParams.put(ApiUtils.parameters.groups.name(), mSpinGrupo.getSelectedItemPosition()-1);
        mParams.put(ApiUtils.parameters.regulator.name(), mSpinRegulador.getSelectedItemPosition()-1);
        mParams.put(ApiUtils.parameters.source.name(), mSpinFuente.getSelectedItemPosition()-1);
        mParams.put(ApiUtils.parameters.ups.name(), mSpinUps.getSelectedItemPosition()-1);
        mParams.put(ApiUtils.parameters.closet.name(), mSpinArmario.getSelectedItemPosition()-1);
        mParams.put(ApiUtils.parameters.elements.name(), "[]");
        mParams.put(ApiUtils.parameters.commentary.name(), FormUtils.sanitazeInput(mComentario));
        for(ImageModel mModel : mListImages){
            try {
                mParams.put(ApiUtils.parameters.photographs.name()+"[]", new File(mModel.urlPath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("Error_image", "Error al crear archivo de imagen");
            }
        }

        new AsyncHttpClient().post(mUrl, mParams, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                mDialog.dismiss();
                mLabelResult = "";
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    if(mJson.getString("Status").contentEquals("OK")){
                        Toast.makeText(mContext, "Semaforo agregado exitosamente", Toast.LENGTH_LONG).show();
                        mLabelResult+="<b>Id de Semaforo: </b> "+mJson.getString(ApiUtils.responses.id_semaforo.name())+"<br/>";
                        mLabelResult+="<b>Elementos: </b> <br/>";
                        createElements(mJson.getInt(ApiUtils.responses.id.name()));
                        //finish();
                    }else{
                        Toast.makeText(mContext, "Error al crear semaforo", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("Resp Add Semaph", rawJsonResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                mDialog.dismiss();
                Toast.makeText(mContext, getString(R.string.error_server), Toast.LENGTH_LONG).show();
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void createElements(int idSemaphore){
        final MaterialDialog mDialog = DialogUtils.showProgress(mContext, "Agregando Elementos", "Por favor espere mientras se agregan los elementos asociados al semaforo");
        final int[] count = {0};
        String mUrl = ApiUtils.API_URL+ApiUtils.ADD_ELEMENT;
        for(ElementSemaphoreModel mModel : mListElementsObjects){
            if(mModel.id == 0){
                RequestParams mParams = new RequestParams();
                mParams.put(ApiUtils.parameters.id.name(), idSemaphore);
                mParams.put(ApiUtils.parameters.elemento.name(), mModel.element);
                mParams.put(ApiUtils.parameters.tipo.name(), mModel.type_element_id);
                mParams.put(ApiUtils.parameters.status.name(), mModel.status);
                mParams.put(ApiUtils.parameters.direccion.name(), mModel.direction);
                mParams.put(ApiUtils.parameters.user_id.name(), SessionUtils.getUser(mContext).id);
                new AsyncHttpClient().post(mUrl, mParams, new BaseJsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                        Log.d("Resp. Add Element", rawJsonResponse);
                        try{
                            JSONObject mJson = new JSONObject(rawJsonResponse);
                            int element = count[0]+1;
                            mLabelResult+="<b>Elemento "+element+": </b> "+mJson.getString(ApiUtils.responses.id_nventario.name())+"<br/>";

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(count[0] == mListElementsObjects.size()-1){
                            mDialog.dismiss();
                            final MaterialDialog mDialog2 = DialogUtils.showDialogConfirm(mContext, "Resultado", Html.fromHtml(mLabelResult));
                            mDialog2.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog2.dismiss();
                                    finish();
                                }
                            });
                        }
                        count[0]++;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                        mDialog.dismiss();
                        Toast.makeText(mContext, getString(R.string.error_server), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                        return null;
                    }
                });
            }
        }
    }

    private void loadSemaphore(){
        SemaphoreModel mModel = (SemaphoreModel) getIntent().getSerializableExtra(SessionUtils.params.semaphore.name());
        mLatitude.getEditText().setText(String.valueOf(mModel.latitude));
        mLongitude.getEditText().setText(String.valueOf(mModel.longitude));
        mGoogleAddress.getEditText().setText(mModel.address_google);
        mStreet1.getEditText().setText(mModel.street1);
        mStreet2.getEditText().setText(mModel.street2);
        mBarrio.getEditText().setText(mModel.neighborhood);
        mParroquia.getEditText().setText(mModel.parish);
        mSpinnInterseccion.setSelection(mModel.intersectios_id);
        mSpinMarca.setSelection(mModel.brand);
        mSpinGrupo.setSelection(mModel.groups);
        mSpinRegulador.setSelection(mModel.regulator);
        mSpinFuente.setSelection(mModel.source);
        mSpinUps.setSelection(mModel.ups);
        mSpinArmario.setSelection(mModel.closet);
        getElements(mModel.id);
        mInventario.getEditText().setText(mModel.inventory);
        mComentario.getEditText().setText(mModel.commentary);
        mEquipo.getEditText().setText(mModel.equipment);

    }

    private void getElements(int idSemaphore){
        final MaterialDialog mDialog = DialogUtils.showProgress(mContext, "Obteniendo elementos", null);
        String mUrl = ApiUtils.API_URL+"getElementosView/"+idSemaphore;
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                mDialog.dismiss();
                Type mDataType = new TypeToken<List<ElementSemaphoreModel>>() {}.getType();
                mListElementsObjects = new Gson().fromJson(rawJsonResponse, mDataType);
                mRecyclerElementsAdapter = new ElementsAdapter(mContext, mListElementsObjects, true);
                mRecyclerElements.setAdapter(mRecyclerElementsAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                mDialog.dismiss();
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void dialogAddElement(){
        final MaterialDialog mDialog = DialogUtils.showCustomAcceptCancelDialog(mContext, "Agregar Elemento", R.layout.dialog_add_element);
        View v = mDialog.getCustomView();
        mSpinElement = v.findViewById(R.id.mSemaphoreSpnElement);
        mSpinType = v.findViewById(R.id.mSemaphoreSpnType);
        mSpinStatus = v.findViewById(R.id.mSemaphoreSpnStatus);
        mSpinDirection = v.findViewById(R.id.mSemaphoreSpnDirection);
        showElements();
        showDirections();
        showEstados();

        mDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSpinStatus.getSelectedItemPosition() == 0){
                    Toast.makeText(mContext, "Debe indicar el estado del element", Toast.LENGTH_LONG).show();
                }else if(mSpinDirection.getSelectedItemPosition() == 0){
                    Toast.makeText(mContext, "Debe indicar la dirección del element", Toast.LENGTH_LONG).show();
                }else{
                    ElementSemaphoreModel mModel = new ElementSemaphoreModel();
                    mModel.id = 0;
                    mModel.element = ElementSemaphoreModel.mIdElement(mSpinElement.getSelectedItemPosition());
                    mModel.type_element_id = TypesElements.getIdElement(mSpinElement.getSelectedItemPosition()+1, mSpinType.getSelectedItemPosition());
                    mModel.status = mSpinStatus.getSelectedItemPosition();
                    mModel.direction = mSpinDirection.getSelectedItemPosition();
                    mListElementsObjects.add(mModel);
                    mElementsAdapter.notifyDataSetChanged();
                    mDialog.dismiss();
                }
            }
        });

        mDialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }
}
