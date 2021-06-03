package com.marc.buscaminas.Music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.marc.buscaminas.R;

/*
*   Servei creat per engegar i parar la musica depenen de si el usuari vol sonido o no
*
* */


public class SoundTrack extends Service {

    MediaPlayer sonidoDeFondo;
    int lenght;
    @Override
    public void onCreate() {
        sonidoDeFondo = MediaPlayer.create(this, R.raw.backgroundmusic);
        sonidoDeFondo.setLooping(true);
    }

    @Override
    public void onDestroy() {
        if(sonidoDeFondo.isPlaying())
            sonidoDeFondo.stop();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getExtras().getString("start").equals("start"))
            sonidoDeFondo.start();
        return startId;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onPause(){
        sonidoDeFondo.pause();
        lenght = sonidoDeFondo.getCurrentPosition();
    }

    public void onResume(){
        sonidoDeFondo.seekTo(lenght);
        sonidoDeFondo.start();
    }
}