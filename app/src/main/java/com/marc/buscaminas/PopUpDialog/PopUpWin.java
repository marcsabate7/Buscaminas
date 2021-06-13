package com.marc.buscaminas.PopUpDialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.marc.buscaminas.R;

public class PopUpWin extends PopUpDialog {

    public PopUpWin(Context context, LayoutInflater layoutInflater){
        super(context, layoutInflater);
    }

    @Override
    public void configurePopUp() {
        LayoutInflater inflater = PopUpWin.super.layoutInflater;
        View view = inflater.inflate(R.layout.popupwin, null);
        super.builder.setView(view);
        super.builder.setCancelable(false);
    }
}
