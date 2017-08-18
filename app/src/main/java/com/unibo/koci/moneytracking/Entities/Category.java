package com.unibo.koci.moneytracking.Entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
/**
 * Created by koale on 11/08/17.
 */



@Entity
public class Category {
    @Id
    private Long categoryID;

    @NotNull
    private String name;

    @Generated(hash = 428706830)
    public Category(Long categoryID, @NotNull String name) {
        this.categoryID = categoryID;
        this.name = name;
    }

    @Generated(hash = 1150634039)
    public Category() {
    }


    public Long getCategoryID() {
        return this.categoryID;
    }

    public void setCategoryID(Long categoryID) {
        this.categoryID = categoryID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}