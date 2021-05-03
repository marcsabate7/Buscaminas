package com.marc.buscaminas;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class Configuration extends AppCompatActivity {

    private ImageButton backToMain;
    private Button startGame;
    private EditText userName;
    private RadioGroup radioGropuNumeroParrilla, radioGroupBombsPercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);

        backToMain = (ImageButton) findViewById(R.id.ConfigBackToMain);
        startGame = (Button) findViewById(R.id.EmpezarDesdeConfig);
        userName = findViewById(R.id.EditText_username);
        radioGropuNumeroParrilla = findViewById(R.id.RadioGroupGraella);
        radioGroupBombsPercentage = findViewById(R.id.RadioGroupBombs);

    }

}

