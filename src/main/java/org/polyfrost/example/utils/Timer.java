/*
 * Decompiled with CFR 0.152.
 */
package org.polyfrost.example.utils;

public class Timer {
    private long lastMS;

    private long getCurrentMilliseconds() {
        return System.nanoTime() / 1000000L;
    }

    public boolean passed(double ms) {
        return (double)(this.getCurrentMilliseconds() - this.lastMS) >= ms;
    }

    public void reset() {
        this.lastMS = this.getCurrentMilliseconds();
    }

    public long getTime() {
        return this.getCurrentMilliseconds() - this.lastMS;
    }
}

