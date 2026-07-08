package com.example.simbalita.model;

public class Child {
    private int id;
    private String name;
    private String birthDate; // YYYY-MM-DD
    private String gender; // 'Laki-laki' or 'Perempuan'
    private double birthWeight; // in kg
    private double birthHeight; // in cm
    private int motherId; // FK to users table

    public Child(int id, String name, String birthDate, String gender, double birthWeight, double birthHeight, int motherId) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.birthWeight = birthWeight;
        this.birthHeight = birthHeight;
        this.motherId = motherId;
    }

    public Child() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public double getBirthWeight() { return birthWeight; }
    public void setBirthWeight(double birthWeight) { this.birthWeight = birthWeight; }

    public double getBirthHeight() { return birthHeight; }
    public void setBirthHeight(double birthHeight) { this.birthHeight = birthHeight; }

    public int getMotherId() { return motherId; }
    public void setMotherId(int motherId) { this.motherId = motherId; }
}
