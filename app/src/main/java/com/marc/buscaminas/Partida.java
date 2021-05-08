package com.marc.buscaminas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    private int[][] matrix;
    private int [] drawableOfNumbers;
    private DadesDePartida receivedData;
    private List<Integer> listOfBombsIndexes;
    private int numberOfcolumns;
    private GridView graella;
    long timepo_restante = 25000;
    Intent activity_final;
    int num_cells;
    TextView num_casillas;
    TextView timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partida);

        num_casillas = (TextView) findViewById(R.id.casillasid);
        timer = (TextView) findViewById(R.id.timer);


        drawableOfNumbers = initialize_drawableOfNumbers();
        receivedIntent = getIntent();
        receivedData = receivedIntent.getExtras().getParcelable("DadesDePartida");
        numberOfcolumns = receivedData.getNumero_graella();
        listOfBombsIndexes = bombs_index_list(numberOfcolumns, receivedData.getPercentatge());
        matrix = initialize_matrix(numberOfcolumns,listOfBombsIndexes);

        if (receivedData.isHave_timer()){
            Toast.makeText(getApplicationContext(),"EL TIMER ESTA ACTIVAT", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),"EL TIMER ESTA DESACTIVAT", Toast.LENGTH_LONG).show();
        }

        graella = (GridView) findViewById(R.id.gridview);
        CustomAdapter gridAdapter = new CustomAdapter(this, numberOfcolumns * numberOfcolumns);
        graella.setAdapter(gridAdapter);
        graella.setNumColumns(numberOfcolumns);
        num_cells = (numberOfcolumns * numberOfcolumns) - listOfBombsIndexes.size();
        num_casillas.setText("Casillas por descubrir: "+num_cells);

        // RESTORE SAVEINSTANCE STATE
        if (savedInstanceState!= null){
            num_cells = savedInstanceState.getInt("casillas_restantes");
            timepo_restante = savedInstanceState.getInt("tiempo_restante");
            System.out.println(timepo_restante);
        }


        if (receivedData.isHave_timer()){
            num_casillas.setText("Casillas por descubrir: "+num_cells);
            num_casillas.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        }else{
            num_casillas.setText("Casillas por descubrir: "+num_cells);
            num_casillas.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
        }

        new CountDownTimer(timepo_restante,1000) {
            @Override
            public void onTick(long l) {
                timepo_restante = l;
                if (receivedData.isHave_timer()){
                    timer.setText("Segundos restantes: " + l / 1000);
                    timer.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                }else{
                    timer.setText("Segundos restantes: " + l / 1000);
                    timer.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));

                }
            }

            @Override
            public void onFinish() {
                if (receivedData.isHave_timer()){
                    // PARTIDA PERDUDA PER TEMPS, JA QUE S'HA ACABAT EL TEMPS
                    timer.setText("GAME OVER");
                    timer.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    activity_final = new Intent(getApplicationContext(),FinalActivity.class);
                    startActivityForResult(activity_final,10);
                }else{
                    timer.setText("-");
                    timer.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
                }
            }
        }.start();

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


            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listOfBombsIndexes.contains(position)) {
                        Toast.makeText(getApplicationContext(),"I AM A BOMB",Toast.LENGTH_SHORT).show();
                        view.setBackgroundResource(R.drawable.ic_bomb2);
                        // PARTIDA PERDUDA PERQUE HA CLICAT A UNA BOMBA

                        timer.setText("GAME OVER");
                        activity_final = new Intent(getApplicationContext(),FinalActivity.class);
                        startActivityForResult(activity_final,10);
                    }else{
                        num_cells--;
                        if (receivedData.isHave_timer()){
                            num_casillas.setText("Casillas por descubrir: "+num_cells);
                            num_casillas.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                        }else{
                            num_casillas.setText("Casillas por descubrir: "+num_cells);
                            num_casillas.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
                        }
                        if (num_cells == 0){
                            // PARTIDA GUANYADA - AGAFAR DADES I PASARLES AL SEGUENT INTENT
                            activity_final = new Intent(getApplicationContext(),FinalActivity.class);
                            startActivityForResult(activity_final,10);
                        }
                        int counter = numberSurroundingBombs(matrix,position);
                        view.setBackgroundResource(drawableOfNumbers[counter]);
                        Toast.makeText(getApplicationContext(),"I HAVE "+counter+" BOMBS SURROUNDING ME",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //AIXO ESTA COMENTAT PQ LA FOTO Q TE PASSAT ABANS ESTAVA FETA AMB AIXO, PER MIRAR SI HO FEIA BÉ I ARA VOLIA COMPROVAR SI REACCIONAVA AL CLICK LA GRAELLA
            //TE DONO PER A IDEA QUE NO M'HA DONAT TEMPS A MI, MIRAR DE FICAR EL LISTENER AQUÍ AL METODE GETVIEW PER ALS QUE SIGUIN BOMBES FER SETBACKGROUND

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



    //A PARTIR D'AQUÍ ÉS UNA PROVA D'IDEA PER PODER CONTAR EL NOMBRE DE BOMBES A LES CASELLES DEL VOLTANT

    public int [][] initialize_matrix(int numberOfcolumns,List<Integer> listOfBombs){
        int [][] matrix = new int[numberOfcolumns][numberOfcolumns];
        for(int x=0;x<listOfBombs.size();x++){
            Integer current = listOfBombs.get(x);
            int i = current/numberOfcolumns;
            int j = current % numberOfcolumns;
            matrix[i][j]=1;
        }
        for(int i=0;i<numberOfcolumns;i++){
            for(int j=0;j<numberOfcolumns;j++){
                if(matrix[i][j]!=1)
                    matrix[i][j]=0;
            }
        }
        return matrix;
    }

    public int numberSurroundingBombs(int [][] matrix, int position){
        int counter=0, position_i=position/numberOfcolumns, position_j=position % numberOfcolumns;
        if(isPosValid(position_i+1,position_j,matrix.length) && matrix[position_i+1][position_j]==1)
            counter++;
        if(isPosValid(position_i-1,position_j,matrix.length) && matrix[position_i-1][position_j]==1)
            counter++;
        if(isPosValid(position_i,position_j+1,matrix.length) && matrix[position_i][position_j+1]==1)
            counter++;
        if(isPosValid(position_i,position_j-1,matrix.length) && matrix[position_i][position_j-1]==1)
            counter++;
        if(isPosValid(position_i+1,position_j+1,matrix.length) && matrix[position_i+1][position_j+1]==1)
            counter++;
        if(isPosValid(position_i+1,position_j-1,matrix.length) && matrix[position_i+1][position_j-1]==1)
            counter++;
        if(isPosValid(position_i-1,position_j-1,matrix.length) && matrix[position_i-1][position_j-1]==1)
            counter++;
        if(isPosValid(position_i-1,position_j+1,matrix.length) && matrix[position_i-1][position_j+1]==1)
            counter++;
        return counter;
    }

    public boolean isPosValid(int x, int y, int side_length){
        if(x < 0 || y<0 || x>=side_length || y>=side_length)
            return false;
        return true;
    }

    public int[] initialize_drawableOfNumbers(){
        return new int[]{R.drawable.zero,R.drawable.one,R.drawable.two,R.drawable.three,R.drawable.four,
        R.drawable.five,R.drawable.six,R.drawable.seven,R.drawable.eight};
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong("tiempo_restante",timepo_restante);
        outState.putInt("casillas_restantes",num_cells);
    }
}
