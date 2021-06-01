package com.marc.buscaminas;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
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
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.service.controls.actions.CommandAction;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
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
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.graphics.Color.TRANSPARENT;


public class Partida extends AppCompatActivity implements GridFrag.CellListener {

    /**
     * De més, s'ha implementat l'opció que l'usuari col·loqui banderes on cregui que hi ha una bomba. També s'ha configurat
     * la partida de manera que quan s'acabi el joc, es mostrin les posicions de les bombes perquè l'usuari no es quedi amb l'intriga.
     * Un cop acabada la partida, es deshabiliten els botons de la graella per evitar que es repeteixin accions que només s'han de dur
     * a terme un cop i així evitar actuacions d'error de l'aplicació. Com a transicions s'han incorporat uns Dialogs per donar feedback
     * a l'usuari i millorar la seva experiència.
     */

    private Intent receivedIntent, toActivityFinal;
    private int[][] matrix;
    private int[] drawableOfNumbers;
    private DadesDePartida receivedData;
    private ArrayList<Integer> listOfBombsIndexes;
    private HashMap<Integer, ImageButton> copyofviews = new HashMap<>();
    private int numberOfcolumns;
    private Intent toStopService;
    private GridView graella;
    private CustomAdapter gridAdapter;
    long tiempo_restante;
    float percentage_bombs;
    String user_name, timeString;
    int num_cells;
    CountDownTimer time;
    TextView num_casillas, timer, titol_partida;

    private int[] list_orientation, array_caught, list_of_flags, flags_caught;
    private boolean is_change_orientation, havetimer, have_music;


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

        // Intent que passarem cap activity final
        toActivityFinal = new Intent(this, FinalActivity.class);
        if (receivedIntent.getStringExtra("Music") != null && receivedIntent.getStringExtra("Music").equals("ON")) {
            have_music = true;
            toActivityFinal.putExtra("ReceivedMusic", "ON");
        }

        receivedData = receivedIntent.getExtras().getParcelable("DadesDePartida");
        user_name = receivedData.getUserName();
        numberOfcolumns = receivedData.getNumero_graella();
        percentage_bombs = receivedData.getPercentatge();
        havetimer = receivedData.isHave_timer();
        if ((havetimer = receivedData.isHave_timer()))
            timeString = receivedData.getTime();

        listOfBombsIndexes = bombs_index_list(numberOfcolumns, percentage_bombs);

        // Partida text en negreta
        SpannableString mitextoU = new SpannableString("PARTIDA EN MARXA, " + user_name.toUpperCase() + "!!");
        mitextoU.setSpan(new UnderlineSpan(), 0, mitextoU.length(), 0);
        titol_partida.setText(mitextoU);

        // Tractament de les flags
        list_orientation = new int[numberOfcolumns * numberOfcolumns];
        list_of_flags = new int[numberOfcolumns * numberOfcolumns];
        for (int i = 0; i < list_orientation.length; i++) {
            list_orientation[i] = -1;
            list_of_flags[i] = -1;
        }

        // Inicialització del grid view
        graella = (GridView) findViewById(R.id.gridview);
        gridAdapter = new CustomAdapter(this, numberOfcolumns * numberOfcolumns);
        graella.setAdapter(gridAdapter);
        graella.setNumColumns(numberOfcolumns);
        num_cells = (numberOfcolumns * numberOfcolumns) - listOfBombsIndexes.size();
        num_casillas.setText("Casillas por descubrir: " + num_cells);

        // OnSavedInstanceState per si el usuari gira la pantalla recuperarem les dades de la partida pertinents
        if (savedInstanceState != null) {
            num_cells = savedInstanceState.getInt("casillas_restantes");
            tiempo_restante = savedInstanceState.getLong("tiempo_restante");
            array_caught = savedInstanceState.getIntArray("array_orientation");
            listOfBombsIndexes = savedInstanceState.getIntegerArrayList("list_bombs");
            flags_caught = savedInstanceState.getIntArray("flags_posades");

            is_change_orientation = true;
        }
        // Controlem l'orientació de la pantalla ja que ens es util en una funció implementada al final del codi
        if (is_change_orientation == false && havetimer)
            tiempo_restante = timechoice(timeString);

        matrix = initialize_matrix(numberOfcolumns, listOfBombsIndexes);


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

