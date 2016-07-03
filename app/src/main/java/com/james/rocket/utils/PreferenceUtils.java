package com.james.rocket.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

public class PreferenceUtils {

    public enum PreferenceIdentifier {
        HIGH_SCORE, TOTAL_ATTEMPTS
    }

    public enum LevelIdentifier {
        EASY, MEDIUM, HARD, EXTREME, SPECIAL
    }

    public static void putScore(Context context, LevelIdentifier level, PreferenceIdentifier identifier, int integer) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(level.toString() + identifier.toString(), integer).apply();
    }

    @Nullable
    public static Integer getScore(Context context, LevelIdentifier level, PreferenceIdentifier identifier) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(level.toString() + identifier.toString())) return prefs.getInt(level.toString() + identifier.toString(), 0);
        else return null;
    }
}
