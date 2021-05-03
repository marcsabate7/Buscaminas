package com.marc.buscaminas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Configuration extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private ImageButton backToMain;
    private CheckBox checkBoxTimer;
    private Button startGame;
    private EditText userName;
    private RadioGroup radioGropuNumeroParrilla, radioGroupBombsPercentage;
    private RadioButton btnParrilla, btnBombs;
    private Integer num_graella;
    private float percentatge_de_bombes;
    private Intent intentToGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);

        backToMain = (ImageButton) findViewById(R.id.ConfigBackToMain);
        startGame = (Button) findViewById(R.id.EmpezarDesdeConfig);
        userName = findViewById(R.id.EditText_username);
        checkBoxTimer = findViewById(R.id.checkBox);
        intentToGame = new Intent(this, Partida.class);

        radioGropuNumeroParrilla = findViewById(R.id.RadioGroupGraella);
        radioGropuNumeroParrilla.setOnCheckedChangeListener(this);
        radioGroupBombsPercentage = findViewById(R.id.RadioGroupBombs);
        radioGroupBombsPercentage.setOnCheckedChangeListener(this);

        startGame.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.EmpezarDesdeConfig:

                DadesDePartida dataReady = new DadesDePartida();
                int idButtonParrilla = radioGropuNumeroParrilla.getCheckedRadioButtonId();
                int idButtonBombs = radioGroupBombsPercentage.getCheckedRadioButtonId();

                btnParrilla = (RadioButton) findViewById(idButtonParrilla);
                dataReady.setNumero_graella(Integer.parseInt(btnParrilla.getText().toString()));

                btnBombs = (RadioButton) findViewById(idButtonBombs);
                float percentage = Float.parseFloat(btnBombs.getText().toString() + "f") / 100f;
                dataReady.setPercentatge(percentage);


                if (checkBoxTimer.isChecked())
                    dataReady.setHave_timer(true);

                intentToGame.putExtra("userName",userName.getText().toString());
                intentToGame.putExtra("DadesDePartida", dataReady);
                startActivity(intentToGame);
                break;
            case R.id.ConfigBackToMain:
                this.finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
    }


}

