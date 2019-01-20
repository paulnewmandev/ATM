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
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ap.atm.R;
import com.ap.atm.models.CategoryModel;
import com.ap.atm.models.DimensionModel;
import com.ap.atm.models.GroupModel;
import com.ap.atm.models.ImageModel;
import com.ap.atm.models.SerieModel;
import com.ap.atm.models.SignalModel;
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

import org.json.JSONArray;
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

public class AddPortraitSignalActivity extends AppCompatActivity {
    //private Double mLatitude = 0.0, mLongitude = 0.0;
    private String mCurrentPhotoPath;

    private Context mContext;
    //private View mFindLoc;
    //private TextView mTxtLatitude;
    //private TextView mTxtLongitude;

    //Ubicacion Views
    private TextInputLayout mLatitude, mLongitude, mGoogleAddress;

    //Direccion Views
    private TextInputLayout mStreet1;
    private TextInputLayout mStreet2;
    private TextInputLayout mBarrio;
    private TextInputLayout mParroquia;
    private TextInputLayout mOrientation;
    //Categoria Views
    private Spinner mSpinGroup;
    private Spinner mSpinCategory;
    private Spinner mSpinSerie;
    private Spinner mSpinDimension;
    private TextInputLayout mVariacion;
    private TextInputLayout mInformation;
    private CheckBox mChecInformation;

    //Normativa Views
    private Spinner mSpinState;
    private Spinner mSpinNormativa;

    //Caracteristicas Views
    private Spinner mSpinForma;
    private Spinner mSpinColor;
    private Spinner mSpinFijacion;
    private Spinner mSpinMaterial;
    private Spinner mSpinFondo;
    private TextInputLayout mDimensionText;
    private CheckBox mCheckSignalHorizontal;

    //Estado Views
    //private TextInputLayout mEstadoGeneral;
    //private TextInputLayout mNormativa;

    //Conmentarios Views
    private TextInputLayout mComentarios;
    private TextInputLayout mInventario;
    private TextInputLayout mEquipo;
    //Images Views
    //private View mPicRequest;
    private List<ImageModel> mListImages = new ArrayList<>();
    private RecyclerView mImagesRecycler;
    private ImagesAdapter mRecyclerAdapter;
    private SignalModel mModel;

    private Button mSaveButton;

    private List<GroupModel> mListGroup = new ArrayList<>();
    private List<CategoryModel> mListCategories = new ArrayList<>();
    private List<SerieModel> mListSeries = new ArrayList<>();
    private List<DimensionModel> mListDimensions = new ArrayList<>();
    //private List<String> mListDimensions = new ArrayList<>();
    private List<String> mListStates = new ArrayList<>();
    private List<String> mListNormatives = new ArrayList<>();
    private List<String> mListFormas = new ArrayList<>();
    private List<String> mListColors = new ArrayList<>();
    private List<String> mListFijacion = new ArrayList<>();
    private List<String> mListMaterials = new ArrayList<>();
    private List<String> mListFondos = new ArrayList<>();

