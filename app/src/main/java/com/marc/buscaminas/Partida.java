package com.marc.buscaminas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Partida extends AppCompatActivity{
    private Intent receivedIntent;
    private DadesDePartida receivedData;
    private List<Integer> listOfBombsIndexes;
    private GridView graella;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partida);

        receivedIntent = getIntent();
        receivedData = receivedIntent.getExtras().getParcelable("DadesDePartida");
        int numberOfcolumns = receivedData.getNumero_graella();
        listOfBombsIndexes = bombs_index_list(numberOfcolumns, receivedData.getPercentatge());

        graella = (GridView) findViewById(R.id.gridview);
        CustomAdapter gridAdapter = new CustomAdapter(this, numberOfcolumns * numberOfcolumns);
        graella.setAdapter(gridAdapter);
        graella.setNumColumns(numberOfcolumns);


        //A PARTIR D'AQUESTA PART DEL ONCREATE HE MIRAT DE IMPLEMENTAR UN LISTENER A LA GRAELLA PERO NOSE PQ POLLES NO VA, NO REACCIONA, SEMBLA QUE NO HI ENTRI
        // ELS TOASTS SON PER COMPROVAR PERO NO FA NI UN NI L'ALTRE NI PUTA IDEA BRO
        graella.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getApplicationContext(),"I HAVE BEEN CLICKED with pos:"+position,Toast.LENGTH_SHORT).show();
                if(listOfBombsIndexes.contains(position)){
                    Toast.makeText(getApplicationContext(),"I GOT A BOMB",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //CREEM LLISTA RANDOM DE 0 FINS A LLARGADA MAXIMA SENSE REPETIR PER PODER SITUAR LES BOMBES ENTRE LES POSICIONS DELS BOTONS EN LA GRAELLA
    public List<Integer> bombs_index_list(int numColumns, float percentage) {
        Random random = new Random();
        List<Integer> listOfIndexBomb = new ArrayList<>();

        int max_length = (int) ((numColumns * numColumns) * percentage);

        while (listOfIndexBomb.size() < max_length) {
            int thisOne = (int) (Math.random() * (0 - (numColumns * numColumns)) + (numColumns * numColumns));
            if (listOfIndexBomb.isEmpty() || !listOfIndexBomb.contains(thisOne))
                listOfIndexBomb.add(thisOne);
        }
        return listOfIndexBomb;
    }


    private class CustomAdapter extends BaseAdapter {
        private Context context;
        private ImageButton cell;
        private int numberOfCells;
        private LayoutInflater inflter;

        public CustomAdapter(Context c, int numberOfCells) {
            this.context = c;
            inflter = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.numberOfCells = numberOfCells;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflter.inflate(R.layout.row_data, null);
            }
            cell = (ImageButton) view.findViewById(R.id.buttoninGrid);
            cell.setScaleType(ImageView.ScaleType.FIT_XY);

            //AIXO ESTA COMENTAT PQ LA FOTO Q TE PASSAT ABANS ESTAVA FETA AMB AIXO, PER MIRAR SI HO FEIA BÉ I ARA VOLIA COMPROVAR SI REACCIONAVA AL CLICK LA GRAELLA
            //TE DONO PER A IDEA QUE NO M'HA DONAT TEMPS A MI, MIRAR DE FICAR EL LISTENER AQUÍ AL METODE GETVIEW PER ALS QUE SIGUIN BOMBES FER SETBACKGROUND

            /*if (listOfBombsIndexes.contains(position)) {
                cell.setBackgroundResource(R.drawable.ic_bomb);
            }

             */
            return view;
        }

        @Override
        public int getCount() {
            return this.numberOfCells;
        }
        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }

    //HE AFEGIT ESTA FUNCIO COM A L'ACTIVITY CONFIGURATION PER TIRAR ENRERE I MATAR L'APLICACIÓ AMB EL SEU ALERT DIALOG
    //BASICAMENT AIXO SERIA TOT, HE ESTAT PROVAN VARIES COSES PERO NO HE AVANSAT MOLT

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