        if (havetimer) {
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

            if (is_change_orientation) {
                if (array_caught[position] != -1) {
                    cell.setBackgroundResource(drawableOfNumbers[array_caught[position]]);
                    list_orientation[position] = array_caught[position];

                }
                if (flags_caught[position] == 0) {
                    list_of_flags[position] = 0;
                    cell.setBackgroundResource(R.drawable.blueflag);
                }
            }

            cell.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if (view.getBackground().getConstantState().equals(getDrawable(R.drawable.blueflag).getConstantState())) {
                        if (list_orientation[position] != -1) {
                            view.setBackgroundResource(drawableOfNumbers[list_orientation[position]]);
                        } else
                            view.setBackground(defaultbackgrond);
                        list_of_flags[position] = -1;
                    } else {
                        view.setBackgroundResource(R.drawable.blueflag);
                        list_of_flags[position] = 0;
                    }

                    if (havetimer) {
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
                    System.out.println("\n" + position + "\n");
                    if (listOfBombsIndexes.contains(position)) {
                        view.setBackgroundResource(R.drawable.ic_bomb2);
                        timer.setText("GAME OVER");
                        view.setEnabled(false);
                        view.setClickable(false);
                        changeActivityToFinal(2, position);

                    } else {

                        if (list_orientation[position] == -1) {
                            num_cells--;
                        }

                        if (havetimer) {

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

            copyofviews.put(position, cell);
            return view;
        }

        @Override
        public int getCount() {
            return this.numberOfCells;
        }

        @Override
        public Object getItem(int i) {
            return copyofviews.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }
    @Override
    public void onCasillaSeleccionada(Datalog datalog) {
        LogFrag logFrag = (LogFrag) getSupportFragmentManager().findFragmentById(R.id.fraglog);
        boolean hayLog = (logFrag != null && logFrag.isInLayout());

        if (hayLog) {
            logFrag.mostrarDetalle(datalog);
        }
    }


    // Aquesta funció s'utilitza per pasar les dades de la partida al intent i aquest cap al activity final amb un status que controlara si la aprtida s'ha guanyat / per
    // Falta bloquejar el grid view quan s'ensenyen les bombes per a que el usuari no pugui clicar a cap item de la graella
    @SuppressLint("WrongConstant")
    public void changeActivityToFinal(int status_partida, int position) {
        stopService(toStopService);


        if (time != null) {
            time.cancel();
        }

        toActivityFinal.putExtra("user_name", user_name);
        toActivityFinal.putExtra("casillas_totales", numberOfcolumns * numberOfcolumns);
        toActivityFinal.putExtra("porcentage_minas_escogidas", percentage_bombs);
        int num_minas = (int) ((numberOfcolumns * numberOfcolumns) * percentage_bombs);
        toActivityFinal.putExtra("total_minas", num_minas);
        toActivityFinal.putExtra("tiempo_total", (tiempo_restante) / 1000);


        final Handler handler2 = new Handler();
        Timer tt = new Timer();
        tt.schedule(new TimerTask() {
            public void run() {
                handler2.post(new Runnable() {
                    public void run() {
                        for (int i = 0; i < copyofviews.size(); i++) {
                            copyofviews.get(i).setClickable(false);
                            copyofviews.get(i).setEnabled(false);
                        }
                        for (int i = 0; i < listOfBombsIndexes.size(); i++) {
                            copyofviews.get(listOfBombsIndexes.get(i)).setBackgroundResource(R.drawable.ic_bomb2);
                        }
                        //LA PRIMERA POSICIÓ DEL GRIDVIEW NO REACCIONA, NO FA CAS DE CAP DE LES CRIDES SUPERIORS
                        //NO SABEM ON FALLA PERÒ ES DETECTA COM A BOMBA QUAN S'HA DE DETECTAR PERÒ SEMBLA QUE ACTUI INDEPENDENTMENT DE LA RESTA
                        // Toast.makeText(getApplicationContext(),listOfBombsIndexes.toString(),Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(),String.valueOf(copyofviews.containsKey(0)),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 250);

        // Estatus == 1 per a partides on s'acabe el temps
        if (status_partida == 1) {
            timer.setText("GAME OVER");
            MediaPlayer game_over_sound = MediaPlayer.create(this, R.raw.gameover);
            game_over_sound.start();
            delayPopups(5000, status_partida);

            toActivityFinal.putExtra("partida_status", "Ha perdido la partida porque se ha agotado el tiempo...!!, Te han quedado " + num_cells + " casillas por descubrir");
            toActivityFinal.putExtra("casillas_restantes", num_cells);
        }
        // Estatus == 2 per a partides on s'ha clicat a una bomba
        if (status_partida == 2) {
            MediaPlayer boom = MediaPlayer.create(this, R.raw.boomsound);
            boom.start();
            delayPopups(5000, status_partida);

            int position_x = position / numberOfcolumns;
            int position_y = position % numberOfcolumns;
            toActivityFinal.putExtra("partida_status", "Has perdido!! Bomba en casilla " + position_x + ", " + position_y + ".\n" + "Te han quedado " + num_cells + " casillas por descubrir!!");
            toActivityFinal.putExtra("casillas_restantes", num_cells);

        }
        // Estatus == 3 per a partides guanyades
        if (status_partida == 3) {

            MediaPlayer victory = MediaPlayer.create(this, R.raw.victory);
            victory.start();
            delayPopups(5000, status_partida);

            toActivityFinal.putExtra("casillas_restantes", num_cells);
            if (receivedData.isHave_timer()) {
                toActivityFinal.putExtra("partida_status", "Has ganado!! Y te han sobrado " + (tiempo_restante) / 1000 + " segundos!");
            } else {
                toActivityFinal.putExtra("partida_status", "Has ganado!! Sin control de tiempo!!");
            }
            toActivityFinal.putExtra("casillas_restantes", num_cells);
        }

        toActivityFinal.putExtra("DadesDePartida", receivedData);
        final Handler handler = new Handler();
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        // Afegim flags al intent per controlar les activitats obertes
                        toActivityFinal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(toActivityFinal);
                        finish();
                    }
                });
            }
        }, 6000);
    }

    // Funció que mostra un dialog si volem tirar cap a radere
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

    // Temps elegit pel usuari
    public long timechoice(String received) {
        long result = 0;
        switch (received) {
            case "Fácil - 150s":
                result = 150000;
                break;
            case "Medio - 80s":
                result = 80000;
                break;
            case "Duro - 60s":
                result = 60000;
                break;
            case "Leyenda - 40s":
                result = 40000;
                break;
        }
        return result;
    }

    // Colocació de les bombes de manera aleatoria
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

    // Inicialització de la matriu si hi va una bomba ficarem un 1 del contrari un 0
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

    // Controlem el numero de bombes que envolten una casella
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

    // Array que cada posició conté el drawable pertinent ho fem aixi per comoditat
    public int[] initialize_drawableOfNumbers() {
        return new int[]{R.drawable.zero, R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four,
                R.drawable.five, R.drawable.six, R.drawable.seven, R.drawable.eight};
    }

    // PopUp's que es mostren quan una partida s'acabe per X motius, mirar noms de les funcions per saber a quin pertany
    public void showpopupTimeLoss() {
        AlertDialog.Builder popupTimeLoss = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.popuptimeloss, null);
        popupTimeLoss.setView(view);
        popupTimeLoss.setCancelable(false);
        popupTimeLoss.create().show();
    }

    public void showpopupWin() {
        AlertDialog.Builder popupWin = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.popupwin, null);
        popupWin.setView(view);
        popupWin.setCancelable(false);
        popupWin.create().show();
    }

    public void showpopupBomb() {
        AlertDialog.Builder popupBomb = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.popupbomb, null);
        popupBomb.setView(view);
        popupBomb.setCancelable(false);
        popupBomb.create().show();
    }

