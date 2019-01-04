package com.ap.atm.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andmari on 20/12/2018.
 */

public class ElementSemaphoreModel implements Serializable {
    public int id;
    public int traffic_light_id;
    public String element;
    public String type_element_id;
    public int status;
    public int direction;
    public String inventory;
    public String updated_at;
    public int user_id;

    public ElementSemaphoreModel() {
    }

    public static List<String> mElements(){
        List<String> mList = new ArrayList<>();
        mList.add("Semáforo vehicular");
        mList.add("Semáforo peatonal");
        mList.add("Montaje");
        mList.add("Otros");
        return mList;
    }

    public static String mIdElement(int position){
        switch (position){
            case 0:
                return "svehicular";
            case 1:
                return "speatonal";
            case 2:
                return "emontaje";
            default:
                return "eotro";
        }
    }
}
