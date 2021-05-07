package com.marc.buscaminas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Partida extends AppCompatActivity implements View.OnClickListener {
    public static Button [] cells;
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
        cells = makeButtonArray(listOfBombsIndexes,numberOfcolumns*numberOfcolumns);

        graella = (GridView) findViewById(R.id.gridviewID);
        CustomAdapter gridAdapter = new CustomAdapter();
        graella.setAdapter(gridAdapter);
        graella.setNumColumns(numberOfcolumns);


    }



    public List<Integer> bombs_index_list(int numColumns, float percentage) {
        Random random = new Random();
        List<Integer> listOfIndexBomb = new ArrayList<>();

        int max_length = (int) ((numColumns*numColumns) *percentage);

        Toast.makeText(this,"Aixo hauria de ser 6 numeros i es "+percentage,Toast.LENGTH_SHORT).show();
        while (listOfIndexBomb.size() < max_length) {
            int thisOne = (int) (Math.random() * (0 - (numColumns * numColumns)) + (numColumns * numColumns));
            if(listOfIndexBomb.isEmpty() || !listOfIndexBomb.contains(thisOne))
                listOfIndexBomb.add(thisOne);
        }
        return listOfIndexBomb;
    }

    public Button [] makeButtonArray(List<Integer> bombs, int lengthofArray){
        Button [] buttonArray = new Button[lengthofArray];
        for(int i=0;i<buttonArray.length;i++){
            if(bombs.contains(i))
                buttonArray[i]=(Button)findViewById(R.id.b);
            else
                buttonArray[i]=(Button)findViewById(R.id.notabomb);
        }
        return buttonArray;
    }

    @Override
    public void onClick(View view) {

    }
    private class CustomAdapter extends BaseAdapter {
        private Context context;
        private Button cells;

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = getLayoutInflater().inflate(R.layout.row_data,null);
            Button button = view1.findViewById(R.id.buttoninGrid);

            return view1;
        }
    }
}
