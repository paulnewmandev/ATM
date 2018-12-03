package com.ap.atm.utils;

/**
 * Created by Andmari on 20/11/2018.
 */

public class ApiUtils {

    public static String API_URL = "https://atm.gotopdev.com/api/";
    public static String ACTION = "action";

    public enum actions {login, getCombo, getGrups}

    public enum codes {grupo, dimension, estado, normativa, forma, background, fijacion, material, fondo, marca,
        regulador, fuente, ups, armario}

    public enum parameters {username, password, action, code}

    public enum responses {JWT, records}

}
