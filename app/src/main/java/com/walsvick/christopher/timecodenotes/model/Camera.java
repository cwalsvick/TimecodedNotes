package com.walsvick.christopher.timecodenotes.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Christopher on 1/2/2015.
 */
public class Camera implements Parcelable {

    private String name;

    public Camera() {

    }

    public Camera(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    public static final Parcelable.Creator<Camera> CREATOR
            = new Parcelable.Creator<Camera>() {
        public Camera createFromParcel(Parcel in) {
            return new Camera(in);
        }

        public Camera[] newArray(int size) {
            return new Camera[size];
        }
    };

    public Camera(Parcel in) {
        name = in.readString();
    }
}
