package com.example.mycustomcalendar;

public class Events {
    String Date;
    String Event;
    String Month;
    String Time;
    String Year;

    public Events(String event, String time, String date, String Month2, String Year2) {
        this.Event = event;
        this.Time = time;
        this.Date = date;
    }

    public String getMonth() {
        return this.Month;
    }

    public void setMonth(String month) {
        this.Month = month;
    }

    public String getYear() {
        return this.Year;
    }

    public void setYear(String year) {
        this.Year = year;
    }

    public String getEvent() {
        return this.Event;
    }

    public void setEvent(String event) {
        this.Event = event;
    }

    public String getTime() {
        return this.Time;
    }

    public void setTime(String time) {
        this.Time = time;
    }

    public String getDate() {
        return this.Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }
}

