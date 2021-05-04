package com.marc.buscaminas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent toAyuda, toConfiguration;
    public final static int CLOSE_ALL = 10;

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
                startActivityForResult(toConfiguration,1);
                break;
            case R.id.buttonsalir:
                exitAppCLICK(v);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==CLOSE_ALL) {
            finish();
            System.exit(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void exitAppCLICK (View view) {
        finishAffinity();
        System.exit(0);
    }
}