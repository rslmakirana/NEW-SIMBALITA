package com.example.simbalita.model;

public class User {
    private int id;
    private String name;
    private String phone;
    private String password;
    private String role; // 'IBU' or 'ADMIN'
    private String nik;
    private String address;
    private String username;

    public User(int id, String name, String phone, String password, String role, String nik, String address, String username) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.nik = nik;
        this.address = address;
        this.username = username;
    }

    public User() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
