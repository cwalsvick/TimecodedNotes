package com.walsvick.christopher.timecodenotes.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.LocalTime;

/**
 * Created by Christopher on 1/3/2015.
 */
public class Note implements Parcelable {

    private LocalTime timeCode;
    private String note;
    private Camera camera;

    public Note(LocalTime timeCode) {
        this.timeCode = timeCode;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return this.note;
    }

    public void setCamera(Camera c) {
        this.camera = c;
    }

    public Camera getCamera() {
        return camera;
    }

    public LocalTime getTimeCode() {
        return timeCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(timeCode.toString());
        dest.writeString(note.toString());
        dest.writeParcelable(camera, 0);
    }

    public static final Parcelable.Creator<Note> CREATOR
            = new Parcelable.Creator<Note>() {
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    private Note(Parcel in) {
        timeCode = new LocalTime(in.readString());
        note = in.readString();
        camera = in.readParcelable(Camera.class.getClassLoader());
    }
}
