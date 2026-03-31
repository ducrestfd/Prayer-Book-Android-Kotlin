package com.myhobby.prayerbook.thread;

import androidx.annotation.UiThread;

public interface UiRunnable extends Runnable {

    @UiThread
    void run();

}
