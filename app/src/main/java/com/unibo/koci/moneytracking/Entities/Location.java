package com.unibo.koci.moneytracking.Entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by koale on 14/08/17.
 */



@Entity
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long locationID;

    @NotNull
    private String name;

    private double latitude;

    private double longitude;

    @Generated(hash = 2019704419)
    public Location(Long locationID, @NotNull String name, double latitude,
            double longitude) {
        this.locationID = locationID;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Generated(hash = 375979639)
    public Location() {
    }

    public Long getLocationID() {
        return this.locationID;
    }

    public void setLocationID(Long locationID) {
        this.locationID = locationID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}