package com.ap.atm.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Andmari on 19/12/2018.
 */

public class SemaphoreModel implements Serializable {

    public int id;
    public int user_id;
    public String date_creat;
    public float latitude;
    public float longitude;
    public String address_google;
    public String street1;
    public String street2;
    public String neighborhood;
    public String parish;
    public int intersectios_id;
    public int brand;
    public int groups;
    public int regulator;
    public int source;
    public int ups;
    public int closet;
    public String elements;
    public List<ElementSemaphoreModel> list_elements;
    public String commentary;
    public String inventory;
    public String photographs;
    public String updated_at;
    public String equipment;

    public SemaphoreModel() {
    }
}
