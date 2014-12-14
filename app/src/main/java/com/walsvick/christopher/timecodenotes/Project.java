package com.walsvick.christopher.timecodenotes;

/**
 * Created by Christopher on 12/13/2014.
 */
import org.joda.time.DateTime;
import lombok.Data;

@Data
public class Project {

    String name;
    DateTime startDate;

    public Project(String name, DateTime startDate) {
        this.name = name;
        this.startDate = startDate;
    }
}
