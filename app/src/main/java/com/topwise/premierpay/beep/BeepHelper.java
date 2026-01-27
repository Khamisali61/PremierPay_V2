package com.topwise.premierpay.beep;

import android.os.SystemClock;

public class BeepHelper {
    public static BeepHelper getInstance() {
        return BeepHelper.SingletonHolder.sInstance;
    }

    //静态内部类
    private static class SingletonHolder {
        private static final BeepHelper sInstance = new BeepHelper();
    }

    private AudioTrackManager audioTrackManager;

    //750; //Alert Tone
    //1500; //Success Tone
    //3000; //Remove Card Tone
    public void beep() {
        if (audioTrackManager != null) {
            audioTrackManager.stop();
            audioTrackManager = null;
        }
        audioTrackManager = new AudioTrackManager();
        audioTrackManager.start(750);
        audioTrackManager.play();
        SystemClock.sleep(50);
        audioTrackManager.stop();
    }

    public void beef(int [] ints) {
        if (audioTrackManager != null) {
            audioTrackManager.stop();
            audioTrackManager = null;
        }
        audioTrackManager = new AudioTrackManager();
        audioTrackManager.start(ints[0]);
        audioTrackManager.play();
        SystemClock.sleep(ints[1]);
        audioTrackManager.stop();
    }
}
