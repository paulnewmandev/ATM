package com.ap.atm.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * Creado Por sergio el 13/01/16.
 */
public class FormUtils {

    public static boolean isEmailValid(TextInputLayout mInput){
        return sanitazeInput(mInput).contains("@") && sanitazeInput(mInput).contains(".");
    }

    public static boolean isPhoneValid(TextInputLayout mInput){
        return sanitazeInput(mInput).length() == 10;
    }

    public static String sanitazeInput(TextInputLayout mInput){
        return mInput.getEditText().getText().toString().trim();
    }

    public static boolean validateAdmission(String validTime, String currentTime){
        String[] parts = currentTime.split(":");
        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        cal1.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        cal1.set(Calendar.SECOND, Integer.parseInt(parts[2]));

        parts = validTime.split(":");
        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        cal2.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        cal2.set(Calendar.SECOND, Integer.parseInt(parts[2]));
        cal2.add(Calendar.DATE, 1);
        return cal1.before(cal2); //true si esta antes de validTime
    }

    public static boolean validateUse(String validTime, String currentTime){
        String[] parts = currentTime.split(":");
        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        cal1.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        cal1.set(Calendar.SECOND, Integer.parseInt(parts[2]));

        parts = validTime.split(":");
        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        cal2.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        cal2.set(Calendar.SECOND, Integer.parseInt(parts[2]));
        cal2.add(Calendar.DATE, 1);
        return !cal1.before(cal2); //true si esta despues de validTime
    }

    /**
     * @return FECHA ACTUAL FORMATIADA
     */
    public static String dateNow(){
        SimpleDateFormat mFormat = new SimpleDateFormat("MMMM d yyyy", new Locale("es", "ES"));
        return mFormat.format(new Date());
    }

    public static String currentDateTime(){
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", new Locale("es", "ES"));
        return mFormat.format(new Date());
    }

    public static String currentDate(){
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "ES"));
        return mFormat.format(new Date());
    }

    public static String currentTime(){
        SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss", new Locale("es", "ES"));
        return mFormat.format(new Date());
    }

    public static boolean validateRangeHours(int minHour, int minMin, int maxHour, int maxMin){
        String mCurrentTime = currentTime();
        String[] splitTime = mCurrentTime.split(":");
        if(Integer.parseInt(splitTime[0]) > minHour && Integer.parseInt(splitTime[0]) < maxHour){
            return true;
        }else if(Integer.parseInt(splitTime[0]) == minHour || Integer.parseInt(splitTime[0]) == maxHour){
            if(Integer.parseInt(splitTime[0]) == minHour){
                return Integer.parseInt(splitTime[1]) > minMin;
            }else{
                return Integer.parseInt(splitTime[1]) < maxMin;
            }
        }else{
            return false;
        }
    }

    /**
     * @return TIEMPO ACTUAL
     */
    public static String timeNow(){
        SimpleDateFormat mFormat =
                new SimpleDateFormat("h':'m a", new Locale("es", "ES"));
        return mFormat.format(new Date());
    }

    public static Date yyyyMMddTHHmmssToDate(String dateInString){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es", "ES"));
        try {
            return formatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String yyyyMMddTHHmmssToddMMyyyyTHHmmss(String mDate){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", new Locale("es", "ES"));
        SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss", new Locale("es", "ES"));
        try {
            return mFormat.format(formatter.parse(mDate));
        } catch (ParseException e) {
            e.printStackTrace();
            return "error";
        }

    }

    public static Date yyyyMMddToDate(String dateInString){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "ES"));
        try {
            return formatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String dateToStringddMMM(Date date){
        SimpleDateFormat mFormat =
                new SimpleDateFormat("dd-MMM", new Locale("es", "ES"));
        return mFormat.format(date);
    }

    public static String dateToStringddMMMyyyy(Date date){
        SimpleDateFormat mFormat =
                new SimpleDateFormat("dd MMM yyyy", new Locale("es", "ES"));
        return mFormat.format(date);
    }

    public static String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    public static boolean saveBitmapIntoFile(Bitmap bitmap, File file){
        OutputStream os;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //CREAR IMAGEN
    public static File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }


}
