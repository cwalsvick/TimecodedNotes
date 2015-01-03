package com.walsvick.christopher.timecodenotes.com.walsvick.christopher.timecodenodes.model;

/**
 * Created by Christopher on 12/13/2014.
 */
import org.joda.time.LocalDate;

import java.util.ArrayList;

public class Project {

    private String name;
    private LocalDate startDate;
    private ArrayList<Camera> cameraList;
    private String addInfo;

    public Project() {
        name = new String();
        startDate = LocalDate.now();
        cameraList = new ArrayList<Camera>();
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
}
