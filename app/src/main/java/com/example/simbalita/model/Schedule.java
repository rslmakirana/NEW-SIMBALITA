package com.example.simbalita.model;

public class Schedule {
    private int id;
    private String date; // YYYY-MM-DD
    private String time; // HH:MM
    private String title; // Posyandu Melati 1 etc.
    private String location; // Address

    public Schedule(int id, String date, String time, String title, String location) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.title = title;
        this.location = location;
    }

    public Schedule() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
