package com.order.print.player;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.Telephony;
import android.util.Log;

import com.order.print.App;
import com.order.print.R;
import com.order.print.threadpool.CustomThreadPool;

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
    private static final String TAG = "VoicePlayerManager";
    private static class SingletonInstance{
        private static final VoicePlayerManager INSTANCE=new VoicePlayerManager();
    }
    private VoicePlayerManager(){

    }
    public static VoicePlayerManager getInstance(){
        return SingletonInstance.INSTANCE;
    }
    public void playVoice(int voiceType){
        try {
            Log.d(TAG, "playVoice: " + voiceType);
            switch (voiceType) {
                case VOICE_NEW_ORDER:
                    play(App.getInstance(), R.raw.new_order);
                    break;
                case VOICE_NET_DISCONN:
                    play(App.getInstance(), R.raw.net_disconn);
                    break;
                case VOICE_NET_CONN:
                    play(App.getInstance(), R.raw.net_conn);
                    break;
                case VOICE_BLUE_DISCONN:
                    play(App.getInstance(), R.raw.blue_disconn);
                    break;
                case VOICE_BLUE_CONN:
                    Log.d(TAG, "playVoice: VOICE_BLUE_CONN");
                    play(App.getInstance(), R.raw.blue_conn);
                    break;
                case VOICE_ORDER_CANCEL:
                    play(App.getInstance(), R.raw.order_cancel);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void play(Context context, int resId){
        try {
            if(App.getInstance().isPrintOrderFlag()) {
                Log.d(TAG, "play: ");
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                if (mMediaPlayer != null) {
                    mMediaPlayer.release();
                }
                Log.d(TAG, "MediaPlayer.create: ");
                mMediaPlayer = MediaPlayer.create(context, resId);
                final AudioManager am = (AudioManager) App.getInstance().getSystemService(Context.AUDIO_SERVICE);
                final int currentVolume=am.getStreamVolume(AudioManager.STREAM_MUSIC);
                int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_PLAY_SOUND);
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.start();
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_PLAY_SOUND);
                    }
                });
//            mMediaPlayer.setVolume(1.0f,1.0f);
            }
        }catch (Exception e){
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
