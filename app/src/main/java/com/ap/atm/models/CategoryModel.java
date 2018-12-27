package com.ap.atm.models;

import java.io.Serializable;

/**
 * Created by Andmari on 23/12/2018.
 */

public class CategoryModel implements Serializable {

    public int id;
    public int grup_id;
    public String code;
    public String meaning;
    public String generalities;
    public String distribution;
    public String general_use;
    public String shape;
    public String color;
    public int status;
    public String update_at;

    public CategoryModel() {
    }
}
