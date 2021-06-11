package com.marc.buscaminas.Game;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.marc.buscaminas.Game.Configuration;
import com.marc.buscaminas.R;
import com.marc.buscaminas.Game.AyudaActivity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Aquesta activity té dues maneres d'avançar, cap a Ajuda i cap a la configuració de Partida.
     * El background de l'activitat s'ha dissenyat amb AdobeXD, un programa bàsic de disseny que podem traslladar a la layout.
     * S'ha implementat de més un Switch per a habilitar la música de fons durant el joc.
     * Mitjançant els flags fem espai a la memòria eliminant l'activity del BackStack.
     * <p>
     * En quasi totes les activitats s'ha implementat la funció onBackPressed per a confirmar la sortida de l'aplicació
     * per a millorar l'experiència d'usuari i no marxar de l'aplicació per un error en tocar la pantalla.
     */

    private Intent toAyuda, toConfiguration;
    public static String  OFF, ON, MUSIC;
    private Switch switchMusic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MUSIC = getResources().getString(R.string.Music);
        ON = getResources().getString(R.string.On);
        OFF = getResources().getString(R.string.Off);

        Button btnAyuda = (Button) findViewById(R.id.buttonAyuda);
        Button btnEmpezar = (Button) findViewById(R.id.buttonIniciar);
        Button btnSalir = (Button) findViewById(R.id.buttonsalir);
        switchMusic = (Switch) findViewById(R.id.switchMusic);

        switchMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        btnAyuda.setOnClickListener(this);
        btnEmpezar.setOnClickListener(this);
        btnSalir.setOnClickListener(this);

        toAyuda = new Intent(this, AyudaActivity.class);
        toConfiguration = new Intent(this, Configuration.class);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonAyuda:
                startActivity(toAyuda);
                finish();
                break;
            case R.id.buttonIniciar:
                if (switchMusic.isChecked())
                    toConfiguration.putExtra(MUSIC, ON);
                else
                    toConfiguration.putExtra(MUSIC, OFF);
                toConfiguration.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toConfiguration);
                finish();
                break;
            case R.id.buttonsalir:
                exitAppCLICK(v);
                break;
        }
    }

    public void exitAppCLICK(View view) {
        finishAffinity();
        System.exit(0);
    }
}