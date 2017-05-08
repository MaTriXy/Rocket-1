package com.james.rocket.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import com.james.rocket.R;
import com.james.rocket.data.LevelData;

public class PreferenceUtils {

    public enum PreferenceIdentifier {
        HIGH_SCORE, TOTAL_ATTEMPTS
    }

    public static final LevelData[] LEVELS = new LevelData[]{
            new LevelData(10, 2, 3500, R.mipmap.sled, R.mipmap.snowball, R.mipmap.snowbg, R.mipmap.cloud, "Special"),
            new LevelData(5, 1, 5000, R.mipmap.rocket3, R.mipmap.rocket, R.mipmap.bg, R.mipmap.cloud, "Easy"),
            new LevelData(10, 2, 3500, R.mipmap.rocket4, R.mipmap.rocket2, R.mipmap.sandbg, R.mipmap.sandcloud, "Medium"),
            new LevelData(15, 3, 2000, R.mipmap.sunnyrocket, R.mipmap.rocket3, R.mipmap.sunnybg, R.mipmap.cloud, "Hard"),
            new LevelData(20, 5, 1000, R.mipmap.spacerocket, R.mipmap.meteor, R.mipmap.spacebg, R.mipmap.spacecloud, "Extreme")
    };

    public static void putScore(Context context, String id, PreferenceIdentifier identifier, int integer) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(id + identifier.toString(), integer).apply();
    }

    public static int getScore(Context context, String id, PreferenceIdentifier identifier) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(id + identifier.toString(), 0);
    }
}
