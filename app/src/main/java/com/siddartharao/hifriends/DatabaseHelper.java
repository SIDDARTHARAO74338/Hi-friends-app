package com.siddartharao.hifriends;

import android.content.Context;
import android.content.SharedPreferences;

public class DatabaseHelper {
    private static final String PREFS_NAME = "prefs";
    private static final String SWITCH_STATE_KEY = "switch_state";
    private SharedPreferences sharedPreferences;

    public DatabaseHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Get the switch state from SharedPreferences
    public boolean getSwitchState() {
        return sharedPreferences.getBoolean(SWITCH_STATE_KEY, false); // Default to false if not found
    }

    // Save the switch state to SharedPreferences
    public void setSwitchState(boolean state) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SWITCH_STATE_KEY, state);
        editor.apply();
    }
}

