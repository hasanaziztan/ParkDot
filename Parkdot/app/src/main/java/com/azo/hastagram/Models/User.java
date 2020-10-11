package com.azo.hastagram.Models;

import java.util.ArrayList;

public class User {
    private String name;
    private String image;
    private String surname;
    //private ArrayList<Park> parkList;

    public User() {
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getImage() {
        return image;
    }

    public User setImage(String image) {
        this.image = image;
        return this;
    }

    public String getSurname() {
        return surname;
    }

    public User setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    /*public ArrayList<Park> getParkList() {
        return parkList;
    }

    public User setParkList(ArrayList<Park> parkList) {
        this.parkList = parkList;
        return this;
    }*/
}
