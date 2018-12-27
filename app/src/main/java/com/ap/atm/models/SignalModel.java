package com.ap.atm.models;

import java.io.Serializable;

/**
 * Created by Andmari on 19/12/2018.
 */

public class SignalModel implements Serializable {

    public int id;
    public int user_id;
    public String date_creat;
    public float latitude;
    public float longitude;
    public String orientation;
    public int grup_id;
    public int category_id;
    public int seire_id;
    public int serie_id;
    public int dimesion_id;
    public String address_google;
    public String street1;
    public String street2;
    public String neighborhood;
    public String parish;
    public String variation;
    public int is_additional_info;
    public String additional_info;
    public int state;
    public int normative;
    public int shape;
    public int background_color;
    public String dimension;
    public int fixation;
    public int material;
    public int background;
    public int is_horizontal;
    public String commentary;
    public String inventory;
    public String photographs;
    public String updated_at;
    public String equipment;

    public SignalModel() {
    }
}
