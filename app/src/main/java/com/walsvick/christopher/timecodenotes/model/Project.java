package com.walsvick.christopher.timecodenotes.model;

/**
 * Created on 12/13/2014 by Christopher.
 */
import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Project implements Parcelable {

    private int id;
    private String name;
    private LocalDate startDate;
    private ArrayList<String> cameras;
    private String addInfo;
    private ArrayList<Note> notes;

    public Project() {
        id = -1;
        name = "";
        startDate = LocalDate.now();
        cameras = new ArrayList<>();
        addInfo = "";
        notes = new ArrayList<>();
    }

    public Project(Project p) {
        id = -1;
        name = p.getName();
        startDate = p.getStartDate();
        cameras = new ArrayList<>(p.getCameras());
        addInfo = p.getAddInfo();
        notes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public ArrayList<String> getCameras() {
        return cameras;
    }

    public String getCamera(int pos) {
        return pos >= cameras.size() ? null : cameras.get(pos);
    }

    public void setCameras(List<String> list) {
        this.cameras = new ArrayList<>(list);
    }

    public void addCamera(String c) {
        cameras.add(c);
    }

    public void setAddInfo(String info) {
        this.addInfo = info;
    }

    public String getAddInfo() {
        return this.addInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(startDate.toString());
        dest.writeString(StringUtils.join(cameras, ';'));
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
        id = in.readInt();
        name = in.readString();
        startDate = new LocalDate(in.readString());
        cameras = new ArrayList<>(Arrays.asList(StringUtils.split(in.readString(), ';')));
        addInfo = in.readString();
        notes = new ArrayList<>();
        in.readList(notes, Note.class.getClassLoader());
    }
}
