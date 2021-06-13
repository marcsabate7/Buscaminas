package com.marc.buscaminas.Game;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.view.View.OnClickListener;
import android.widget.Toast;

import com.marc.buscaminas.R;
import com.marc.buscaminas.AuxiliarStructures.DadesDePartida;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FinalActivity extends AppCompatActivity implements OnClickListener {
    private DadesDePartida dadesDePartida;
    private Intent intent, toConfig;
    private TextView status, diayhora, text_log, text_email;
    private String log, partida_status;
    public static String ON, DADES, PARTIDA_STATUS, RECEIVED_MUSIC, USER_NAME, CASILLAS_TOTALES, PORCENTAGE_MINAS_ELEGIDO, FROM_FINAL, TOTAL_MINAS;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        ON = getResources().getString(R.string.On);
        DADES = getResources().getString(R.string.DadesDePartida);
        PARTIDA_STATUS = getResources().getString(R.string.PartidaStatus);
        RECEIVED_MUSIC = getResources().getString(R.string.ReceivedMusic);
        USER_NAME = getResources().getString(R.string.UserNameKEY);
        CASILLAS_TOTALES = getResources().getString(R.string.CasillasTotales);
        PORCENTAGE_MINAS_ELEGIDO = getResources().getString(R.string.PercentatgeEscollitMines);
        FROM_FINAL = getResources().getString(R.string.fromFinal);
        TOTAL_MINAS = getResources().getString(R.string.TotalMinas);

        Button btn_email = (Button) findViewById(R.id.buttonEnviarEmail);
        Button btn_nova_partida = (Button) findViewById(R.id.buttonNovaPartida);
        Button btn_salir = (Button) findViewById(R.id.buttonSortir);

        btn_salir.setOnClickListener(this);
        btn_email.setOnClickListener(this);
        btn_nova_partida.setOnClickListener(this);

        toConfig = (new Intent(this, ConfigurationActivity.class));
        intent = getIntent();

        if (intent.getStringExtra(RECEIVED_MUSIC) != null) {
            if (intent.getStringExtra(RECEIVED_MUSIC).equals(ON)) {
                toConfig.putExtra(RECEIVED_MUSIC, ON);
            }
        }


        dadesDePartida = intent.getExtras().getParcelable(DADES);
        String name_user = intent.getStringExtra(USER_NAME);
        int total_casillas = intent.getIntExtra(CASILLAS_TOTALES, 1);
        float porcentage_minas = intent.getFloatExtra(PORCENTAGE_MINAS_ELEGIDO, 1);
        int num_minas = intent.getIntExtra(TOTAL_MINAS, 1);
        partida_status = intent.getStringExtra(PARTIDA_STATUS);

        status = (TextView) findViewById(R.id.results_partida_user);
        diayhora = (TextView) findViewById(R.id.text_dia_hora);
        text_log = (TextView) findViewById(R.id.log_user);
        text_email = (TextView) findViewById(R.id.text_hint_email);

        log = "Alias: " + name_user + " / " + "Casillas: " + total_casillas + " / " + "Porcentage minas: " + porcentage_minas + " / " + "Num minas: " + num_minas;

        status.setText(partida_status);
        @SuppressLint("SimpleDateFormat") String fecha = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        fecha = "Fecha: " + fecha;
        @SuppressLint("SimpleDateFormat") String fecha2 = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        fecha2 = "Hora: " + fecha2;
        diayhora.setText(fecha + " / " + fecha2);
        text_log.setText(log);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonEnviarEmail:
                if (text_email.getText().toString().trim().equalsIgnoreCase("")) {
                    text_email.setError("This field can not be blank");
                    Toast.makeText(this, "Campo email vacio, completalo para enviar email!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
                    String correu = text_email.getText().toString();
                    String[] TO = {correu};
                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, TO); // * configurar email aquí!
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Log partida buscaminas");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, partida_status + "\n" + log);
                    startActivity(emailIntent);
                }
                break;
            case R.id.buttonNovaPartida:
                toConfig.putExtra(DADES, dadesDePartida);
                toConfig.putExtra(FROM_FINAL, FROM_FINAL);
                startActivity(toConfig);
                finish();
                break;
            case R.id.buttonSortir:
                onBackPressed();
                break;

        }

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.quitMessageConfirmation)
                .setNegativeButton(R.string.no, (dialogInterface, i) -> {
                })
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> finish()).create().show();
    }
}