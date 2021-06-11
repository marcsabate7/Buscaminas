package com.marc.buscaminas.AuxiliarStructures;

import android.os.Parcel;
import android.os.Parcelable;

public class Datalog implements Parcelable {

    private final DadesDePartida dadesDePartida;
    private final int coordX, coordY;
    private long tiempo_restante;


    public Datalog(DadesDePartida dadesDePartida, int coordX, int coordY,long time){
        this.dadesDePartida = dadesDePartida;
        this.coordX = coordX;
        this.coordY = coordY;
        this.tiempo_restante = time;
    }

    public DadesDePartida getDadesDePartida(){
        return this.dadesDePartida;
    }
    public int getCoordX(){
        return this.coordX;
    }
    public int getCoordY(){
        return this.coordY;
    }
    public long getTiempo_restante(){return this.tiempo_restante;}

    protected Datalog(Parcel in) {
        dadesDePartida = in.readParcelable(getClass().getClassLoader());
        coordX = in.readInt();
        coordY = in.readInt();
    }

    public static final Creator<Datalog> CREATOR = new Creator<Datalog>() {
        @Override
        public Datalog createFromParcel(Parcel in) {
            return new Datalog(in);
        }

        @Override
        public Datalog[] newArray(int size) {
            return new Datalog[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(dadesDePartida,0);
        parcel.writeInt(coordX);
        parcel.writeInt(coordY);
    }
}
