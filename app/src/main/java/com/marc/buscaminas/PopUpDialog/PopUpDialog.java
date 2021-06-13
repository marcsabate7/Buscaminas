package com.marc.buscaminas.PopUpDialog;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

import com.marc.buscaminas.Game.PartidaActivity;

public abstract class PopUpDialog {

    protected Context context;
    protected LayoutInflater layoutInflater;
    protected AlertDialog.Builder builder;


    public PopUpDialog(Context context, LayoutInflater layoutInflater){
        this.context = context;
        this.layoutInflater = layoutInflater;
        builder = new AlertDialog.Builder(this.context);
    }

    public abstract void configurePopUp();
    public void show (){
        builder.create().show();
    }

}
