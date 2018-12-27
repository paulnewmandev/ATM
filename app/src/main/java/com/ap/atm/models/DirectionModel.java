package com.ap.atm.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andmari on 21/12/2018.
 */

public class DirectionModel implements Serializable {

    public static List<String> mDirections(){
        List<String> mList = new ArrayList<>();
        mList.add("Alfabetico cardinal");
        mList.add("Otro");
        return mList;
    }

    public static String mIdDirection(int position){
        switch (position){
            case 0:
                return "A";
            default:
                return "ND";
        }
    }
}
