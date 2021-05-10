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
import java.util.Date;

public class FinalActivity extends AppCompatActivity implements OnClickListener {
    private Button btn_email, btn_nova_partida, btn_salir;
    TextView status;
    TextView diayhora;
    TextView text_log;
    TextView text_email;
    String log;
    String partida_status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        btn_email = (Button) findViewById(R.id.buttonEnviarEmail);
        btn_nova_partida = (Button) findViewById(R.id.buttonNovaPartida);
        btn_salir = (Button) findViewById(R.id.buttonSortir);

        btn_salir.setOnClickListener(this);
        btn_email.setOnClickListener(this);
        btn_nova_partida.setOnClickListener(this);


        Intent intent = getIntent();
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

        Date myDate = new Date();
        status.setText(partida_status);
        diayhora.setText(new SimpleDateFormat("dd-MM-yyyy-h-m").format(myDate));
        text_log.setText(log);
        text_email.setText("marc-saba@hotmail.com");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonEnviarEmail:
                Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
                String correu = text_email.getText().toString();
                String[] TO = {correu};
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO); // * configurar email aquí!
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Log partida buscaminas");
                emailIntent.putExtra(Intent.EXTRA_TEXT, partida_status + "\n" + log);
                startActivity(emailIntent);
                break;
            case R.id.buttonNovaPartida:
                setResult(Configuration.RESTARTGAME);
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
}