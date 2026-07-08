package com.example.simbalita.model;

public class Notification {
    private int id;
    private String title;
    private String body;
    private String timeLabel;
    private String iconType;

    public Notification() {}

    public Notification(int id, String title, String body, String timeLabel, String iconType) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.timeLabel = timeLabel;
        this.iconType = iconType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(String timeLabel) {
        this.timeLabel = timeLabel;
    }

    public String getIconType() {
        return iconType;
    }

    public void setIconType(String iconType) {
        this.iconType = iconType;
    }
}
