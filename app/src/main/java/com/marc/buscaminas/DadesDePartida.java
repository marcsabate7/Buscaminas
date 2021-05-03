package com.marc.buscaminas;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DadesDePartida implements Parcelable {

    private int numero_graella;
    private float percentatge;
    private boolean have_timer;

    public DadesDePartida(int numero_graella, float percentatge, boolean have_timer){
        this.numero_graella = numero_graella;
        this.percentatge = percentatge;
        this.have_timer = have_timer;
    }
    public DadesDePartida(){
        this.numero_graella = 7;
        this.percentatge = 0.25f;
        this.have_timer = false;
    }

    public void setNumero_graella(int numero_graella){
        this.numero_graella = numero_graella;
    }
    public void setPercentatge(float percentatge){
        this.percentatge = percentatge;
    }
    public void setHave_timer(boolean have_timer){
        this.have_timer = have_timer;
    }

    public int getNumero_graella(){
        return this.numero_graella;
    }
    public float getPercentatge(){
        return this.percentatge;
    }
    public boolean isHave_timer(){
        return this.have_timer;
    }

    protected DadesDePartida(Parcel in) {
        numero_graella = in.readInt();
        percentatge = in.readFloat();
        have_timer = in.readByte() != 0;
    }

    public static final Creator<DadesDePartida> CREATOR = new Creator<DadesDePartida>() {
        @Override
        public DadesDePartida createFromParcel(Parcel in) {
            return new DadesDePartida(in);
        }

        @Override
        public DadesDePartida[] newArray(int size) {
            return new DadesDePartida[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(numero_graella);
        parcel.writeFloat(percentatge);
        parcel.writeByte((byte) (have_timer ? 1 : 0));
    }
}
