package com.marc.buscaminas;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Configuration extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private CheckBox checkBoxTimer;
    private Button startGame;
    private EditText userName;
    private RadioGroup radioGroupNumeroParrilla, radioGroupBombsPercentage;
    private RadioButton btnParrilla, btnBombs;
    private Intent intentToGame, intentToService;
    private Bundle bundle;
    private MediaPlayer boomSound;
    private Spinner timespinner;
    private ArrayAdapter<String> spinAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);


        bundle = new Bundle();
        if(getIntent().getExtras()==null);
        else if(getIntent().getStringExtra("Music").equals("ON")) {
            intentToService = new Intent(this, SoundTrack.class);
            bundle.putString("start", "start");
            intentToService.putExtras(bundle);
            startService(intentToService);
        }

        startGame = (Button) findViewById(R.id.EmpezarDesdeConfig);
        userName = findViewById(R.id.EditText_username);
        checkBoxTimer = findViewById(R.id.checkBox);
        intentToGame = new Intent(this, Partida.class);
        timespinner = (Spinner) findViewById(R.id.tiemposspiner);
        List<String> str = Arrays.asList(getResources().getStringArray(R.array.TimespinnerChoices));
        spinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,str);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timespinner.setAdapter(spinAdapter);
        timespinner.setOnItemSelectedListener(new InfoSpinner());


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
                    userName.setError("Completa el nombre para empezar la partida!");
                    Toast.makeText(this,"Campo nombre vacio, completalo para empezar la partida!",Toast.LENGTH_SHORT).show();
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
                    intentToGame.putExtra("time_value",timespinner.getSelectedItem().toString());
                    Toast.makeText(this,timespinner.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();

                    boomSound.start();
                    intentToGame.addFlags(FLAG_ACTIVITY_NEW_TASK|FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentToGame);
                }
                break;

        }
    }

    private class InfoSpinner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> spinner, View selectedView, int selectedIndex, long id) {
            Toast.makeText(getApplicationContext(),""+ spinner.getItemAtPosition(selectedIndex).toString()+" selected",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
    }

}

