package com.marc.buscaminas.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marc.buscaminas.Structure.Datalog;
import com.marc.buscaminas.R;

public class LogFrag extends Fragment {

    private TextView data;
    private TextView caselles;
    String messageDades;
    String casselles = "";
    public LogFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            casselles = savedInstanceState.getString("caselles_seleccionades");
            messageDades = savedInstanceState.getString("data_log");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.frag_log, container, false);

    }
    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        data = (TextView) getView().findViewById(R.id.textviewdadeslog);
        caselles = (TextView) getView().findViewById(R.id.casillaSeleccionada);
        caselles.setText(casselles);
        data.setText(messageDades);
    }
    public void mostrarDetalle(Datalog datalog){
        messageDades = "ALIAS: "+datalog.getDadesDePartida().getUserName();
        messageDades += " NUMERO CASILLAS: "+datalog.getDadesDePartida().getNumero_graella();
        messageDades += " MINAS: "+datalog.getDadesDePartida().getPercentatge()+"%";
        if(datalog.getDadesDePartida().isHave_timer()) {
            messageDades += " TIEMPO: " + datalog.getDadesDePartida().getTime();
        } else{
            messageDades += " TIEMPO: No hay tiempo";
        }
        data.setText(messageDades);

        if(datalog.getDadesDePartida().isHave_timer()) {
            casselles = caselles.getText().toString() + "\nCasilla Seleccionada = ("+datalog.getCoordX()+","+datalog.getCoordY()+")" + " - Time: " +datalog.getTiempo_restante()/1000 + "s";
            caselles.setText(casselles);
        } else{
            casselles = caselles.getText().toString() + "\nCasilla Seleccionada = ("+datalog.getCoordX()+","+datalog.getCoordY()+")" + " - Time: No hay tiempo";
            caselles.setText(casselles);
        }
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("caselles_seleccionades",caselles.getText().toString());
        outState.putString("data_log",data.getText().toString());
    }
}