package com.james.rocket.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

public class LevelData implements Parcelable {

    private int speed;
    private int multiplier;
    private int difficulty;
    @DrawableRes
    private int rocket;
    @DrawableRes
    private int antiRocket;
    @DrawableRes
    private int background;
    @DrawableRes
    private int cloud;
    private String id;

    public LevelData(int speed, int multiplier, int difficulty, @DrawableRes int rocket, @DrawableRes int antiRocket, @DrawableRes int background, @DrawableRes int cloud, String identifier) {
        this.speed = speed;
        this.multiplier = multiplier;
        this.difficulty = difficulty;
        this.rocket = rocket;
        this.antiRocket = antiRocket;
        this.background = background;
        this.cloud = cloud;
        this.id = identifier;
    }

    protected LevelData(Parcel in) {
        speed = in.readInt();
        multiplier = in.readInt();
        difficulty = in.readInt();
        rocket = in.readInt();
        antiRocket = in.readInt();
        background = in.readInt();
        cloud = in.readInt();
        id = in.readString();
    }

    public static final Creator<LevelData> CREATOR = new Creator<LevelData>() {
        @Override
        public LevelData createFromParcel(Parcel in) {
            return new LevelData(in);
        }

        @Override
        public LevelData[] newArray(int size) {
            return new LevelData[size];
        }
    };

    public String getName() {
        return String.valueOf(id.charAt(0)).toUpperCase() + id.substring(1, id.length());
    }

    public int getSpeed() {
        return speed;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public int getDifficulty() {
        return difficulty;
    }

    @DrawableRes
    public int getRocket() {
        return rocket;
    }

    @DrawableRes
    public int getAntiRocket() {
        return antiRocket;
    }

    @DrawableRes
    public int getBackground() {
        return background;
    }

    @DrawableRes
    public int getCloud() {
        return cloud;
    }

    public String getIdentifier() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(speed);
        dest.writeInt(multiplier);
        dest.writeInt(difficulty);
        dest.writeInt(rocket);
        dest.writeInt(antiRocket);
        dest.writeInt(background);
        dest.writeInt(cloud);
        dest.writeString(id);
    }
}
