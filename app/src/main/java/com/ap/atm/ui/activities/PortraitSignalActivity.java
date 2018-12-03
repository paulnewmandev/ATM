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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ap.atm.R;
import com.ap.atm.models.GroupModel;
import com.ap.atm.models.ImageModel;
import com.ap.atm.ui.adapters.ImagesAdapter;
import com.ap.atm.utils.ApiUtils;
import com.ap.atm.utils.DialogUtils;
import com.ap.atm.utils.SessionUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import me.alexrs.prefs.lib.Prefs;

import static com.ap.atm.utils.Constants.PLACE_PICKER_REQUEST;
import static com.ap.atm.utils.Constants.REQUEST_IMAGE_CAPTURE;
import static com.ap.atm.utils.Constants.REQUEST_IMAGE_GALERY;

public class PortraitSignalActivity extends AppCompatActivity {
    private Double mLatitude = 0.0, mLongitude = 0.0;
    private String mCurrentPhotoPath;

    private Context mContext;
    private View mFindLoc;
    private TextView mTxtLatitude;
    private TextView mTxtLongitude;
    //General Views
    private TextInputLayout mStreet1;
    private TextInputLayout mStreet2;
    private TextInputLayout mBarrio;
    private TextInputLayout mParroquia;
    //Ubicacion Views
    private Spinner mSpinGroup;
    private Spinner mSpinCategory;
    private Spinner mSpinSerie;
    private Spinner mSpinDimension;
    private TextInputLayout mVariacion;
    private TextInputLayout mInformation;
    private CheckBox mChecInformation;
    //Clasificacion Views
    private Spinner mSpinState;
    private Spinner mSpinNormativa;
    //Caracteristicas Views
    private Spinner mSpinForma;
    private Spinner mSpinColor;
    private Spinner mSpinDimension2;
    private Spinner mSpinFijacion;
    private Spinner mSpinMaterial;
    private Spinner mSpinFondo;
    //Estado Views
    private TextInputLayout mEstadoGeneral;
    private TextInputLayout mNormativa;
    private CheckBox mCheckSignalHorizontal;
    //Conmentarios Views
    private TextInputLayout mComentarios;
    private TextInputLayout mInventario;
    private TextInputLayout mEquipo;
    //Images Views
    private View mPicRequest;
    private List<ImageModel> mListImages = new ArrayList<>();
    private RecyclerView mImagesRecycler;
    private ImagesAdapter mRecyclerAdapter;

    private Button mSaveButton;

    private List<GroupModel> mListGroup = new ArrayList<>();
    private List<String> mListDimensions = new ArrayList<>();
    private List<String> mListStates = new ArrayList<>();
    private List<String> mListNormatives = new ArrayList<>();
    private List<String> mListFormas = new ArrayList<>();
    private List<String> mListColors = new ArrayList<>();
    private List<String> mListFijacion = new ArrayList<>();
    private List<String> mListMaterials = new ArrayList<>();
    private List<String> mListFondos = new ArrayList<>();

