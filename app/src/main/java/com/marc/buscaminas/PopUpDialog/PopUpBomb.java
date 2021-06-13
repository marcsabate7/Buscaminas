package com.marc.buscaminas.PopUpDialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.marc.buscaminas.R;

public class PopUpBomb extends PopUpDialog{

    public PopUpBomb(Context context, LayoutInflater layoutInflater){
        super(context, layoutInflater);

    }
    @Override
    public void configurePopUp() {
        LayoutInflater inflater = PopUpBomb.super.layoutInflater;
        View view = inflater.inflate(R.layout.popupbomb, null);
        super.builder.setView(view);
        super.builder.setCancelable(false);
    }
}
