package com.example.timetablefragment;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "event_table")
public class Events {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "eventTitle")
    public String eventTitle;
    @NonNull
    @ColumnInfo(name = "eventVenue")
    public String eventVenue;
    @NonNull
    @ColumnInfo(name = "organizers")
    public String organizers;
    @NonNull
    @ColumnInfo(name = "eventDescription")
    public String eventDescription;
    @NonNull
    @ColumnInfo(name = "startYear")
    public int startYear;
    @NonNull
    @ColumnInfo(name = "startMonth")
    public int startMonth;
    @NonNull
    @ColumnInfo(name = "startDay")
    public int startDay;
    @NonNull
    @ColumnInfo(name = "startHour")
    public int startHour;
    @NonNull
    @ColumnInfo(name = "startMin")
    public int startMin;
    @NonNull
    @ColumnInfo(name = "endYear")
    public int endYear;
    @NonNull
    @ColumnInfo(name = "endMonth")
    public int endMonth;
    @NonNull
    @ColumnInfo(name = "endDay")
    public int endDay;
    @NonNull
    @ColumnInfo(name = "endHour")
    public int endHour;
    @NonNull
    @ColumnInfo(name = "endMin")
    public int endMin;

    public Events(String eventTitle, String eventVenue, String organizers, String eventDescription,
                  int startYear, int startMonth, int startDay, int startHour, int startMin,
                  int endYear, int endMonth, int endDay, int endHour, int endMin){
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
        this.startHour = startHour;
        this.startMin = startMin;
        this.endYear = endYear;
        this.endMonth = endMonth;
        this.endDay = endDay;
        this.endHour = endHour;
        this.endMin = endMin;
        this.eventTitle = eventTitle;
        this.eventVenue = eventVenue;
        this.organizers = organizers;
        this.eventDescription = eventDescription;
    }
    public int getStartYear(){return this.startYear;}
    public int getStartMonth(){return this.startMonth;}
    public int getStartDay(){return this.startDay;}
    public int getStartHour(){return this.startHour;}
    public int getStartMin(){return this.startMin;}
    public int getEndYear(){return this.endYear;}
    public int getEndMonth(){return this.endMonth;}
    public int getEndDay(){return this.endDay;}
    public int getEndHour(){return this.endHour;}
    public int getEndMin(){return this.endMin;}
    public String getEventTitle(){return this.eventTitle;}
    public String getEventVenue(){return this.eventVenue;}
    public String getOrganizers(){return this.organizers;}
    public String getEventDescription(){return this.eventDescription;}

}