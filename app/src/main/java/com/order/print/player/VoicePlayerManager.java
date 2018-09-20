package com.order.print.player;


import android.content.Context;
import android.media.MediaPlayer;
import android.provider.Telephony;

import com.order.print.App;
import com.order.print.R;

import java.io.IOException;

/**
 * Created by pt198 on 19/09/2018.
 */

public class VoicePlayerManager {

    private MediaPlayer mMediaPlayer;
    public static final int VOICE_NEW_ORDER=0;
    public static final int VOICE_NET_DISCONN=1;
    public static final int VOICE_NET_CONN=2;
    public static final int VOICE_BLUE_DISCONN=3;
    public static final int VOICE_BLUE_CONN=4;
    public static final int VOICE_ORDER_CANCEL=5;

    private static class SingletonInstance{
        private static final VoicePlayerManager INSTANCE=new VoicePlayerManager();
    }
    private VoicePlayerManager(){

    }
    public static VoicePlayerManager getInstance(){
        return SingletonInstance.INSTANCE;
    }
    public void playVoice(int voiceType){
        switch (voiceType){
            case VOICE_NEW_ORDER:
                play(App.getInstance(),R.raw.new_order);
                break;
            case VOICE_NET_DISCONN:
                play(App.getInstance(),R.raw.net_disconn);
                break;
            case VOICE_NET_CONN:
                play(App.getInstance(),R.raw.net_conn);
                break;
            case VOICE_BLUE_DISCONN:
                play(App.getInstance(),R.raw.blue_disconn);
                break;
            case VOICE_BLUE_CONN:
                play(App.getInstance(),R.raw.blue_conn);
                break;
            case VOICE_ORDER_CANCEL:
                play(App.getInstance(),R.raw.order_cancel);
                break;
        }
    }

    private void play(Context context, int resId){
        if(mMediaPlayer!=null&&mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }
        try {
            mMediaPlayer=MediaPlayer.create(context,resId);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void stop(){
        if(mMediaPlayer!=null&&mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }
    }
    public void release(){
        if(mMediaPlayer!=null&&mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }
        if(mMediaPlayer!=null) {
            mMediaPlayer.release();
        }
    }
}
