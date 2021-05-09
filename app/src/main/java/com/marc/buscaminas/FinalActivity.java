package com.marc.buscaminas;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.view.View.OnClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FinalActivity extends AppCompatActivity implements OnClickListener {
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


        Intent intent = getIntent();
        String name_user = intent.getStringExtra("user_name");
        int total_casillas = intent.getIntExtra("casillas_totales",1);
        float porcentage_minas = intent.getFloatExtra("porcentage_minas_escogidas",1);
        int num_minas = intent.getIntExtra("total_minas",1);
        partida_status = intent.getStringExtra("partida_status");

        status = (TextView)findViewById(R.id.textViewStatus);
        diayhora = (TextView)findViewById(R.id.editTextTextdiayhora);
        text_log = (TextView)findViewById(R.id.editTextTextlog);
        text_email = (TextView)findViewById(R.id.editTextTextEmail);

        log = "Alias: "+name_user+" / "+ "Casillas: "+total_casillas+" / "+ "Porcentage minas: "+porcentage_minas + " / "+ "Num minas: "+num_minas;

        Date myDate = new Date();
        status.setText(partida_status);
        diayhora.setText(new SimpleDateFormat("dd-MM-yyyy-h-m").format(myDate));
        text_log.setText(log);
        text_email.setText("marc-saba@hotmail.com");

        Button btn_email = (Button) findViewById(R.id.buttonEnviarEmail);
        Button btn_nova_partida = (Button) findViewById(R.id.buttonEnviarEmail);
        Button btn_salir = (Button) findViewById(R.id.buttonEnviarEmail);

        btn_email.setOnClickListener(this);
        btn_nova_partida.setOnClickListener(this);
        btn_salir.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonEnviarEmail:
                Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));

                String correu = text_email.getText().toString();
                String[] TO = {correu};
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO); // * configurar email aquí!
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Log partida buscaminas");
                emailIntent.putExtra(Intent.EXTRA_TEXT,partida_status+"\n"+ log);
                startActivity(emailIntent);
                break;
            case R.id.buttonNovaPartida:
                setResult(Configuration.RESTARTGAME);
                finish();
                System.exit(0);
                break;
            case R.id.buttonSortir:
                setResult(MainActivity.CLOSE_ALL);
                finish();
                System.exit(0);
                break;

        }
    }

    public void exitAppCLICK (View view) {
        finishAffinity();
        System.exit(0);
    }


}