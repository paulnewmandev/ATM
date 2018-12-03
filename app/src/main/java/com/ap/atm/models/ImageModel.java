package com.ap.atm.models;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Andmari on 3/12/2018.
 */

public class ImageModel implements Serializable {
    public String urlPath;
    public Bitmap imageBitmap;

    public ImageModel() {
    }

    public ImageModel(String urlPath, Bitmap imageBitmap) {
        this.urlPath = urlPath;
        this.imageBitmap = imageBitmap;
    }
}
