package com.walsvick.christopher.timecodenotes.com.walsvick.christopher.timecodenodes.model;

/**
 * Created by Christopher on 12/13/2014.
 */
import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.LocalDate;

import java.util.ArrayList;

public class Project implements Parcelable {

    private String name;
    private LocalDate startDate;
    private ArrayList<Camera> cameraList;
    private String addInfo;
    private ArrayList<Note> notes;

    public Project() {
        name = new String();
        startDate = LocalDate.now();
        cameraList = new ArrayList<Camera>();
        addInfo = new String();
        notes = new ArrayList<Note>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setCameraList(ArrayList<Camera> cameraList) {
        this.cameraList = cameraList;
    }

    public ArrayList<Camera> getCameraList() {
        return cameraList;
    }

    public ArrayList<Note> getNoteList() { return notes; }

    public void addNote(Note note) {
        if (notes == null) {
            notes = new ArrayList<Note>();
        }
        notes.add(note);
    }

    public ArrayList<String> getCameraListNames() {
        ArrayList<String> rtn = new ArrayList<>();

        for (Camera c : cameraList) {
            rtn.add(c.getName());
        }

        return rtn;
    }

    public int getNumCameras() {
        return cameraList.size();
    }

    public void addCamera(Camera c) {
        cameraList.add(c);
    }

    public void setAddInfo(String info) {
        this.addInfo = info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(startDate.toString());
        dest.writeList(cameraList);
        dest.writeString(addInfo);
        dest.writeList(notes);
    }

    public static final Parcelable.Creator<Project> CREATOR
            = new Parcelable.Creator<Project>() {
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public Project(Parcel in) {
        name = in.readString();
        startDate = new LocalDate(in.readString());
        cameraList = new ArrayList<Camera>();
        in.readList(cameraList, Camera.class.getClassLoader());
        addInfo = in.readString();
        notes = new ArrayList<Note>();
        in.readList(notes, Note.class.getClassLoader());
    }
}
