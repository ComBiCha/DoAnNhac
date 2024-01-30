package com.example.doannhac;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MusicService extends Service {

    public static  final String ACTION_NEXTS="NEXT";
    public static  final String ACTION_PREV="PREVIOUS";
    public static  final String ACTION_PLAY="PLAY";
    private IBinder mBinder=new MyBinder();
    ActionPlaying actionPlaying;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    public class  MyBinder extends Binder{
        MusicService getService(){
            return  MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String actionName=intent.getStringExtra("myActionName");
        if(actionName!=null)
        {
            switch (actionName){
                case ACTION_PLAY:
                    if(actionPlaying!=null)
                    {
                        actionPlaying.playClicked();
                    }
                    break;
                case ACTION_NEXTS:
                    if(actionPlaying!=null)
                    {
                        actionPlaying.nextClicked();
                    }
                    break;
                case ACTION_PREV:
                    if(actionPlaying!=null)
                    {
                        actionPlaying.prevClicked();
                    }
                    break;
            }
        }
        return START_STICKY;
    }
    public void setCallback(ActionPlaying actionPlaying)
    {
        this.actionPlaying=actionPlaying;
    }
}
