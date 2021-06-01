package com.marc.buscaminas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LogFrag extends Fragment {

    private TextView data;
    private TextView caselles;
    public LogFrag() {
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
        return inflater.inflate(R.layout.frag_log, container, false);
    }
    public void mostrarDetalle(Datalog datalog){
        String messageDades;
        String casselles = "";

        messageDades = "ALIAS: "+datalog.getDadesDePartida().getUserName();
        messageDades += " NUMERO CASILLAS: "+datalog.getDadesDePartida().getNumero_graella();
        messageDades += " MINAS: "+datalog.getDadesDePartida().getPercentatge()+"%";
        messageDades += " HAY TIEMPO? "+datalog.getDadesDePartida().isHave_timer();
        messageDades += " TIEMPO: "+datalog.getDadesDePartida().getTime();
        data = (TextView) getView().findViewById(R.id.textviewdadeslog);
        data.setText(messageDades);

        casselles += "\nCasilla Seleccionada = ("+datalog.getCoordX()+","+datalog.getCoordY()+")";

    }
}