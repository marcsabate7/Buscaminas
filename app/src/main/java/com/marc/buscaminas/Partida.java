package com.marc.buscaminas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class Partida extends AppCompatActivity {

    private Intent receivedIntent;
    private DadesDePartida receivedData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partida);

        receivedIntent = getIntent();
        receivedData = receivedIntent.getExtras().getParcelable("DadesDePartida");

        Toast.makeText(this,String.valueOf(receivedData.getNumero_graella()),Toast.LENGTH_SHORT).show();
        Toast.makeText(this,String.valueOf(receivedData.getPercentatge()),Toast.LENGTH_SHORT).show();

        TextView user = (TextView) findViewById(R.id.userNameEnLaPartida);
        user.setText(receivedIntent.getExtras().getString("userName"));


    }
}