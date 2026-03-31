package com.myhobby.prayerbook;

import androidx.annotation.UiThread;

interface OnPrayerSelectedListener {
    @UiThread
    void onPrayerSelected(long prayerId);
}
