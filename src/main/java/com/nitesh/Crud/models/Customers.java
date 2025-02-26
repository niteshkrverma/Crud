package com.nitesh.Crud.models;

import jakarta.persistence.*;

@Entity
@Table(name = "customer")
public class Customers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String address;
    private String email;
    private String pwd;
    private long number;
    private String image; // Store image filename

    // Default constructor
    public Customers() {}

    // Parameterized constructor
    public Customers(String name, String address, String email, String pwd, long number, String image) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.pwd = pwd;
        this.number = number;
        this.image = image;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPwd() { return pwd; }
    public void setPwd(String pwd) { this.pwd = pwd; }

    public long getNumber() { return number; }
    public void setNumber(long number) { this.number = number; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    @Override
    public String toString() {
        return "Customers{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", pwd='" + pwd + '\'' +
                ", number=" + number +
                ", image='" + image + '\'' +
                '}';
    }
}
