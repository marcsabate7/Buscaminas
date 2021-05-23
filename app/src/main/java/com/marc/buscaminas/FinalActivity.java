package com.marc.buscaminas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.view.View.OnClickListener;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FinalActivity extends AppCompatActivity implements OnClickListener {
    private DadesDePartida dadesDePartida;
    private Intent intent, toConfig;
    TextView status;
    TextView diayhora;
    TextView text_log;
    TextView text_email;
    String log;
    String partida_status;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);


        Button btn_email = (Button) findViewById(R.id.buttonEnviarEmail);
        Button btn_nova_partida = (Button) findViewById(R.id.buttonNovaPartida);
        Button btn_salir = (Button) findViewById(R.id.buttonSortir);

        btn_salir.setOnClickListener(this);
        btn_email.setOnClickListener(this);
        btn_nova_partida.setOnClickListener(this);

        toConfig = (new Intent(this, Configuration.class));
        intent = getIntent();

        if (intent.getStringExtra("ReceivedMusic") != null) {//&& intent.getStringExtra("ReceivedMusic").equals("ON")) {
            if (intent.getStringExtra("ReceivedMusic").equals("ON")) {
                Toast.makeText(getApplicationContext(), "rebo music on a final", Toast.LENGTH_SHORT).show();
                toConfig.putExtra("ReceivedMusic","ON");
            }
        }


        dadesDePartida = intent.getExtras().getParcelable("DadesDePartida");
        String name_user = intent.getStringExtra("user_name");
        int total_casillas = intent.getIntExtra("casillas_totales", 1);
        float porcentage_minas = intent.getFloatExtra("porcentage_minas_escogidas", 1);
        int num_minas = intent.getIntExtra("total_minas", 1);
        partida_status = intent.getStringExtra("partida_status");

        status = (TextView) findViewById(R.id.textViewStatus);
        diayhora = (TextView) findViewById(R.id.editTextTextdiayhora);
        text_log = (TextView) findViewById(R.id.editTextTextlog);
        text_email = (TextView) findViewById(R.id.editTextTextEmail);

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
                toConfig.putExtra("DadesDePartida", dadesDePartida);
                toConfig.putExtra("from final", "from final");
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