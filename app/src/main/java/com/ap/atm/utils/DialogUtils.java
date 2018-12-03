package com.ap.atm.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spanned;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ap.atm.R;


import java.util.Collection;

/**
 * Creado Por sergio el 14/01/16.
 */
public class DialogUtils {

    public static MaterialDialog showProgress(Context mCon, String mTitle, String mContent){
        return new MaterialDialog.Builder(mCon)
                .title(mTitle)
                .content(mContent)
                .cancelable(false)
                .autoDismiss(false)
                .progress(true, 0)
                .show();
    }

    public static MaterialDialog showDialogAcceptCancel(Context mCon, String mTitle, String mContent){
        return new MaterialDialog.Builder(mCon)
                .title(mTitle)
                .content(mContent)
                .contentColorRes(R.color.primary_text)
                .positiveText(R.string.accept)
                .negativeText(R.string.cancel)
                .positiveColorRes(R.color.primaryATM)
                .negativeColorRes(R.color.primaryATM)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public static MaterialDialog showCustomAcceptCancelDialog(Context mCon, String mTitle, int mIdLayout){
        return new MaterialDialog.Builder(mCon)
                .title(mTitle)
                .customView(mIdLayout, false)
                .contentColorRes(R.color.primary_text)
                .positiveText(R.string.accept)
                .negativeText(R.string.cancel)
                .positiveColorRes(R.color.primaryATM)
                .negativeColorRes(R.color.primaryATM)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public static MaterialDialog showDialogConfirm(Context mCon, String mTitle,
                                                   String mContent){
        return new MaterialDialog.Builder(mCon)
                .title(mTitle)
                .content(mContent)
                .contentColorRes(R.color.primary_text)
                .positiveText(R.string.accept)
                .positiveColorRes(R.color.primaryATM)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public static MaterialDialog showCustomDialog(Context mContext, int mIdLayout){
        return new MaterialDialog.Builder(mContext)
                .title(null)
                .autoDismiss(false)
                .customView(mIdLayout, false)
                .positiveText(R.string.accept)
                .positiveColorRes(R.color.primaryATM)
                .show();
    }

    public static MaterialDialog showCustomDialog(String Title, Context mContext, int mIdLayout){
        return new MaterialDialog.Builder(mContext)
                .title(Title)
                .autoDismiss(false)
                .customView(mIdLayout, false)
                .positiveText(R.string.accept)
                .positiveColorRes(R.color.primaryATM)
                .show();
    }

    public static MaterialDialog showCustomDialog2(Context mContext, int mIdLayout){
        return new MaterialDialog.Builder(mContext)
                .autoDismiss(false)
                .customView(mIdLayout, false)
                //.positiveText(R.string.accept)
                //.positiveColorRes(R.color.primary360)
                .show();
    }



    public static MaterialDialog showListDialog(String mTitle, int mArray, Context mContext){
        return new MaterialDialog.Builder(mContext)
                .title(mTitle)
                .items(mArray)
                .show();
    }

    public static MaterialDialog showListDialog2(String mTitle, Collection mArray, Context mContext){
        return new MaterialDialog.Builder(mContext)
                .title(mTitle)
                .items(mArray)
                .show();
    }

    public static MaterialDialog showContentDialog(String mTitle, Spanned content, Context mContext){
        return new MaterialDialog.Builder(mContext)
                .title(mTitle)
                .content(content)
                .positiveText(R.string.accept)
                .positiveColorRes(R.color.primaryATM)
                .show();
    }

    public static MaterialDialog showContentDialog(String mTitle, String content, Context mContext){
        return new MaterialDialog.Builder(mContext)
                .title(mTitle)
                .content(content)
                .positiveText(R.string.accept)
                .positiveColorRes(R.color.primaryATM)
                .show();
    }

    public static MaterialDialog showInputDialog(String mTitle, String content, int textHint, int minLenght, int maxLenght,
                                                 int inputType, Context mContext){
        return new MaterialDialog.Builder(mContext)
                .title(mTitle)
                .content(content)
                .input(textHint, textHint, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                    }
                })
                .inputRange(minLenght, maxLenght)
                .inputType(inputType)
                .positiveText(R.string.accept)
                .positiveColorRes(R.color.primaryATM)
                .show();
    }

}
