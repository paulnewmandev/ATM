package com.ap.atm.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andmari on 21/12/2018.
 */

public class TypesElements {

    public static final int S_VEHICULAR = 1;
    public static final int S_PEATONAL = 2;
    public static final int S_MONTAJE = 3;
    public static final int S_OTROS = 4;

    public static List<String> mTypesVehicular(){
        List<String> mList = new ArrayList<>();
        mList.add("S1 - 3/200");
        mList.add("S1B - 3/200 Bus");
        mList.add("S1G - 3/200 Giro");
        mList.add("S1D - 2/200 Destello");
        mList.add("S2 - 1/300 + 2/200");
        mList.add("S2B - 1/300 + 2/200 Bus");
        mList.add("S2G - 1/300 + 2/200 Giro");
        mList.add("S3 - 3/300");
        mList.add("S3B - 3/300 Bus");
        mList.add("S3G - 3/300 Giro");
        mList.add("Otro");
        return mList;
    }

    public static String getIdElement(int element, int position){
        switch (element){
            case S_VEHICULAR:
                return mIdTypeVehicular(position);
            case S_PEATONAL:
                return mIdTypePeatonal(position);
            case S_MONTAJE:
                return mIdTypeMontaje(position);
            default:
                return mIdTypeOtro(position);
        }
    }

    public static String mIdTypeVehicular(int position){
        switch (position){
            case 0:
                return "S1";
            case 1:
                return "S1B";
            case 2:
                return "S1G";
            case 3:
                return "S1D";
            case 4:
                return "S2";
            case 5:
                return "S2B";
            case 6:
                return "S2G";
            case 7:
                return "S3";
            case 8:
                return "S3B";
            case 9:
                return "S3G";
            default:
                return "ND";
        }
    }

    public static List<String> mTypesPeatonal(){
        List<String> mList = new ArrayList<>();
        mList.add("S1PA - 200 Audible");
        mList.add("S1P - 2/200");
        mList.add("S2P - 2/300");
        mList.add("Otro");
        return mList;
    }

    public static String mIdTypePeatonal(int position){
        switch (position){
            case 0:
                return "S1PA";
            case 1:
                return "S1P";
            case 2:
                return "S2P";
            default:
                return "ND";
        }
    }

    public static List<String> mTypesMontaje(){
        List<String> mList = new ArrayList<>();
        mList.add("Cable Tensor");
        mList.add("Poste");
        mList.add("T1");
        mList.add("T2");
        mList.add("T3");
        mList.add("Otro");
        return mList;
    }

    public static String mIdTypeMontaje(int position){
        switch (position){
            case 0:
                return "4";
            case 1:
                return "5";
            case 2:
                return "T1";
            case 3:
                return "T2";
            case 4:
                return "T3";
            default:
                return "ND";
        }
    }

    public static List<String> mTypesOtro(){
        List<String> mList = new ArrayList<>();
        mList.add("Camara");
        mList.add("Dispositivo TV");
        mList.add("Otro");
        return mList;
    }

    public static String mIdTypeOtro(int position){
        switch (position){
            case 0:
                return "1";
            case 1:
                return "2";
            default:
                return "ND";
        }
    }




}
