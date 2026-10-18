/*
 * Decompiled with CFR 0.152.
 */
package org.polyfrost.example.module;

public class Module {
    private boolean isEnabled;

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        if (!enabled) {
            this.onDisable();
        }
        if (enabled) {
            this.onEnable();
        }
    }

    public void onDisable() {
    }

    public void onEnable() {
    }
}

