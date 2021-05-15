package com.marc.buscaminas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.ContactsContract;
import android.service.controls.actions.CommandAction;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.graphics.Color.TRANSPARENT;


public class Partida extends AppCompatActivity {
    private Intent receivedIntent, toActivityFinal;
    private int[][] matrix;
    private int[] drawableOfNumbers;
    private DadesDePartida receivedData;
    private ArrayList<Integer> listOfBombsIndexes;
    private int numberOfcolumns;
    private Intent toStopService;
    private GridView graella;
    long tiempo_restante = 20000;
    float percentage_bombs;
    String user_name;
    int num_cells;
    CountDownTimer time;
    TextView num_casillas;
    TextView timer;
    TextView titol_partida;
    private int[] list_orientation, array_caught, list_of_flags, flags_caught;
    boolean is_change_orientation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partida);


        num_casillas = (TextView) findViewById(R.id.casillasid);
        timer = (TextView) findViewById(R.id.timer);
        titol_partida = (TextView) findViewById(R.id.textViewPartidaMarxa);
        drawableOfNumbers = initialize_drawableOfNumbers();




        toStopService = new Intent(this, SoundTrack.class);
        receivedIntent = getIntent();
        receivedData = receivedIntent.getExtras().getParcelable("DadesDePartida");
        numberOfcolumns = receivedData.getNumero_graella();
        percentage_bombs = receivedData.getPercentatge();
        listOfBombsIndexes = bombs_index_list(numberOfcolumns, percentage_bombs);
        user_name = receivedIntent.getStringExtra("userName");
        SpannableString mitextoU = new SpannableString("PARTIDA EN MARXA, " + user_name.toUpperCase() + "!!");
        mitextoU.setSpan(new UnderlineSpan(), 0, mitextoU.length(), 0);
        titol_partida.setText(mitextoU);

        graella = (GridView) findViewById(R.id.gridview);
        CustomAdapter gridAdapter = new CustomAdapter(this, numberOfcolumns * numberOfcolumns);
        graella.setAdapter(gridAdapter);
        graella.setNumColumns(numberOfcolumns);

        num_cells = (numberOfcolumns * numberOfcolumns) - listOfBombsIndexes.size();
        num_casillas.setText("Casillas por descubrir: " + num_cells);
        // INICIALITZACIÓ DEL ARRAY
        list_orientation = new int[numberOfcolumns*numberOfcolumns];
        list_of_flags = new int[numberOfcolumns*numberOfcolumns];
        for(int i = 0;i<list_orientation.length;i++){
            list_orientation[i] = -1;
            list_of_flags[i]=-1;
        }





        if (savedInstanceState != null) {
            num_cells = savedInstanceState.getInt("casillas_restantes");
            tiempo_restante = savedInstanceState.getLong("tiempo_restante");
            // A PARTIR D'AQUI ES EL QUE E AFEGIT ON A LES POSICIONS DEL ARRAY QUE HI HAGI UN NUMERO DIFERENT DE -1 HEM DE FERLI EL SET BACKGROUND
            array_caught = savedInstanceState.getIntArray("array_orientation");
            listOfBombsIndexes = savedInstanceState.getIntegerArrayList("list_bombs");
            flags_caught = savedInstanceState.getIntArray("flags_posades");

            is_change_orientation = true;
        }

        matrix = initialize_matrix(numberOfcolumns, listOfBombsIndexes);


        if (receivedData.isHave_timer()) {
            time = new CountDownTimer(tiempo_restante, 1000) {
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
        private Drawable defaultbackgrond;

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
            defaultbackgrond = cell.getBackground();

            if(is_change_orientation){
                if(array_caught[position]!=-1){
                    cell.setBackgroundResource(drawableOfNumbers[array_caught[position]]);
                    list_orientation[position] = array_caught[position];
                    notifyDataSetChanged();
                }
                if(flags_caught[position]==0){
                    list_of_flags[position]=0;
                    cell.setBackgroundResource(R.drawable.blueflag);
                }
            }
            cell.setScaleType(ImageView.ScaleType.FIT_XY);

            cell.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if(view.getBackground().getConstantState().equals(getDrawable(R.drawable.blueflag).getConstantState())){
                        Toast.makeText(getApplicationContext(), " I AM A FLAG ALREADY",Toast.LENGTH_SHORT).show();
                        if(list_orientation[position]!=-1) {
                            view.setBackgroundResource(drawableOfNumbers[list_orientation[position]]);
                        }
                        else
                            view.setBackground(defaultbackgrond);
                        list_of_flags[position]=-1;
                    }else {
                        view.setBackgroundResource(R.drawable.blueflag);
                        list_of_flags[position]=0;
                    }

                    if (receivedData.isHave_timer()) {
                        num_casillas.setText("Casillas por descubrir: " + num_cells);
                        num_casillas.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    } else {
                        num_casillas.setText("Casillas por descubrir: " + num_cells);
                        num_casillas.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
                    }
                    return true;
                }
            });

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("\n"+position+"\n");
                    if (listOfBombsIndexes.contains(position)) {
                        view.setBackgroundResource(R.drawable.ic_bomb2);
                        // PARTIDA PERDUDA PERQUE HA CLICAT A UNA BOMBA
                        timer.setText("GAME OVER");
                        changeActivityToFinal(2, position);


                    } else {

                        if(list_orientation[position]==-1) {
                            num_cells--;
                        }

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
                        list_orientation[position] = counter;
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

        if(receivedData.isHave_timer()) {
            //Toast.makeText(this, "entro al cancel", Toast.LENGTH_SHORT).show();
            time.cancel();
        }

        toActivityFinal = new Intent(this, FinalActivity.class);
        toActivityFinal.putExtra("user_name", user_name);
        toActivityFinal.putExtra("casillas_totales", numberOfcolumns * numberOfcolumns);
        toActivityFinal.putExtra("porcentage_minas_escogidas", percentage_bombs);
        int num_minas = (int) ((numberOfcolumns * numberOfcolumns) * percentage_bombs);
        toActivityFinal.putExtra("total_minas", num_minas);
        // CAMBIAR TIEMPO EMPLADO QUAN FEM QUE EL USUARI INTRODUEIXI EL TEMPS
        toActivityFinal.putExtra("tiempo_total", (tiempo_restante) / 1000);


        if (status_partida == 1) { // Estatus == 1 per a partides on s'acabe el temps
            showpopupTimeLoss();
            toActivityFinal.putExtra("partida_status", "Ha perdido la partida porque se ha agotado el tiempo...!!, Te han quedado " + num_cells + " casillas por descubrir");
            toActivityFinal.putExtra("casillas_restantes", num_cells);
        }
        if (status_partida == 2) {
            // Estatus == 2 per a partides on s'ha clicat a una bomba
            MediaPlayer boom = MediaPlayer.create(this,R.raw.boomsound);
            boom.start();
            showpopupBomb();
            int position_x = position / numberOfcolumns;
            int position_y = position % numberOfcolumns;
            toActivityFinal.putExtra("partida_status", "Has perdido!! Bomba en casilla " + position_x + ", " + position_y + ".\n" + "Te han quedado " + num_cells + " casillas por descubrir!!");
            toActivityFinal.putExtra("casillas_restantes", num_cells);
        }
        if (status_partida == 3) {
            // Estatus == 3 per a partides guanyades
            showpopupWin();
            toActivityFinal.putExtra("casillas_restantes", num_cells);
            if (receivedData.isHave_timer()) {
                toActivityFinal.putExtra("partida_status", "Has ganado!! Y te han sobrado " + (tiempo_restante) / 1000 + " segundos!");
            } else {
                toActivityFinal.putExtra("partida_status", "Has ganado!! Sin control de tiempo!!");
            }
            toActivityFinal.putExtra("casillas_restantes", num_cells);
        }


        final Handler handler = new Handler();
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        // MIRAR DE FER STARTACTIVITY NORMAL EN COMPTES DE FOR RESULT
                        toActivityFinal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        //Toast.makeText(getApplicationContext(),"Entrem handler",Toast.LENGTH_SHORT).show();
                        startActivity(toActivityFinal);
                        finish();
                    }
                });
            }
        }, 2000);
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

    public ArrayList<Integer> bombs_index_list(int numColumns, float percentage) {
        Random random = new Random();
        ArrayList<Integer> listOfIndexBomb = new ArrayList<>();

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

    public void showpopupTimeLoss() {
        AlertDialog.Builder popupTimeLoss = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.popuptimeloss, null);
        popupTimeLoss.setView(view);
        popupTimeLoss.create().show();
    }

    public void showpopupWin(){
        AlertDialog.Builder popupWin = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.popupwin, null);
        popupWin.setView(view);
        popupWin.create().show();
    }

    public void showpopupBomb(){
        AlertDialog.Builder popupBomb = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.popupbomb, null);
        popupBomb.setView(view);
        popupBomb.create().show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(receivedData.isHave_timer()) {
            //Toast.makeText(this, "entro al cancel al onsave", Toast.LENGTH_SHORT).show();
            time.cancel();
        }
        outState.putLong("tiempo_restante", tiempo_restante);
        outState.putInt("casillas_restantes", num_cells);
        outState.putIntArray("array_orientation", list_orientation);
        outState.putIntegerArrayList("list_bombs",listOfBombsIndexes);
        outState.putIntArray("flags_posades", list_of_flags);
    }
}