    public boolean istablet() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        if (width > 1023 || height > 1023) {
            return true;
        } else {
            return false;
        }
    }

    // Funcio que realitza una espera abans de mostrar els Popup's i que despres fa la crida
    // Opció 1 per Timeloss, opció 2 per bomba clicada, opció 3 per victory
    public void delayPopups(int milliseconds, int option) {
        final Handler handler = new Handler();
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        switch (option) {
                            case 1:
                                showpopupTimeLoss();
                                break;
                            case 2:
                                showpopupBomb();
                                break;
                            case 3:
                                showpopupWin();
                                break;
                        }
                    }
                });
            }
        }, milliseconds);

    }

    // Guardem les dades a recuperar al OnCreate
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (havetimer) {
            time.cancel();
        }
        outState.putLong("tiempo_restante", tiempo_restante);
        outState.putInt("casillas_restantes", num_cells);
        outState.putIntArray("array_orientation", list_orientation);
        outState.putIntegerArrayList("list_bombs", listOfBombsIndexes);
        outState.putIntArray("flags_posades", list_of_flags);
    }


    /* Metodes per parar musica i per continuar el timer, falte fixejar el tema de la musica ja que torna a comensar quan pausem i cridem al onRestart(),
    hem pensat utilitzar un broadcast Receiver que ens permetra pausar i reanudar falte implementar per la seguent entrega */

    @Override
    protected void onPause() {
        super.onPause();
        if (have_music) {
            stopService(toStopService);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Comprovar si sonido haurie de estar activat si ho esta engeggarlo
        if (have_music) {
            toStopService.putExtra("start", "start");
            startService(toStopService);
        }
        if (havetimer) {
            time = new CountDownTimer(tiempo_restante, 1000) {
                @Override
                public void onTick(long l) {
                    tiempo_restante = l;
                    if (havetimer) {
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

    }




}
