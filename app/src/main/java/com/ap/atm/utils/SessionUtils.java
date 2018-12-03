package com.ap.atm.utils;

import android.content.Context;

import com.ap.atm.models.GroupModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import me.alexrs.prefs.lib.Prefs;

/**
 * Created by Andmari on 20/11/2018.
 */

public class SessionUtils {
    public enum prefs {jwt, groups, dimensions, states, normatives, formas, colors, fijacions, materials, fondos,
        marcas, grupos, reguladores, fuentes, ups, armarios}

    public static List<GroupModel> getGroups(Context mContext){
        String sData = Prefs.with(mContext).getString(prefs.groups.name(), "");
        if(sData.isEmpty()){
            return null;
        }
        Type mDataType = new TypeToken<List<GroupModel>>() {}.getType();
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

    /*
    public static UserModel getUser(Context mContext){
        String sData = Prefs.with(mContext).getString(USER_DATA, "");
        showLog("userInfo", sData);
        Type mDataType = new TypeToken<UserModel>() {}.getType();
        return new Gson().fromJson(sData, mDataType);
    }
     */
}
