package com.marc.buscaminas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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


public class Partida extends AppCompatActivity {
    private Intent receivedIntent;
    private int[][] matrix;
    private int[] drawableOfNumbers;
    private DadesDePartida receivedData;
    private List<Integer> listOfBombsIndexes;
    private int numberOfcolumns;
    private Intent toStopService;
    private GridView graella;
    long tiempo_restante = 25000;
    float percentage_bombs;
    String user_name;
    private Intent toActivityFinal;
    int num_cells;
    TextView num_casillas;
    TextView timer;
    TextView titol_partida;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partida);

        num_casillas = (TextView) findViewById(R.id.casillasid);
        timer = (TextView) findViewById(R.id.timer);
        titol_partida = (TextView) findViewById(R.id.textViewPartidaMarxa);

        // RESTORE SAVEINSTANCE STATE
        if (savedInstanceState != null) {
            //num_cells = savedInstanceState.getInt("casillas_restantes");
            //tiempo_restante = savedInstanceState.getLong("tiempo_restante");
        }

        toStopService = new Intent(this, SoundTrack.class);
        drawableOfNumbers = initialize_drawableOfNumbers();
        receivedIntent = getIntent();
        receivedData = receivedIntent.getExtras().getParcelable("DadesDePartida");
        numberOfcolumns = receivedData.getNumero_graella();
        percentage_bombs = receivedData.getPercentatge();
        listOfBombsIndexes = bombs_index_list(numberOfcolumns, percentage_bombs);
        matrix = initialize_matrix(numberOfcolumns, listOfBombsIndexes);

        user_name = receivedIntent.getStringExtra("userName");
        titol_partida.setText("PARTIDA EN MARXA, " + user_name.toUpperCase() + "!!");

        graella = (GridView) findViewById(R.id.gridview);
        CustomAdapter gridAdapter = new CustomAdapter(this, numberOfcolumns * numberOfcolumns);
        graella.setAdapter(gridAdapter);
        graella.setNumColumns(numberOfcolumns);
        num_cells = (numberOfcolumns * numberOfcolumns) - listOfBombsIndexes.size();
        num_casillas.setText("Casillas por descubrir: " + num_cells);

        if (receivedData.isHave_timer()) {
            //Toast.makeText(getApplicationContext(),"EL TIMER ESTA ACTIVAT", Toast.LENGTH_LONG).show();
            new CountDownTimer(tiempo_restante, 1000) {
                @Override
                public void onTick(long l) {
                    tiempo_restante = l;
                    if (receivedData.isHave_timer()) {
                        timer.setText("Segundos restantes: " + l / 1000);
                        timer.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    } else {
                        timer.setText("Segundos restantes: " + l / 1000);
                        timer.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));

                    }
                }

                @Override
                public void onFinish() {
                    // PARTIDA PERDUDA PER TEMPS
                    timer.setText("GAME OVER");
                    int status_partida = 1;
                    changeActivityToFinal(status_partida, 0);

                }
            }.start();
        } else {
            timer.setText("No hay tiempo!");
            timer.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
        }

        if (receivedData.isHave_timer()) {
            num_casillas.setText("Casillas por descubrir: " + num_cells);
            num_casillas.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        } else {
            num_casillas.setText("Casillas por descubrir: " + num_cells);
            num_casillas.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
        }

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
                        view.setBackgroundResource(R.drawable.ic_bomb2);
                        // PARTIDA PERDUDA PERQUE HA CLICAT A UNA BOMBA
                        timer.setText("GAME OVER");
                        changeActivityToFinal(2, position);

                    } else {
                        num_cells--;
                        if (receivedData.isHave_timer()) {
                            num_casillas.setText("Casillas por descubrir: " + num_cells);
                            num_casillas.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                        } else {
                            num_casillas.setText("Casillas por descubrir: " + num_cells);
                            num_casillas.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
                        }
                        if (num_cells == 0) {
                            // PARTIDA GUANYADA
                            changeActivityToFinal(3, 0);
                        }
                        int counter = numberSurroundingBombs(matrix, position);
                        view.setBackgroundResource(drawableOfNumbers[counter]);
                    }
                }
            });

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


    // AQUESTA FUNCIÓ S'UTILITZA PER A PASAR LES DADES DE LA PARTIDA AL INTENT I AQUEST CAP A L'ACTIVITY FINAL
    public void changeActivityToFinal(int status_partida, int position) {
        stopService(toStopService);

        toActivityFinal = new Intent(this, FinalActivity.class);
        toActivityFinal.putExtra("user_name", user_name);
        toActivityFinal.putExtra("casillas_totales", numberOfcolumns * numberOfcolumns);
        toActivityFinal.putExtra("porcentage_minas_escogidas", percentage_bombs);
        int num_minas = (int) ((numberOfcolumns * numberOfcolumns) * percentage_bombs);
        toActivityFinal.putExtra("total_minas", num_minas);
        // CAMBIAR TIEMPO EMPLADO QUAN FEM QUE EL USUARI INTRODUEIXI EL TEMPS
        toActivityFinal.putExtra("tiempo_total", 25000 - tiempo_restante);


        if (status_partida == 1) {           // Estatus == 1 per a partides on s'acabe el temps
            toActivityFinal.putExtra("partida_status", "Ha perdido la partida porque se ha agotado el tiempo...!!, Te han quedado " + num_cells + " casillas por descubrir");
            toActivityFinal.putExtra("casillas_restantes", num_cells);
        }
        if (status_partida == 2) {           // Estatus == 2 per a partides on s'ha clicat a una bomba
            int position_x = position / numberOfcolumns;
            int position_y = position % numberOfcolumns;
            toActivityFinal.putExtra("partida_status", "Has perdido!! Bomba en casilla " + position_x + ", " + position_y + ".\n" + "Te han quedado " + num_cells + " casillas por descubrir!!");
            toActivityFinal.putExtra("casillas_restantes", num_cells);
        }
        if (status_partida == 3) {           // Estatus == 3 per a partides guanyades
            toActivityFinal.putExtra("casillas_restantes", num_cells);
            if (receivedData.isHave_timer()) {
                toActivityFinal.putExtra("partida_status", "Has ganado!! Y te han sobrado " + (25000 - tiempo_restante) + " segundos!");
            } else {
                toActivityFinal.putExtra("partida_status", "Has ganado!! Sin control de tiempo!!");
            }
            toActivityFinal.putExtra("casillas_restantes", num_cells);
        }
        startActivityForResult(toActivityFinal, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.CLOSE_ALL) {
            setResult(MainActivity.CLOSE_ALL);
        } else if (resultCode == Configuration.RESTARTGAME ) {
            setResult(Configuration.RESTARTGAME);
        }
        finish();
        System.exit(0);
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

    public int[][] initialize_matrix(int numberOfcolumns, List<Integer> listOfBombs) {
        int[][] matrix = new int[numberOfcolumns][numberOfcolumns];
        for (int x = 0; x < listOfBombs.size(); x++) {
            Integer current = listOfBombs.get(x);
            int i = current / numberOfcolumns;
            int j = current % numberOfcolumns;
            matrix[i][j] = 1;
        }
        for (int i = 0; i < numberOfcolumns; i++) {
            for (int j = 0; j < numberOfcolumns; j++) {
                if (matrix[i][j] != 1)
                    matrix[i][j] = 0;
            }
        }
        return matrix;
    }

    public int numberSurroundingBombs(int[][] matrix, int position) {
        int counter = 0, position_i = position / numberOfcolumns, position_j = position % numberOfcolumns;
        if (isPosValid(position_i + 1, position_j, matrix.length) && matrix[position_i + 1][position_j] == 1)
            counter++;
        if (isPosValid(position_i - 1, position_j, matrix.length) && matrix[position_i - 1][position_j] == 1)
            counter++;
        if (isPosValid(position_i, position_j + 1, matrix.length) && matrix[position_i][position_j + 1] == 1)
            counter++;
        if (isPosValid(position_i, position_j - 1, matrix.length) && matrix[position_i][position_j - 1] == 1)
            counter++;
        if (isPosValid(position_i + 1, position_j + 1, matrix.length) && matrix[position_i + 1][position_j + 1] == 1)
            counter++;
        if (isPosValid(position_i + 1, position_j - 1, matrix.length) && matrix[position_i + 1][position_j - 1] == 1)
            counter++;
        if (isPosValid(position_i - 1, position_j - 1, matrix.length) && matrix[position_i - 1][position_j - 1] == 1)
            counter++;
        if (isPosValid(position_i - 1, position_j + 1, matrix.length) && matrix[position_i - 1][position_j + 1] == 1)
            counter++;
        return counter;
    }

    public boolean isPosValid(int x, int y, int side_length) {
        if (x < 0 || y < 0 || x >= side_length || y >= side_length)
            return false;
        return true;
    }

    public int[] initialize_drawableOfNumbers() {
        return new int[]{R.drawable.zero, R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four,
                R.drawable.five, R.drawable.six, R.drawable.seven, R.drawable.eight};
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong("tiempo_restante", tiempo_restante);
        outState.putInt("casillas_restantes", num_cells);
    }
}
