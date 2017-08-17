package com.unibo.koci.moneytracking.Entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by koale on 14/08/17.
 */



@Entity
public class Location {
    @Id
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private double latatude;

    @NotNull
    private double longitude;

    @Generated(hash = 146174459)
    public Location(Long id, @NotNull String name, double latatude,
            double longitude) {
        this.id = id;
        this.name = name;
        this.latatude = latatude;
        this.longitude = longitude;
    }

    @Generated(hash = 375979639)
    public Location() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatatude() {
        return this.latatude;
    }

    public void setLatatude(double latatude) {
        this.latatude = latatude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}