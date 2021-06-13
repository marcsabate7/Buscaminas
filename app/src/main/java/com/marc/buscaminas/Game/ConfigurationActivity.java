package com.marc.buscaminas.Game;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.marc.buscaminas.Music.SoundTrackService;
import com.marc.buscaminas.R;
import com.marc.buscaminas.AuxiliarStructures.DadesDePartida;

import java.util.Arrays;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ConfigurationActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {


    /**
     * Aquesta activity consisteix en els paràmetres de la partida.
     * De més, s'ha implementat un Spinner per seleccionar el temps que es vol escollir segons la dificultat.
     * Depenent de l'intent que es rebi, sabem si es tractarà de la primera partida o una nova partida, on guardarem algunes de les
     * dades per a que s'auto completin. Hem tingut alguns problemes amb guardar les dades dels RadioButtons i de la informació del Spinner
     * Procurarem que estigui implementat en la següent entrega. El conjunt  de les dades de Partida és guarden en una classe ALternativa
     * que hem dissenyat per guardar-les com  un sol conjunt i tenir més facilitat alhora de treballar amb elles entre diferents Activities.
     */
    public static String DADES, MUSIC, ON, RECEIVED_MUSIC, START;
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
    private int currentNumGraella;
    private float currentPercentatge;
    private String receivedUser, currentUserName, currentTime;
    private boolean receivedHaveTimer, currentHaveTimer, music_on;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);

        DADES = getResources().getString(R.string.DadesDePartida);
        MUSIC = getResources().getString(R.string.Music);
        ON = getResources().getString(R.string.On);
        RECEIVED_MUSIC = getResources().getString(R.string.ReceivedMusic);
        START = getResources().getString(R.string.start);

        receivedIntent = getIntent();
        intentToService = new Intent(this, SoundTrackService.class);
        bundle = new Bundle();


        startGame = (Button) findViewById(R.id.button_empezar_partida);
        userName = findViewById(R.id.username);
        checkBoxTimer = findViewById(R.id.checkBox__time);
        intentToGame = new Intent(this, PartidaActivity.class);
        timespinner = (Spinner) findViewById(R.id.tiemposspiner);
        List<String> str = Arrays.asList(getResources().getStringArray(R.array.TimespinnerChoices));
        spinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, str);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timespinner.setAdapter(spinAdapter);
        timespinner.setOnItemSelectedListener(new InfoSpinner());


        TextView title_configuration = (TextView) findViewById(R.id.title_configuration);
        radioGroupNumeroParrilla = findViewById(R.id.RadioGroupGraella);
        radioGroupNumeroParrilla.setOnCheckedChangeListener(this);
        radioGroupBombsPercentage = findViewById(R.id.RadioGroupBombs);
        radioGroupBombsPercentage.setOnCheckedChangeListener(this);

        SpannableString mitextoU = new SpannableString("CONFIGURACIÓN");
        mitextoU.setSpan(new UnderlineSpan(), 0, mitextoU.length(), 0);
        title_configuration.setText(mitextoU);


        if (receivedIntent.getExtras() != null) {
            if (receivedIntent.getStringExtra(MUSIC) != null && receivedIntent.getStringExtra(MUSIC).equals(ON)) {
                bundle.putString(START, START);
                intentToService.putExtras(bundle);
                intentToGame.putExtra(MUSIC, ON);
                music_on = true;
            } else if (receivedIntent.getStringExtra(MUSIC) != null && !receivedIntent.getStringExtra(MUSIC).equals(ON))
                ;
            else {
                if (receivedIntent.getExtras().getString(RECEIVED_MUSIC) != null) {
                    intentToService.putExtra(START, START);
                    intentToGame.putExtra(MUSIC, ON);
                    music_on = true;
                }

                receivedDadesDePartida = receivedIntent.getExtras().getParcelable(DADES);
                receivedUser = receivedDadesDePartida.getUserName();
                userName.setText(receivedUser);
                receivedHaveTimer = receivedDadesDePartida.isHave_timer();
                checkBoxTimer.setChecked(receivedHaveTimer);

            }
        }

        boomSound = MediaPlayer.create(this, R.raw.boomsound);
        startGame.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Integer idButtonBombs = radioGroupBombsPercentage.getCheckedRadioButtonId();
        Integer idButtonParrilla = radioGroupNumeroParrilla.getCheckedRadioButtonId();
        switch (view.getId()) {
            case R.id.button_empezar_partida:

                if (userName.getText().toString().trim().equalsIgnoreCase("")) {
                    userName.setError("Completa el nombre para empezar la partida!");
                    Toast.makeText(this, "Campo nombre vacio, completalo para empezar la partida!", Toast.LENGTH_SHORT).show();
                } else if (idButtonParrilla == -1) {
                    RadioButton radioButton = (RadioButton) findViewById(R.id.RadioButton_5);
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

                    intentToGame.putExtra(DADES, dataReady);
                    boomSound.start();
                    intentToGame.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentToGame);
                }
                break;

        }
    }
    // Spiner time
    private class InfoSpinner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> spinner, View selectedView, int selectedIndex, long id) {
            if (checkBoxTimer.isChecked())
                Toast.makeText(getApplicationContext(), "" + spinner.getItemAtPosition(selectedIndex).toString() + " selected",
                        Toast.LENGTH_SHORT).show();

        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {}
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {}

    // AlertDialog if user want to exit the app
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

    // OnPause function
    @Override
    protected void onPause() {
        super.onPause();
        if (music_on) {
            stopService(intentToService);
        }
    }


    // OnResume function
    @Override
    protected void onResume() {
        super.onResume();
        if (music_on)
            startService(intentToService);
    }
}


