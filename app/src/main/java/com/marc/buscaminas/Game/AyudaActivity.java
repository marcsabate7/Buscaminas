package com.marc.buscaminas.Game;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.marc.buscaminas.R;

import org.w3c.dom.Text;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AyudaActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ayuda_activity);

        Button backToMain = (Button) findViewById(R.id.backToMain);
        TextView ayuda_title = (TextView) findViewById(R.id.ayuda_title);

        SpannableString mitextoU = new SpannableString("Bienvenido al juego del BUSCA MINAS!!");
        mitextoU.setSpan(new UnderlineSpan(), 0, mitextoU.length(), 0);
        ayuda_title.setText(mitextoU);

        backToMain.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backToMain) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
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
