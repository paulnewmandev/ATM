package com.ap.atm.models;

import java.io.Serializable;

/**
 * Created by Andmari on 23/12/2018.
 */

public class DimensionModel implements Serializable {

    public int id;
    public String serie_id;
    public String code_signal;
    public String dimesion;
    public String dimesion_fn;
    public int status;
    public String updated_at;

    public DimensionModel() {
    }
}
