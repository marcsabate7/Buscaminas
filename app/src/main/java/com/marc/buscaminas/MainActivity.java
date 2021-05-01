package com.marc.buscaminas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent toAyuda, toConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAyuda = (Button) findViewById(R.id.buttonAyuda);
        Button btnEmpezar = (Button) findViewById(R.id.buttonIniciar);
        Button btnSalir = (Button) findViewById(R.id.buttonsalir);

        btnAyuda.setOnClickListener(this);
        btnEmpezar.setOnClickListener(this);
        btnSalir.setOnClickListener(this);

        toAyuda = new Intent(this,AyudaActivity.class);
        toConfiguration = new Intent(this,Configuration.class);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonAyuda:
                startActivity(toAyuda);
                break;
            case R.id.buttonIniciar:
                startActivity(toConfiguration);
                break;
            case R.id.buttonsalir:
                exitAppCLICK(v);
                break;
        }
    }

    public void exitAppCLICK (View view) {
        finishAffinity();
        System.exit(0);
    }
}