package com.ap.atm.utils;

import android.content.Context;

import com.ap.atm.models.CategoryModel;
import com.ap.atm.models.GroupModel;
import com.ap.atm.models.IntersectionModel;
import com.ap.atm.models.SemaphoreModel;
import com.ap.atm.models.SignalModel;
import com.ap.atm.models.UserModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import me.alexrs.prefs.lib.Prefs;

/**
 * Created by Andmari on 20/11/2018.
 */

public class SessionUtils {

    //CODES
    public static int CREATE_NEW_ELEMENT = 1010;

    public enum prefs {
        jwt, groups, dimensions, states, normatives,
        formas, colors, fijacions, materials, fondos,
        marcas, grupos, reguladores, fuentes, ups,
        armarios, user_data, signals, semaphores, intersections, directions, categories
    }

    public enum params{
        semaphore, signal
    }

    public static UserModel getUser(Context mContext){
        String sData = Prefs.with(mContext).getString(prefs.user_data.name(), "");
        Type mDataType = new TypeToken<UserModel>() {}.getType();
        return new Gson().fromJson(sData, mDataType);
    }

    public static List<GroupModel> getGroups(Context mContext){
        String sData = Prefs.with(mContext).getString(prefs.groups.name(), "");
        if(sData.isEmpty()){
            return null;
        }
        Type mDataType = new TypeToken<List<GroupModel>>() {}.getType();
        return new Gson().fromJson(sData, mDataType);
    }

    public static List<CategoryModel> getCategories(Context mContext){
        String sData = Prefs.with(mContext).getString(prefs.categories.name(), "");
        if(sData.isEmpty()){
            return null;
        }
        Type mDataType = new TypeToken<List<CategoryModel>>() {}.getType();
        return new Gson().fromJson(sData, mDataType);
    }

    public static List<String> getListStrings(Context mContext, String prefValue){
        String sData = Prefs.with(mContext).getString(prefValue, "");
        if(sData.isEmpty()){
            return null;
        }
        Type mDataType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(sData, mDataType);
    }

    public static List<IntersectionModel> getIntersections(Context mContext){
        String sData = Prefs.with(mContext).getString(prefs.intersections.name(), "");
        if(sData.isEmpty()){
            return null;
        }
        Type mDataType = new TypeToken<List<IntersectionModel>>() {}.getType();
        return new Gson().fromJson(sData, mDataType);
    }

    public static List<String> getListStringIntersections(Context mContext){
        List<String> mList = new ArrayList<>();
        if(getIntersections(mContext) != null){
            for(IntersectionModel mModel : getIntersections(mContext)){
                mList.add(mModel.main_st+", "+mModel.cross_st);
            }
        }
        return mList;
    }

    public static List<SignalModel> getSignals(Context mContext){
        String sData = Prefs.with(mContext).getString(prefs.signals.name(), "");
        if(sData.isEmpty()){
            return null;
        }
        Type mDataType = new TypeToken<List<SignalModel>>() {}.getType();
        return new Gson().fromJson(sData, mDataType);
    }

    public static List<SemaphoreModel> getSemaphores(Context mContext){
        String sData = Prefs.with(mContext).getString(prefs.semaphores.name(), "");
        if(sData.isEmpty()){
            return null;
        }
        Type mDataType = new TypeToken<List<SemaphoreModel>>() {}.getType();
        return new Gson().fromJson(sData, mDataType);
    }
}
