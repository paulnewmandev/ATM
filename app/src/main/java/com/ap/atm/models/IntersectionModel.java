package com.ap.atm.models;

import java.io.Serializable;

/**
 * Created by Andmari on 21/12/2018.
 */

public class IntersectionModel implements Serializable {

    public int id;
    public String main_st;
    public String cross_st;
    public float distancia;

    public IntersectionModel() {
    }
}
