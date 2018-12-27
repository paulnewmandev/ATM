package com.ap.atm.models;

import java.io.Serializable;

/**
 * Created by Andmari on 19/12/2018.
 */

public class ElementModel implements Serializable {

    public int id;
    public int traffic_light_id;
    public int element;
    public int type_element_id;
    public int status;
    public int direction;
    public int inventory;
    public int updated_at;
    public int user_id;

    public ElementModel() {
    }
}
