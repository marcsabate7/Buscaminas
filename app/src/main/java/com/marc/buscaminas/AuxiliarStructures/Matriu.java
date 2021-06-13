package com.marc.buscaminas.AuxiliarStructures;

import android.os.Parcel;
import android.os.Parcelable;

import com.marc.buscaminas.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Matriu implements Parcelable {

    private List<Integer> bombs_index_list;
    private int [][] matrix;
    private int numColumns;
    private float percentatge;

    public Matriu(int numColumns, float percentatge){
        this.numColumns = numColumns;
        this.percentatge = percentatge;
        bombs_index_list = create_bombs_index_list(this.numColumns, this.percentatge);
        this.matrix = initialize_matrix(numColumns, bombs_index_list);
    }


    protected Matriu(Parcel in) {
        numColumns = in.readInt();
        percentatge = in.readFloat();
        bombs_index_list = in.readArrayList(Integer.class.getClassLoader());
        matrix = (int[][]) in.readArray(Integer.class.getClassLoader());
    }

    public static final Creator<Matriu> CREATOR = new Creator<Matriu>() {
        @Override
        public Matriu createFromParcel(Parcel in) {
            return new Matriu(in);
        }

        @Override
        public Matriu[] newArray(int size) {
            return new Matriu[size];
        }
    };

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

    public List<Integer> create_bombs_index_list(int numColumns, float percentage) {
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


    public boolean isPosValid(int x, int y, int side_length) {
        if (x < 0 || y < 0 || x >= side_length || y >= side_length)
            return false;
        return true;
    }

    public int[] initialize_drawableOfNumbers() {
        return new int[]{R.drawable.zero, R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four,
                R.drawable.five, R.drawable.six, R.drawable.seven, R.drawable.eight};
    }
    public int numberSurroundingBombs(int[][] matrix, int position) {
        int counter = 0, position_i = position / numColumns, position_j = position % numColumns;
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

    public int[][] getMatrix(){
        return this.matrix;
    }

    public List<Integer> getBombs_index_list(){
        return this.bombs_index_list;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(numColumns);
        parcel.writeFloat(percentatge);
        parcel.writeArray(matrix);
        parcel.writeList(bombs_index_list);
    }
}
