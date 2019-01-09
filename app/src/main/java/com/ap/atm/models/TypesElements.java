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
        mList.add("SC - Ciclovia");
        mList.add("SFT - Tren o tranvía");
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
                return "SC";
            case 1:
                return "SFT";
            case 2:
                return "S1";
            case 3:
                return "S1B";
            case 4:
                return "S1G";
            case 5:
                return "S1D";
            case 6:
                return "S2";
            case 7:
                return "S2B";
            case 8:
                return "S2G";
            case 9:
                return "S3";
            case 10:
                return "S3B";
            case 11:
                return "S3G";
            default:
                return "ND";
        }
    }

    public static List<String> mTypesPeatonal(){
        List<String> mList = new ArrayList<>();
        mList.add("PP - Pulsador peatonal");
        mList.add("S1P - 2/200 peatonal");
        mList.add("S3P - 3/300 peatonal");
        mList.add("Otro");
        return mList;
    }

    public static String mIdTypePeatonal(int position){
        switch (position){
            case 0:
                return "PP";
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
        mList.add("T1 - Báculo 4m SC");
        mList.add("T2 - Báculo 4m CCA");
        mList.add("T3 - Columna peatonal");
        mList.add("CM - Otro complementario");
        return mList;
    }

    public static String mIdTypeMontaje(int position){
        switch (position){
            case 0:
                return "T1";
            case 2:
                return "T2";
            case 3:
                return "T3";
            default:
                return "CM";
        }
    }

    public static List<String> mTypesOtro(){
        List<String> mList = new ArrayList<>();
        mList.add("RT - Regulador de tránsito");
        mList.add("CR - Caja de revisión");
        mList.add("CT - Centro de control de tránsito");
        mList.add("SD - Ducto de semaforización");
        mList.add("DI - Detector intrusivo");
        mList.add("DNI - Detector no intrusivo");
        mList.add("GBM - Gabinete de zona");
        mList.add("GB1 - Gabinete 8 canales");
        mList.add("GB2 - Gabinete 12 canales");
        mList.add("GB3 - Gabinete 16 canales");
        mList.add("RZ - Central de zona");
        mList.add("CM - Otro complementario");
        return mList;
    }

    public static String mIdTypeOtro(int position){
        switch (position){
            case 0:
                return "RT";
            case 1:
                return "CR";
            case 2:
                return "CT";
            case 3:
                return "SD";
            case 4:
                return "DI";
            case 5:
                return "DNI";
            case 6:
                return "GBM";
            case 7:
                return "GB1";
            case 8:
                return "GB2";
            case 9:
                return "GB3";
            case 10:
                return "RZ";
            default:
                return "CM";
        }
    }
}
