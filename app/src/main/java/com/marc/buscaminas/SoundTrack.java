package com.marc.buscaminas;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class SoundTrack extends Service {

    MediaPlayer sonidoDeFondo;

    @Override
    public void onCreate() {
        sonidoDeFondo = MediaPlayer.create(this,R.raw.backgroundmusic);
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
}