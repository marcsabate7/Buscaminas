package com.marc.buscaminas;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Configuration extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private CheckBox checkBoxTimer;
    private Button startGame;
    private EditText userName;
    private RadioGroup radioGroupNumeroParrilla, radioGroupBombsPercentage;
    private RadioButton btnParrilla, btnBombs;
    private Intent intentToGame, intentToService;
    private Bundle bundle;
    private MediaPlayer boomSound;
    public final static int RESTARTGAME = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);

        bundle = new Bundle();
        if(getIntent().getStringExtra("Music").equals("ON")) {
            intentToService = new Intent(this, SoundTrack.class);
            bundle.putString("start","start");
            intentToService.putExtras(bundle);
            startService(intentToService);
        }

        startGame = (Button) findViewById(R.id.EmpezarDesdeConfig);
        userName = findViewById(R.id.EditText_username);
        checkBoxTimer = findViewById(R.id.checkBox);
        intentToGame = new Intent(this, Partida.class);

        radioGroupNumeroParrilla = findViewById(R.id.RadioGroupGraella);
        radioGroupNumeroParrilla.setOnCheckedChangeListener(this);
        radioGroupBombsPercentage = findViewById(R.id.RadioGroupBombs);
        radioGroupBombsPercentage.setOnCheckedChangeListener(this);

        boomSound = MediaPlayer.create(this, R.raw.boomsound);
        startGame.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.quitMessageConfirmation)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setResult(MainActivity.CLOSE_ALL);
                        finish();
                    }
                }).create().show();
    }


    @Override
    public void onClick(View view) {
        Integer idButtonBombs = radioGroupBombsPercentage.getCheckedRadioButtonId();
        Integer idButtonParrilla = radioGroupNumeroParrilla.getCheckedRadioButtonId();
        switch (view.getId()) {
            case R.id.EmpezarDesdeConfig:

                if (userName.getText().toString().trim().equalsIgnoreCase("")) {
                    userName.setError("This field can not be blank");
                } else if (idButtonParrilla == -1) {
                    RadioButton radioButton = (RadioButton) findViewById(R.id.lastRadioButton);
                    radioButton.setError("An option must be selected");
                } else {
                    intentToGame.putExtra("userName", userName.getText().toString());

                    DadesDePartida dataReady = new DadesDePartida();

                    btnParrilla = (RadioButton) findViewById(idButtonParrilla);
                    dataReady.setNumero_graella(Integer.parseInt(btnParrilla.getText().toString()));

                    btnBombs = (RadioButton) findViewById(idButtonBombs);
                    float percentage = Float.parseFloat(btnBombs.getText().toString() + "f") / 100f;
                    dataReady.setPercentatge(percentage);

                    if (checkBoxTimer.isChecked())
                        dataReady.setHave_timer(true);

                    intentToGame.putExtra("DadesDePartida", dataReady);

                    boomSound.start();
                    startActivityForResult(intentToGame,2);
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode==MainActivity.CLOSE_ALL) {
            setResult(MainActivity.CLOSE_ALL);
            finish();
        }else if(resultCode == RESTARTGAME){
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
    }

}

