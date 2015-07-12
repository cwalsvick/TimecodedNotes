package com.walsvick.christopher.timecodenotes.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

/**
 * Created on 1/3/2015 by Christopher.
 */
public class Note implements Parcelable {

    private int id;
    private int projectId;
    private String timeCode;
    private String note;
    private String camera;
    private int cameraId;

    public Note() {
        id = -1;
    }

    public Note(String timeCode) {
        this.timeCode = timeCode;
        id = -1;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return this.note;
    }

    public void setCamera(String c) {
        this.camera = c;
    }

    public String getCamera() {
        return camera;
    }

    public String getTimeCode() {
        return timeCode;
    }

    public String getLocalDateTimeCode() { return timeCode; }

    public void setTimeCode(String time) {
        timeCode = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(timeCode.toString());
        dest.writeString(note);
        dest.writeString(camera);
        dest.writeInt(cameraId);
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
        id = in.readInt();
        timeCode = in.readString();
        note = in.readString();
        camera = in.readString();
        cameraId = in.readInt();
    }

    public void setProjectId(int id) {
        this.projectId = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
