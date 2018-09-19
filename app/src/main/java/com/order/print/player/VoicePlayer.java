package com.order.print.player;


/**
 * Created by pt198 on 19/09/2018.
 */

public class VoicePlayer {
    public static final int VOICE_NEW_ORDER=0;
    public static final int VOICE_NET_DISCONN=1;
    public static final int VOICE_NET_CONN=2;
    public static final int VOICE_BLUE_DISCONN=3;
    public static final int VOICE_BLUE_CONN=4;
    public static final int VOICE_ORDER_CANCEL=5;

    private static class SingletonInstance{
        private static final VoicePlayer INSTANCE=new VoicePlayer();
    }
    private VoicePlayer(){

    }
    public static VoicePlayer getInstance(){
        return SingletonInstance.INSTANCE;
    }
    public void playVoice(int voiceType){
        switch (voiceType){
            case VOICE_NEW_ORDER:
                break;
            case VOICE_NET_DISCONN:
                break;
            case VOICE_NET_CONN:
                break;
            case VOICE_BLUE_DISCONN:
                break;
            case VOICE_BLUE_CONN:
                break;
            case VOICE_ORDER_CANCEL:
                break;
        }
    }
}
