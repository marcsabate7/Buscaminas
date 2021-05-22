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
    private Intent intentToGame, intentToService, receivedIntent;
    private Bundle bundle;
    private MediaPlayer boomSound;
    private Spinner timespinner;
    private ArrayAdapter<String> spinAdapter;
    private DadesDePartida receivedDadesDePartida;
    private int receivedNumGraella, currentNumGraella;
    private float receivedPercentatge, currentPercentatge;
    private String receivedUser, receivedTime, currentUserName, currentTime;
    private boolean receivedHaveTimer, currentHaveTimer;
    private boolean music_on;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);

        receivedIntent = getIntent();
        intentToService = new Intent(this, SoundTrack.class);
        bundle = new Bundle();


        startGame = (Button) findViewById(R.id.EmpezarDesdeConfig);
        userName = findViewById(R.id.EditText_username);
        checkBoxTimer = findViewById(R.id.checkBox);
        intentToGame = new Intent(this, Partida.class);
        timespinner = (Spinner) findViewById(R.id.tiemposspiner);
        List<String> str = Arrays.asList(getResources().getStringArray(R.array.TimespinnerChoices));
        spinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, str);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timespinner.setAdapter(spinAdapter);
        timespinner.setOnItemSelectedListener(new InfoSpinner());


        radioGroupNumeroParrilla = findViewById(R.id.RadioGroupGraella);
        radioGroupNumeroParrilla.setOnCheckedChangeListener(this);
        radioGroupBombsPercentage = findViewById(R.id.RadioGroupBombs);
        radioGroupBombsPercentage.setOnCheckedChangeListener(this);


        if (receivedIntent.getExtras() != null) {
            if (receivedIntent.getStringExtra("Music")!=null && receivedIntent.getStringExtra("Music").equals("ON")) {
                bundle.putString("start", "start");
                intentToService.putExtras(bundle);
                intentToGame.putExtra("Music","ON");
                music_on = true;
            } else if (receivedIntent.getStringExtra("Music")!=null &&!receivedIntent.getStringExtra("Music").equals("ON"));
            else {
                if(receivedIntent.getExtras().getString("ReceivedMusic")!=null) {
                    Toast.makeText(getApplicationContext(), "rebo music on a final", Toast.LENGTH_SHORT).show();
                    intentToService.putExtra("start", "start");
                    intentToGame.putExtra("Music","ON");
                    music_on = true;
                }

                receivedDadesDePartida = receivedIntent.getExtras().getParcelable("DadesDePartida");
                receivedUser = receivedDadesDePartida.getUserName();
                userName.setText(receivedUser);
                receivedNumGraella = receivedDadesDePartida.getNumero_graella();
                receivedHaveTimer = receivedDadesDePartida.isHave_timer();
                checkBoxTimer.setChecked(receivedHaveTimer);

            }
        }

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
                    Toast.makeText(this, "Campo nombre vacio, completalo para empezar la partida!", Toast.LENGTH_SHORT).show();
                } else if (idButtonParrilla == -1) {
                    RadioButton radioButton = (RadioButton) findViewById(R.id.lastRadioButton);
                    radioButton.setError("An option must be selected");
                } else {

                    btnParrilla = (RadioButton) findViewById(idButtonParrilla);
                    btnBombs = (RadioButton) findViewById(idButtonBombs);

                    currentUserName = userName.getText().toString();
                    currentNumGraella = Integer.parseInt(btnParrilla.getText().toString());
                    currentPercentatge = Float.parseFloat(btnBombs.getText().toString() + "f") / 100f;
                    currentHaveTimer = checkBoxTimer.isChecked();
                    currentTime = timespinner.getSelectedItem().toString();
                    DadesDePartida dataReady = new DadesDePartida(currentUserName, currentNumGraella, currentPercentatge,
                            currentHaveTimer, currentTime);

                    intentToGame.putExtra("DadesDePartida", dataReady);
                    boomSound.start();
                    intentToGame.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentToGame);
                }
                break;

        }
    }

    private class InfoSpinner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> spinner, View selectedView, int selectedIndex, long id) {
           // Toast.makeText(getApplicationContext(),""+ spinner.getItemAtPosition(selectedIndex).toString()+" selected",
                  //  Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (music_on){
            stopService(intentToService);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(music_on)
            startService(intentToService);
    }
}


