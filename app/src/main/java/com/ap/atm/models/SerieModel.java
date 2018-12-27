package com.ap.atm.models;

import java.io.Serializable;

/**
 * Created by Andmari on 23/12/2018.
 */

public class SerieModel implements Serializable {

    public int id;
    public int category_id;
    public String code;
    public String identification;
    public String uso;
    public String name;
    public String description;
    public String image;
    public String image_fn;
    public int status;
    public String update_at;

    public SerieModel() {
    }
}
