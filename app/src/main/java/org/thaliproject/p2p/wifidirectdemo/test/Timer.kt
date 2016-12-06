package org.thaliproject.p2p.wifidirectdemo.test

import android.os.SystemClock

internal class Timer() {

    @Volatile
    private var startTime = 0L;

    fun start() {
        startTime = SystemClock.elapsedRealtime()
    }

    fun finish(): Long {
        if (startTime == 0L) {
            throw IllegalStateException("You have to call start before calling finish")
        }
        val result = SystemClock.elapsedRealtime() - startTime;
        resetTime();
        return result;
    }

    private fun resetTime() {
        startTime = 0L;
    }
}