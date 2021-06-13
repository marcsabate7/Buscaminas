package com.marc.buscaminas.PopUpDialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.marc.buscaminas.R;

public class PopUpTimeOut extends PopUpDialog{

    public PopUpTimeOut(Context context, LayoutInflater layoutInflater){
        super(context, layoutInflater);

    }

    @Override
    public void configurePopUp() {
        LayoutInflater inflater = PopUpTimeOut.super.layoutInflater;
        View view = inflater.inflate(R.layout.popuptimeloss, null);
        super.builder.setView(view);
        super.builder.setCancelable(false);
    }
}
