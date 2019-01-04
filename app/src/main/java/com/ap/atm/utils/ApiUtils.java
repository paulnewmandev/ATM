package com.ap.atm.utils;

import android.content.Context;
import android.widget.Toast;

import com.ap.atm.R;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import me.alexrs.prefs.lib.Prefs;

/**
 * Created by Andmari on 20/11/2018.
 */

public class ApiUtils {

    //public static String API_URL = "https://atm.gotopdev.com/atmapi/public/";
    public static String API_URL = "http://atmdev.info/atmapi/public/";
    public static String ACTION = "action";
    //////--------ACTIONS-----//////
    public static String LOGIN = "login";
    public static String GET_COMBO = "getCombo/";
    public static String GET_GRUPS = "getGrups";
    public static String GET_CATEGORIES_GRUPS= "getCategoriesGrups/";
    public static String GET_CATEGORIES = "getCategoriesAll";
    public static String GET_SERIE_CATEGORY = "getSeriesCategories/";
    public static String GET_DIMENSIONS_SERIE = "getDimensionsSeries/";

    public static String GET_SIGNALS_ALL = "getSignalsAll";
    public static String GET_SIGNALS_VIEW = "getSignalsView/";
    public static String ADD_SIGNALS = "addSignals";
    public static String EDIT_SIGNALS = "editSignals";
    public static String DELETE_SIGNALS = "deleteSignals";

    public static String GET_SEMAFOROS_ALL = "getSemaforosAll";
    public static String GET_SEMAFOROS_VIEW = "getSemaforosView/";
    public static String GET_ELEMENTOS_VIEW = "getElementosView/";
    public static String ADD_SEMAFORO = "addSemaforos";
    public static String EDIT_SEMAFORO = "editSemaforos";
    public static String DELETE_SEMAFORO = "deleteSemaforo";
    public static String GET_INTERSECTIONS = "getIntersectionsAll";
    public static String VIEW_INTERSECTIONS = "viewIntersectionsDistance";
    public static String ADD_ELEMENT = "addElemento";


    public enum codes {
        grupo, dimension, estado, normativa, forma,
        background, fijacion, material, fondo, marca,
        regulador, fuente, ups, armario, elemento, direccion,
        svehicular, speatonal, emontaje, eotro
    }

    public enum parameters {
        //Login
        user, password,
        //Add semaphore
        user_id, latitude, longitude, address_google, street1, street2, neighborhood,
        parish, intersectios_id, brand, groups, regulator, source, ups, closet, elements,
        commentary, photographs,

        //Add Signal

        //user_id,
        date_creat,
        //latitude,
        //longitude,
        //address_google,
        //street1,
        //street2,
        //neighborhood,
        //parish,
        orientation,

        grup_id,
        category_id,
        serie_id,
        seire_id,
        dimesion_id,

        variation,
        is_additional_info,
        additional_info,
        state,
        normative,
        shape,
        background_color,
        dimension,
        fixation,
        material,
        background,
        is_horizontal,
        //commentary,
        inventory,
        //photographs,

        //AddElemets
        id, elemento, tipo, status, direccion, //user_id

        action, code
    }

    public enum responses {
        JWT, records, id, id_semaforo, id_inventario, Status, id_nventario
    }

    public static void getDatas(Context mContext){
        //Semaphores
        //getIntersections(mContext);
        getMarcas(mContext);
        getGrupos(mContext);
        getReguladores(mContext);
        getFuentes(mContext);
        getUps(mContext);
        getArmarios(mContext);
        getStates(mContext);

        //signals
        getGroups(mContext);
        getCategories(mContext);
        getDimensions(mContext);
        //getStates(mContext); Esta funcion es para semaforos y señales verticales
        getNormatives(mContext);
        getFormas(mContext);
        getColors(mContext);
        getFijacion(mContext);
        getMaterials(mContext);
        getFondos(mContext);
    }

    //Semaforo DATAS

    private static void getIntersections(final Context mContext){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_INTERSECTIONS;
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                if(!Prefs.with(mContext).getString(SessionUtils.prefs.intersections.name(), "").contentEquals(rawJsonResponse)){
                    Prefs.with(mContext).save(SessionUtils.prefs.intersections.name(), rawJsonResponse);
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

    private static void getMarcas(final Context mContext){
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

    private static void getGrupos(final Context mContext){
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

    private static void getReguladores(final Context mContext){
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

    private static void getFuentes(final Context mContext){
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

    private static void getUps(final Context mContext){
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

    private static void getArmarios(final Context mContext){
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

    //Señales DATAS

    private static void getGroups(final Context mContext){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_GRUPS;
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                if(!Prefs.with(mContext).getString(SessionUtils.prefs.groups.name(), "").contentEquals(rawJsonResponse)){
                    Prefs.with(mContext).save(SessionUtils.prefs.groups.name(), rawJsonResponse);
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

    private static void getCategories(final Context mContext){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_CATEGORIES;
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                if(!Prefs.with(mContext).getString(SessionUtils.prefs.categories.name(), "").contentEquals(rawJsonResponse)){
                    Prefs.with(mContext).save(SessionUtils.prefs.categories.name(), rawJsonResponse);
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

    private static void getDimensions(final Context mContext){
        String mUrl = ApiUtils.API_URL + ApiUtils.GET_COMBO + ApiUtils.codes.dimension.name();
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
                    if(mListAux.size() > 0 && !Prefs.with(mContext).getString(SessionUtils.prefs.dimensions.name(), "").contentEquals(new Gson().toJson(mListAux))){
                        Prefs.with(mContext).save(SessionUtils.prefs.dimensions.name(), new Gson().toJson(mListAux));
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

    private static void getStates(final Context mContext){
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

    private static void getNormatives(final Context mContext){
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

    private static void getFormas(final Context mContext){
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

    private static void getColors(final Context mContext){
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

    private static void getFijacion(final Context mContext){
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

    private static void getMaterials(final Context mContext){
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

    private static void getFondos(final Context mContext){
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

}