    private ArrayAdapter<String> mGroupAdapter;
    private ArrayAdapter<String> mCategoryAdapter;
    private ArrayAdapter<String> mSeriesAdapter;
    private ArrayAdapter<String> mDimensionAdapter;
    private ArrayAdapter<String> mStatesAdapter;
    private ArrayAdapter<String> mNormativesAdapter;
    private ArrayAdapter<String> mFormasAdapter;
    private ArrayAdapter<String> mColorsAdapter;
    private ArrayAdapter<String> mFijacionAdapter;
    private ArrayAdapter<String> mMaterialsAdapter;
    private ArrayAdapter<String> mFondosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_portrait_signal);
        Toolbar toolbar = findViewById(R.id.mSimpleToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("NUEVA "+getString(R.string.signal_vertical));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;
        initViews();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK){
            Place place = PlacePicker.getPlace(mContext, intent);
            DecimalFormat df = new DecimalFormat("#.######");
            df.setRoundingMode(RoundingMode.CEILING);
            mLatitude.getEditText().setText(df.format(place.getLatLng().latitude).replace(",", "."));
            mLongitude.getEditText().setText(df.format(place.getLatLng().longitude).replace(",", "."));
            mGoogleAddress.getEditText().setText(place.getAddress());
        }else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            //Toast.makeText(mContext, mCurrentPhotoPath, Toast.LENGTH_LONG).show();
            File image = new File(mCurrentPhotoPath);
            Bitmap mBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
            mListImages.add(new ImageModel(mCurrentPhotoPath, mBitmap));
            mRecyclerAdapter.notifyDataSetChanged();
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
            mRecyclerAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(mContext, "No se recibieron datos", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_add_menu, menu);
        return true;
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
                    startActivityForResult(builder.build(AddPortraitSignalActivity.this), PLACE_PICKER_REQUEST);
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
        //Ubicacion views
        mLatitude = findViewById(R.id.mPortSigInpLatitude);
        mLongitude = findViewById(R.id.mPortSigInpLongitude);
        mGoogleAddress = findViewById(R.id.mPortSigInpAddressGoogle);

        //Direccion Views
        mStreet1 = findViewById(R.id.mPortSigInpStreet1);
        mStreet2 = findViewById(R.id.mPortSigInpStreet2);
        mBarrio = findViewById(R.id.mPortSigInpBarrio);
        mParroquia = findViewById(R.id.mPortSigInpParroquia);
        mOrientation = findViewById(R.id.mPortSigInpOrientation);

        //Categoria Views
        mSpinGroup = findViewById(R.id.mPortSigSpnGroup);
        mSpinCategory = findViewById(R.id.mPortSigSpnCategory);
        mSpinSerie = findViewById(R.id.mPortSigSpnSerie);
        mSpinDimension = findViewById(R.id.mPortSigSpnDimension);
        mVariacion = findViewById(R.id.mPortSigInpVariacion);
        mInformation = findViewById(R.id.mPortSigInpInformation);
        mChecInformation = findViewById(R.id.mPortSigChk1);

        //Normativa Views
        mSpinState = findViewById(R.id.mPortSigSpnState);
        mSpinNormativa = findViewById(R.id.mPortSigSpnNormative);

        //Caracteristicas Views
        mSpinForma = findViewById(R.id.mPortSigSpnForma);
        mSpinColor = findViewById(R.id.mPortSigSpnColor);
        mSpinFijacion = findViewById(R.id.mPortSigSpnFijacion);
        mSpinMaterial = findViewById(R.id.mPortSigSpnMaterial);
        mSpinFondo = findViewById(R.id.mPortSigSpnFondo);
        mDimensionText = findViewById(R.id.mPortSigInpDimension);
        mCheckSignalHorizontal = findViewById(R.id.mPortSigChk2);

        //Conmentarios Views
        mComentarios = findViewById(R.id.mPortSigInpComentario);
        mInventario = findViewById(R.id.mPortSigInpInventario);
        mEquipo = findViewById(R.id.mPortSigInpEquipment);
        //Images Views
        mImagesRecycler = findViewById(R.id.mPortSigRecyclerImages);
        mRecyclerAdapter = new ImagesAdapter(mContext, mListImages);
        //Save Button
        mSaveButton = findViewById(R.id.mPortSigBtnSave);
        initActivity();
    }

    private void initActivity(){
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.groups.name(), "").isEmpty()){
            showGroups();
        }else{
            getGroups();
        }

        if(!Prefs.with(mContext).getString(SessionUtils.prefs.states.name(), "").isEmpty()){
            showStates();
        }else{
            getStates();
        }

        if(!Prefs.with(mContext).getString(SessionUtils.prefs.normatives.name(), "").isEmpty()){
            showNormatives();
        }else{
            getNormatives();
        }

        if(!Prefs.with(mContext).getString(SessionUtils.prefs.formas.name(), "").isEmpty()){
            showFormas();
        }else{
            getFormas();
        }

        if(!Prefs.with(mContext).getString(SessionUtils.prefs.colors.name(), "").isEmpty()){
            showColors();
        }else {
            getColors();
        }

        if(!Prefs.with(mContext).getString(SessionUtils.prefs.fijacions.name(), "").isEmpty()){
            showFijaciones();
        }else {
            getFijacion();
        }

        if(!Prefs.with(mContext).getString(SessionUtils.prefs.materials.name(), "").isEmpty()){
            showMaterials();
        }else {
            getMaterials();
        }

        if(!Prefs.with(mContext).getString(SessionUtils.prefs.fondos.name(), "").isEmpty()){
            showFondos();
        }else {
            getFondos();
        }

        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        mImagesRecycler.setLayoutManager(llm);
        mImagesRecycler.setAdapter(mRecyclerAdapter);

        mSpinGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0){
                    getCategories(SessionUtils.getGroups(mContext).get(position-1).id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0){
                    getSeries(mListCategories.get(position-1).id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinSerie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0){
                    getDimensions(mListSeries.get(position-1).id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()) {
                    sendData();
                }
            }
        });

        if(getIntent() != null){
            if(getIntent().hasExtra(SessionUtils.params.signal.name())){
                getSupportActionBar().setTitle("EDITAR "+getString(R.string.signal_vertical));
                loadSignal();
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

    private void getGroups(){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_GRUPS;
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                if(!Prefs.with(mContext).getString(SessionUtils.prefs.groups.name(), "").contentEquals(rawJsonResponse)){
                    Prefs.with(mContext).save(SessionUtils.prefs.groups.name(), rawJsonResponse);
                    showGroups();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                Toast.makeText(mContext, getString(R.string.error_server), Toast.LENGTH_LONG).show();
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void getCategories(int idGroup){
        final MaterialDialog mDialog = DialogUtils.showProgress(mContext, getString(R.string.title_get_data), getString(R.string.content_get_data));
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_CATEGORIES_GRUPS+idGroup;
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                mDialog.dismiss();
                Type mDataType = new TypeToken<List<CategoryModel>>() {}.getType();
                mListCategories = new Gson().fromJson(rawJsonResponse, mDataType);
                showCategories();

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

    private void getSeries(int idCategory){
        final MaterialDialog mDialog = DialogUtils.showProgress(mContext, getString(R.string.title_get_data), getString(R.string.content_get_data));
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_SERIE_CATEGORY+idCategory;
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                mDialog.dismiss();
                Type mDataType = new TypeToken<List<SerieModel>>() {}.getType();
                mListSeries = new Gson().fromJson(rawJsonResponse, mDataType);
                showSeries();
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

    private void getDimensions(int idSerie){
        final MaterialDialog mDialog = DialogUtils.showProgress(mContext, getString(R.string.title_get_data), getString(R.string.content_get_data));
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_DIMENSIONS_SERIE + idSerie;
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                mDialog.dismiss();
                Type mDataType = new TypeToken<List<DimensionModel>>() {}.getType();
                mListDimensions = new Gson().fromJson(rawJsonResponse, mDataType);
                showDimensions();
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

    private void getStates(){
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
                        showStates();
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

    private void getNormatives(){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_COMBO + ApiUtils.codes.normativa.name();
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
                    if(mListAux.size() > 0 && !Prefs.with(mContext).getString(SessionUtils.prefs.normatives.name(), "").contentEquals(new Gson().toJson(mListAux))){
                        Prefs.with(mContext).save(SessionUtils.prefs.normatives.name(), new Gson().toJson(mListAux));
                        showNormatives();
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

    private void getFormas(){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_COMBO + ApiUtils.codes.forma.name();
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
                    if(mListAux.size() > 0 && !Prefs.with(mContext).getString(SessionUtils.prefs.formas.name(), "").contentEquals(new Gson().toJson(mListAux))){
                        Prefs.with(mContext).save(SessionUtils.prefs.formas.name(), new Gson().toJson(mListAux));
                        showFormas();
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

    private void getColors(){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_COMBO + ApiUtils.codes.background.name();
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
                    if(mListAux.size() > 0 && !Prefs.with(mContext).getString(SessionUtils.prefs.colors.name(), "").contentEquals(new Gson().toJson(mListAux))){
                        Prefs.with(mContext).save(SessionUtils.prefs.colors.name(), new Gson().toJson(mListAux));
                        showColors();
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

    private void getFijacion(){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_COMBO + ApiUtils.codes.fijacion.name();
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
                    if(mListAux.size() > 0 && !Prefs.with(mContext).getString(SessionUtils.prefs.fijacions.name(), "").contentEquals(new Gson().toJson(mListAux))){
                        Prefs.with(mContext).save(SessionUtils.prefs.fijacions.name(), new Gson().toJson(mListAux));
                        showFijaciones();
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

    private void getMaterials(){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_COMBO + ApiUtils.codes.material.name();
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
                    if(mListAux.size() > 0 && !Prefs.with(mContext).getString(SessionUtils.prefs.materials.name(), "").contentEquals(new Gson().toJson(mListAux))){
                        Prefs.with(mContext).save(SessionUtils.prefs.materials.name(), new Gson().toJson(mListAux));
                        showMaterials();
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

    private void getFondos(){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_COMBO + ApiUtils.codes.fondo.name();
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
                    if(mListAux.size() > 0 && !Prefs.with(mContext).getString(SessionUtils.prefs.fondos.name(), "").contentEquals(new Gson().toJson(mListAux))){
                        Prefs.with(mContext).save(SessionUtils.prefs.fondos.name(), new Gson().toJson(mListAux));
                        showFondos();
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

    private void showGroups(){
        mListGroup = SessionUtils.getGroups(mContext);
        List<String> mListAux = new ArrayList<>();
        mListAux.add("Grupo");
        for(GroupModel mGroup : mListGroup){
            mListAux.add(mGroup.name);
        }
        mGroupAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListAux);
        mSpinGroup.setAdapter(mGroupAdapter);
    }

    private void showCategories(){
        List<String> mListAux = new ArrayList<>();
        mListAux.add("Categoria");
        for(CategoryModel mModel : mListCategories){
            mListAux.add(mModel.code);
        }
        mCategoryAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListAux);
        mSpinCategory.setAdapter(mCategoryAdapter);
        if(mModel != null){
            int count = 1;
            for(CategoryModel mCatModel : mListCategories){
                if(mModel.category_id == mCatModel.id){
                    mSpinCategory.setSelection(count);
                    break;
                }
                count++;
            }
        }
    }

    private void showSeries(){
        List<String> mListAux = new ArrayList<>();
        mListAux.add("Serie");
        for(SerieModel mModel : mListSeries){
            mListAux.add(mModel.name);
        }
        mSeriesAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListAux);
        mSpinSerie.setAdapter(mSeriesAdapter);
        if(mModel != null){
            int count = 1;
            for(SerieModel mSerModel : mListSeries){
                if(mModel.seire_id == mSerModel.id || mModel.serie_id == mSerModel.id){
                    mSpinSerie.setSelection(count);
                    break;
                }
                count++;
            }
        }
    }

    private void showDimensions(){
        List<String> mListAux = new ArrayList<>();
        mListAux.add("Dimensión");
        for(DimensionModel mModel : mListDimensions){
            mListAux.add(mModel.dimesion);
        }
        mDimensionAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListAux);
        mSpinDimension.setAdapter(mDimensionAdapter);
        if(mModel != null){
            int count = 1;
            for(DimensionModel mDimModel : mListDimensions){
                if(mModel.dimesion_id == mDimModel.id){
                    mSpinDimension.setSelection(count);
                    break;
                }
                count++;
            }
        }
    }

    private void showStates(){
        mListStates = SessionUtils.getListStrings(mContext, SessionUtils.prefs.states.name());
        Collections.reverse(mListStates);
        mListStates.add(0, "Estado");
        mStatesAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListStates);
        mSpinState.setAdapter(mStatesAdapter);
    }

    private void showNormatives(){
        mListNormatives = SessionUtils.getListStrings(mContext, SessionUtils.prefs.normatives.name());
        mListNormatives.add(0, "Normativa");
        mNormativesAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListNormatives);
        mSpinNormativa.setAdapter(mNormativesAdapter);
    }

    private void showFormas(){
        mListFormas = SessionUtils.getListStrings(mContext, SessionUtils.prefs.formas.name());
        mListFormas.add(0, "Forma");
        mFormasAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListFormas);
        mSpinForma.setAdapter(mFormasAdapter);
    }

    private void showColors(){
        mListColors = SessionUtils.getListStrings(mContext, SessionUtils.prefs.colors.name());
        mListColors.add(0, "Color");
        mColorsAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListColors);
        mSpinColor.setAdapter(mColorsAdapter);
    }

    private void showFijaciones(){
        mListFijacion = SessionUtils.getListStrings(mContext, SessionUtils.prefs.fijacions.name());
        mListFijacion.add(0, "Fijación");
        mFijacionAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListFijacion);
        mSpinFijacion.setAdapter(mFijacionAdapter);
    }

    private void showMaterials(){
        mListMaterials = SessionUtils.getListStrings(mContext, SessionUtils.prefs.materials.name());
        mListMaterials.add(0, "Material");
        mMaterialsAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListMaterials);
        mSpinMaterial.setAdapter(mMaterialsAdapter);
    }

    private void showFondos(){
        mListFondos = SessionUtils.getListStrings(mContext, SessionUtils.prefs.fondos.name());
        mListFondos.add(0, "Fondo");
        mFondosAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListFondos);
        mSpinFondo.setAdapter(mFondosAdapter);
    }

    private boolean validateForm(){
        if(FormUtils.sanitazeInput(mLatitude).isEmpty()){
            Toast.makeText(mContext, getString(R.string.error_empty_location), Toast.LENGTH_LONG).show();
            return  false;
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

        if(FormUtils.sanitazeInput(mOrientation).isEmpty()){
            mOrientation.setError(getString(R.string.error_field_required));
            return false;
        }

        if(mSpinGroup.getSelectedItemPosition() < 1){
            Toast.makeText(mContext, "Debe seleccionar un grupo válido", Toast.LENGTH_LONG).show();
            return false;
        }

        if(mSpinCategory.getSelectedItemPosition() < 1){
            Toast.makeText(mContext, "Debe seleccionar una Categoria válida", Toast.LENGTH_LONG).show();
            return false;
        }

        if(mSpinSerie.getSelectedItemPosition() < 1){
            Toast.makeText(mContext, "Debe seleccionar una Serie válida", Toast.LENGTH_LONG).show();
            return false;
        }

        if(mSpinDimension.getSelectedItemPosition() < 1){
            Toast.makeText(mContext, "Debe seleccionar una Dimensión válida", Toast.LENGTH_LONG).show();
            return false;
        }

        if(FormUtils.sanitazeInput(mVariacion).isEmpty()){
            mVariacion.setError(getString(R.string.error_field_required));
            return false;
        }

        if(FormUtils.sanitazeInput(mInformation).isEmpty()){
            mInformation.setError(getString(R.string.error_field_required));
            return false;
        }

        if(mSpinState.getSelectedItemPosition() < 1){
            Toast.makeText(mContext, "Debe seleccionar un Estado válido", Toast.LENGTH_LONG).show();
            return false;
        }

        if(mSpinNormativa.getSelectedItemPosition() < 1){
            Toast.makeText(mContext, "Debe seleccionar una Normativa válida", Toast.LENGTH_LONG).show();
            return false;
        }

        if(mSpinForma.getSelectedItemPosition() < 1){
            Toast.makeText(mContext, "Debe seleccionar una Forma válida", Toast.LENGTH_LONG).show();
            return false;
        }

        if(mSpinColor.getSelectedItemPosition() < 1){
            Toast.makeText(mContext, "Debe seleccionar un Color válido", Toast.LENGTH_LONG).show();
            return false;
        }

        if(mSpinFijacion.getSelectedItemPosition() < 1){
            Toast.makeText(mContext, "Debe seleccionar una Fijación válida", Toast.LENGTH_LONG).show();
            return false;
        }

        if(mSpinMaterial.getSelectedItemPosition() < 1){
            Toast.makeText(mContext, "Debe seleccionar un Material válido", Toast.LENGTH_LONG).show();
            return false;
        }

        if(mSpinFondo.getSelectedItemPosition() < 1){
            Toast.makeText(mContext, "Debe seleccionar un Fondo válido", Toast.LENGTH_LONG).show();
            return false;
        }

        if(FormUtils.sanitazeInput(mDimensionText).isEmpty()){
            mDimensionText.setError(getString(R.string.error_field_required));
            return false;
        }

        if(FormUtils.sanitazeInput(mComentarios).isEmpty()){
            mComentarios.setError(getString(R.string.error_field_required));
            return false;
        }

        if(getIntent() == null){
            if(mListImages.isEmpty()){
                Toast.makeText(mContext, "Debe agregar al menos una imagen", Toast.LENGTH_LONG).show();
                return false;
            }
        }else if(!getIntent().hasExtra(SessionUtils.params.signal.name())){
            if(mListImages.isEmpty()){
                Toast.makeText(mContext, "Debe agregar al menos una imagen", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private void sendData(){
        final MaterialDialog mDialog = DialogUtils.showProgress(mContext, getString(R.string.title_send_data), getString(R.string.content_send_data));
        String mUrl = ApiUtils.API_URL+ApiUtils.ADD_SIGNALS;
        RequestParams mParams = new RequestParams();
        if(getIntent() != null){
            if(getIntent().hasExtra(SessionUtils.params.signal.name())){
                SignalModel mModel = (SignalModel) getIntent().getSerializableExtra(SessionUtils.params.signal.name());
                mParams.put("id", mModel.id);
                mUrl = ApiUtils.API_URL+ApiUtils.EDIT_SIGNALS;
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
        mParams.put(ApiUtils.parameters.orientation.name(), FormUtils.sanitazeInput(mOrientation));
        mParams.put(ApiUtils.parameters.grup_id.name(), mListGroup.get(mSpinGroup.getSelectedItemPosition()-1).id);
        mParams.put(ApiUtils.parameters.category_id.name(), mListCategories.get(mSpinCategory.getSelectedItemPosition()-1).id);
        mParams.put(ApiUtils.parameters.seire_id.name(), mListSeries.get(mSpinSerie.getSelectedItemPosition()-1).id);
        mParams.put(ApiUtils.parameters.serie_id.name(), mListSeries.get(mSpinSerie.getSelectedItemPosition()-1).id);
        mParams.put(ApiUtils.parameters.dimesion_id.name(), mListDimensions.get(mSpinDimension.getSelectedItemPosition()-1).id);
        mParams.put(ApiUtils.parameters.variation.name(), FormUtils.sanitazeInput(mVariacion));
        mParams.put(ApiUtils.parameters.additional_info.name(), FormUtils.sanitazeInput(mInformation));
        mParams.put(ApiUtils.parameters.is_additional_info.name(), 1);
        mParams.put(ApiUtils.parameters.state.name(), mListStates.size() - mSpinState.getSelectedItemPosition());
        mParams.put(ApiUtils.parameters.normative.name(), mSpinNormativa.getSelectedItemPosition());
        mParams.put(ApiUtils.parameters.shape.name(), mSpinForma.getSelectedItemPosition());
        mParams.put(ApiUtils.parameters.background_color.name(), mSpinColor.getSelectedItemPosition());
        mParams.put(ApiUtils.parameters.dimension.name(), FormUtils.sanitazeInput(mDimensionText));
        mParams.put(ApiUtils.parameters.fixation.name(), mSpinFijacion.getSelectedItemPosition());
        mParams.put(ApiUtils.parameters.material.name(), mSpinMaterial.getSelectedItemPosition());
        mParams.put(ApiUtils.parameters.background.name(), mSpinFondo.getSelectedItemPosition());
        int value = 0;
        if(mCheckSignalHorizontal.isChecked()) value = 1;
        mParams.put(ApiUtils.parameters.is_horizontal.name(), value);
        mParams.put(ApiUtils.parameters.commentary.name(), FormUtils.sanitazeInput(mComentarios));
        mParams.put(ApiUtils.parameters.inventory.name(), "");
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
                Log.d("Resp enviar", rawJsonResponse);
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    if(mJson.getString("Status").contentEquals("OK")){
                        String mLabelResult = "<b>Id Señal vertical: </b> "+mJson.getString("id");
                        final MaterialDialog mDialog2 = DialogUtils.showDialogConfirm(mContext, "Resultado", Html.fromHtml(mLabelResult));
                        mDialog2.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog2.dismiss();
                                finish();
                            }
                        });
                        Toast.makeText(mContext, getString(R.string.register_Success), Toast.LENGTH_LONG).show();
                        //finish();
                    }else{
                        Toast.makeText(mContext, getString(R.string.register_error), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private void loadSignal(){
        mModel = (SignalModel) getIntent().getSerializableExtra(SessionUtils.params.signal.name());
        mLatitude.getEditText().setText(String.valueOf(mModel.latitude));
        mLongitude.getEditText().setText(String.valueOf(mModel.longitude));
        mGoogleAddress.getEditText().setText(mModel.address_google);
        mStreet1.getEditText().setText(mModel.street1);
        mStreet2.getEditText().setText(mModel.street2);
        mBarrio.getEditText().setText(mModel.neighborhood);
        mParroquia.getEditText().setText(mModel.parish);
        mOrientation.getEditText().setText(mModel.orientation);
        int mCount = 1;
        for(GroupModel mGrup : mListGroup){
            if(mGrup.id == mModel.grup_id){
                mSpinGroup.setSelection(mCount);
                break;
            }
            mCount++;
        }
        mVariacion.getEditText().setText(mModel.variation);
        mInformation.getEditText().setText(mModel.additional_info);
        if(mModel.is_additional_info == 1) mChecInformation.setChecked(true);
        mSpinState.setSelection(mModel.state);
        mSpinNormativa.setSelection(mModel.normative);
        mSpinForma.setSelection(mModel.shape);
        mSpinColor.setSelection(mModel.background_color);
        mSpinFijacion.setSelection(mModel.fixation);
        mSpinMaterial.setSelection(mModel.material);
        mSpinFondo.setSelection(mModel.background);
        mDimensionText.getEditText().setText(mModel.dimension);
        if(mModel.is_horizontal == 1) mChecInformation.setChecked(true);
        mComentarios.getEditText().setText(mModel.commentary);
    }

}
