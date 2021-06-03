package com.marc.buscaminas.Structure;

import android.os.Parcel;
import android.os.Parcelable;

public class DadesDePartida implements Parcelable {
    /**
     * Classe alternativa per a guardar les dades de partida com a conjunt i sigui més fàcil treballar amb elles.
     */

    private final int numero_graella;
    private final float percentatge;
    private final boolean have_timer;
    private final String time;
    private final String userName;

    public DadesDePartida(String userName,int numero_graella, float percentatge, boolean have_timer, String time){
        this.userName = userName;
        this.numero_graella = numero_graella;
        this.percentatge = percentatge;
        this.have_timer = have_timer;
        this.time = time;
    }
    public DadesDePartida(){
        this.userName = null;
        this.numero_graella = 7;
        this.percentatge = 0.25f;
        this.have_timer = false;
        this.time = null;
    }
    /*
    public void setNumero_graella(int numero_graella){
        this.numero_graella = numero_graella;
    }
    public void setPercentatge(float percentatge){
        this.percentatge = percentatge;
    }
    public void setHave_timer(boolean have_timer){
        this.have_timer = have_timer;
    }

     */

    public int getNumero_graella(){
        return this.numero_graella;
    }
    public float getPercentatge(){
        return this.percentatge;
    }
    public boolean isHave_timer(){
        return this.have_timer;
    }
    public String getTime(){return this.time;}
    public String getUserName(){return this.userName;}

    protected DadesDePartida(Parcel in) {
        userName = in.readString();
        numero_graella = in.readInt();
        percentatge = in.readFloat();
        have_timer = in.readByte() != 0;
        time = in.readString();
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
        parcel.writeString(userName);
        parcel.writeInt(numero_graella);
        parcel.writeFloat(percentatge);
        parcel.writeByte((byte) (have_timer ? 1 : 0));
        parcel.writeString(time);
    }
}
