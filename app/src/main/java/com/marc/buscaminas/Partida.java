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

import java.util.List;

public class Partida extends AppCompatActivity {

    private Intent receivedIntent;
    private DadesDePartida receivedData;
    private GridView graella;
    private List<Button> cells;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partida);

        receivedIntent = getIntent();
        receivedData = receivedIntent.getExtras().getParcelable("DadesDePartida");

        ////////VIDEO
        graella = (GridView) findViewById(R.id.gridviewID);
        CustomAdapter gridAdapter = new CustomAdapter();
        graella.setAdapter(gridAdapter);
        graella.setNumColumns(receivedData.getNumero_graella());



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
            View view1 = getLayoutInflater().inflate(R.layout.partida,null);
             = view1.findViewById(R.id.cellButton);


        }
    }



}