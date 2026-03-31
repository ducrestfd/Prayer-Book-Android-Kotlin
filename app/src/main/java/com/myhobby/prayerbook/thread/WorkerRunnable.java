package com.myhobby.prayerbook.thread;

import androidx.annotation.WorkerThread;

public interface WorkerRunnable extends Runnable {

    @WorkerThread
    void run();

}
