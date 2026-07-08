package com.example.simbalita.model;

public class Examination {
    private int id;
    private int childId;
    private String date; // YYYY-MM-DD
    private double weight; // in kg
    private double height; // in cm
    private String status; // 'Normal', 'Kurang', 'Lebih', 'Stunting'

    public Examination(int id, int childId, String date, double weight, double height, String status) {
        this.id = id;
        this.childId = childId;
        this.date = date;
        this.weight = weight;
        this.height = height;
        this.status = status;
    }

    public Examination() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getChildId() { return childId; }
    public void setChildId(int childId) { this.childId = childId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
