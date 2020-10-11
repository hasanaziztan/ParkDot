package com.azo.hastagram.Models;

import java.io.Serializable;

public class Park implements Serializable {
    private String id;
    private String parkName;
    private String latit;
    private String longi;

    public Park() {
    }

    public String getId() {
        return id;
    }

    public Park setId(String id) {
        this.id = id;
        return this;
    }

    public String getParkName() {
        return parkName;
    }

    public Park setParkName(String parkName) {
        this.parkName = parkName;
        return this;
    }

    public String getLatit() {
        return latit;
    }

    public Park setLatit(String latit) {
        this.latit = latit;
        return this;
    }

    public String getLongi() {
        return longi;
    }

    public Park setLongi(String longi) {
        this.longi = longi;
        return this;
    }
}