    private ArrayAdapter<String> mGroupAdapter;
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
        setContentView(R.layout.activity_portrait_signal);
        getSupportActionBar().setTitle(getString(R.string.signal_vertical));
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
            mLatitude = Double.valueOf(df.format(place.getLatLng().latitude).replace(",", "."));
            mLongitude = Double.valueOf(df.format(place.getLatLng().longitude).replace(",", "."));
            mTxtLatitude.setText(String.valueOf(mLatitude));
            mTxtLongitude.setText(String.valueOf(mLongitude));
        }else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            Toast.makeText(mContext, mCurrentPhotoPath, Toast.LENGTH_LONG).show();
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
            Toast.makeText(mContext, mCurrentPhotoPath, Toast.LENGTH_LONG).show();
            File image = new File(mCurrentPhotoPath);
            Bitmap mBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
            mListImages.add(new ImageModel(mCurrentPhotoPath, mBitmap));
            mRecyclerAdapter.notifyDataSetChanged();
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
        mFindLoc = findViewById(R.id.mPortSigSelectLocation);
        mTxtLatitude = findViewById(R.id.mPortSigTxtLat);
        mTxtLongitude = findViewById(R.id.mPortSigTxtLon);

        //General Views
        mStreet1 = findViewById(R.id.mPortSigInpStreet1);
        mStreet2 = findViewById(R.id.mPortSigInpStreet2);
        mBarrio = findViewById(R.id.mPortSigInpBarrio);
        mParroquia = findViewById(R.id.mPortSigInpParroquia);
        //Ubicacion Views
        mSpinGroup = findViewById(R.id.mPortSigSpnGroup);
        mSpinCategory = findViewById(R.id.mPortSigSpnCategory);
        mSpinSerie = findViewById(R.id.mPortSigSpnSerie);
        mSpinDimension = findViewById(R.id.mPortSigSpnDimension);
        mVariacion = findViewById(R.id.mPortSigInpVariacion);
        mInformation = findViewById(R.id.mPortSigInpInformation);
        mChecInformation = findViewById(R.id.mPortSigChk1);
        //Clasificacion Views
        mSpinState = findViewById(R.id.mPortSigSpnState);
        mSpinNormativa = findViewById(R.id.mPortSigSpnNormative);
        //Caracteristicas Views
        mSpinForma = findViewById(R.id.mPortSigSpnForma);
        mSpinColor = findViewById(R.id.mPortSigSpnColor);
        mSpinDimension2 = findViewById(R.id.mPortSigSpnDimension2);
        mSpinFijacion = findViewById(R.id.mPortSigSpnFijacion);
        mSpinMaterial = findViewById(R.id.mPortSigSpnMaterial);
        mSpinFondo = findViewById(R.id.mPortSigSpnFondo);
        //Estado Views
        mEstadoGeneral = findViewById(R.id.mPortSigInpState);
        mNormativa = findViewById(R.id.mPortSigInpNormativa);
        mCheckSignalHorizontal = findViewById(R.id.mPortSigChk2);
        //Conmentarios Views
        mComentarios = findViewById(R.id.mPortSigInpComentario);
        mInventario = findViewById(R.id.mPortSigInpInventario);
        mEquipo = findViewById(R.id.mPortSigInpEquipment);
        //Images Views
        mPicRequest = findViewById(R.id.mPortSigImgPicRequest);
        mImagesRecycler = findViewById(R.id.mPortSigRecyclerImages);
        mRecyclerAdapter = new ImagesAdapter(mContext, mListImages);
        //Save Button
        mSaveButton = findViewById(R.id.mPortSigBtnSave);
        initActivity();
    }

    private void initActivity(){
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.groups.name(), "").isEmpty()){
            showGroups();
        }
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.dimensions.name(), "").isEmpty()){
            showDimensions();
        }
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.states.name(), "").isEmpty()){
            showStates();
        }
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.normatives.name(), "").isEmpty()){
            showNormatives();
        }
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.formas.name(), "").isEmpty()){
            showFormas();
        }
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.colors.name(), "").isEmpty()){
            showColors();
        }
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.fijacions.name(), "").isEmpty()){
            showFijaciones();
        }
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.materials.name(), "").isEmpty()){
            showMaterials();
        }
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.fondos.name(), "").isEmpty()){
            showFondos();
        }
        getGroups();
        getDimensions();
        getStates();
        getNormatives();
        getFormas();
        getColors();
        getFijacion();
        getMaterials();
        getFondos();

        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        mImagesRecycler.setLayoutManager(llm);
        mImagesRecycler.setAdapter(mRecyclerAdapter);

        mFindLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(PortraitSignalActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

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

    private void getGroups(){
        String mUrl = ApiUtils.API_URL;
        RequestParams mParams = new RequestParams();
        mParams.put(ApiUtils.parameters.action.name(), ApiUtils.actions.getGrups.name());
        new AsyncHttpClient().get(mUrl, mParams, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    int count = 1;
                    boolean found = true;
                    List<GroupModel> mAuxList = new ArrayList<>();
                    do{
                        if(mJson.has(String.valueOf(count))){
                            JSONObject mData = mJson.getJSONObject(String.valueOf(count));
                            GroupModel mGroup = new GroupModel();
                            mGroup.id = mData.getInt(GroupModel.fields.id.name());
                            mGroup.name = mData.getString(GroupModel.fields.name.name());
                            mGroup.status = mData.getInt(GroupModel.fields.status.name());
                            mGroup.update_at = mData.getString(GroupModel.fields.update_at.name());
                            mAuxList.add(mGroup);
                            count++;
                        }else{
                            found = false;
                        }
                    }while (found);
                    if(mAuxList.size() > 0 && !Prefs.with(mContext).getString(SessionUtils.prefs.groups.name(), "")
                            .contentEquals(new Gson().toJson(mAuxList))){
                        Prefs.with(mContext).save(SessionUtils.prefs.groups.name(), new Gson().toJson(mAuxList));
                        showGroups();
                    }
                } catch (JSONException e) {
                    Toast.makeText(mContext, getString(R.string.error_json), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
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

    private void showGroups(){
        mListGroup = SessionUtils.getGroups(mContext);
        List<String> mListAux = new ArrayList<>();
        mListAux.add("Grupo");
        for(GroupModel mGroup : mListGroup){
            mListAux.add(mGroup.name);
        }
        mGroupAdapter = new ArrayAdapter<String>(mContext, R.layout.item_spinner, mListAux);
        mSpinGroup.setAdapter(mGroupAdapter);
    }

    private void getDimensions(){
        String mUrl = ApiUtils.API_URL;
        RequestParams mParams = new RequestParams();
        mParams.put(ApiUtils.parameters.action.name(), ApiUtils.actions.getCombo.name());
        mParams.put(ApiUtils.parameters.code.name(), ApiUtils.codes.dimension.name());
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
                    if(mListAux.size() > 0 && !Prefs.with(mContext).getString(SessionUtils.prefs.dimensions.name(), "").contentEquals(new Gson().toJson(mListAux))){
                        Prefs.with(mContext).save(SessionUtils.prefs.dimensions.name(), new Gson().toJson(mListAux));
                        showDimensions();
                    }
                    showDimensions();
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

    private void showDimensions(){
        mListDimensions = SessionUtils.getListStrings(mContext, SessionUtils.prefs.dimensions.name());
        mListDimensions.add(0, "Dimensión");
        mDimensionAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListDimensions);
        mSpinDimension.setAdapter(mDimensionAdapter);
        mSpinDimension2.setAdapter(mDimensionAdapter);
    }

    private void getStates(){
        String mUrl = ApiUtils.API_URL;
        RequestParams mParams = new RequestParams();
        mParams.put(ApiUtils.parameters.action.name(), ApiUtils.actions.getCombo.name());
        mParams.put(ApiUtils.parameters.code.name(), ApiUtils.codes.estado.name());
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

    private void showStates(){
        mListStates = SessionUtils.getListStrings(mContext, SessionUtils.prefs.states.name());
        mListStates.add(0, "Estado");
        mStatesAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListStates);
        mSpinState.setAdapter(mStatesAdapter);
    }

    private void getNormatives(){
        String mUrl = ApiUtils.API_URL;
        RequestParams mParams = new RequestParams();
        mParams.put(ApiUtils.parameters.action.name(), ApiUtils.actions.getCombo.name());
        mParams.put(ApiUtils.parameters.code.name(), ApiUtils.codes.normativa.name());
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

    private void showNormatives(){
        mListNormatives = SessionUtils.getListStrings(mContext, SessionUtils.prefs.normatives.name());
        mListNormatives.add(0, "Normativa");
        mNormativesAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListNormatives);
        mSpinNormativa.setAdapter(mNormativesAdapter);
    }

    private void getFormas(){
        String mUrl = ApiUtils.API_URL;
        RequestParams mParams = new RequestParams();
        mParams.put(ApiUtils.parameters.action.name(), ApiUtils.actions.getCombo.name());
        mParams.put(ApiUtils.parameters.code.name(), ApiUtils.codes.forma.name());
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

    private void showFormas(){
        mListFormas = SessionUtils.getListStrings(mContext, SessionUtils.prefs.formas.name());
        mListFormas.add(0, "Forma");
        mFormasAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListFormas);
        mSpinForma.setAdapter(mFormasAdapter);
    }

    private void getColors(){
        String mUrl = ApiUtils.API_URL;
        RequestParams mParams = new RequestParams();
        mParams.put(ApiUtils.parameters.action.name(), ApiUtils.actions.getCombo.name());
        mParams.put(ApiUtils.parameters.code.name(), ApiUtils.codes.background.name());
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

    private void showColors(){
        mListColors = SessionUtils.getListStrings(mContext, SessionUtils.prefs.colors.name());
        mListColors.add(0, "Color");
        mColorsAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListColors);
        mSpinColor.setAdapter(mColorsAdapter);
    }

    private void getFijacion(){
        String mUrl = ApiUtils.API_URL;
        RequestParams mParams = new RequestParams();
        mParams.put(ApiUtils.parameters.action.name(), ApiUtils.actions.getCombo.name());
        mParams.put(ApiUtils.parameters.code.name(), ApiUtils.codes.fijacion.name());
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

    private void showFijaciones(){
        mListFijacion = SessionUtils.getListStrings(mContext, SessionUtils.prefs.fijacions.name());
        mListFijacion.add(0, "Fijación");
        mFijacionAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListFijacion);
        mSpinFijacion.setAdapter(mFijacionAdapter);
    }

    private void getMaterials(){
        String mUrl = ApiUtils.API_URL;
        RequestParams mParams = new RequestParams();
        mParams.put(ApiUtils.parameters.action.name(), ApiUtils.actions.getCombo.name());
        mParams.put(ApiUtils.parameters.code.name(), ApiUtils.codes.material.name());
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

    private void showMaterials(){
        mListMaterials = SessionUtils.getListStrings(mContext, SessionUtils.prefs.materials.name());
        mListMaterials.add(0, "Material");
        mMaterialsAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListMaterials);
        mSpinMaterial.setAdapter(mMaterialsAdapter);
    }

    private void getFondos(){
        String mUrl = ApiUtils.API_URL;
        RequestParams mParams = new RequestParams();
        mParams.put(ApiUtils.parameters.action.name(), ApiUtils.actions.getCombo.name());
        mParams.put(ApiUtils.parameters.code.name(), ApiUtils.codes.fondo.name());
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

    private void showFondos(){
        mListFondos = SessionUtils.getListStrings(mContext, SessionUtils.prefs.fondos.name());
        mListFondos.add(0, "Fondo");
        mFondosAdapter = new ArrayAdapter<>(mContext, R.layout.item_spinner, mListFondos);
        mSpinMaterial.setAdapter(mFondosAdapter);
    }

    private void validateForm(){
        sendData();
    }

    private void sendData(){

    }


}
