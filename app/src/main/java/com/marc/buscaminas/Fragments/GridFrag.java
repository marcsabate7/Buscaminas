package com.marc.buscaminas.Fragments;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marc.buscaminas.AuxiliarStructures.Datalog;
import com.marc.buscaminas.R;

public class GridFrag extends Fragment {


    private CellListener listener;

    public GridFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            return inflater.inflate(R.layout.gridfrag2, container, false);
        }
        return inflater.inflate(R.layout.grid_frag, container, false);
    }

    public interface CellListener {
        void onCasillaSeleccionada(Datalog datalog);
    }

    /*public void setCellListener(CellListener listener) {
        this.listener=listener;
    }*/
}