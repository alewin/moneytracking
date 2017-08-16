package com.unibo.koci.moneytracking.Entities;

import java.util.Date;

/**
 * Created by koale on 12/08/17.
 */

public class MoneyItem {
    int id;
    String name;
    String description;
    Date date;
    Category category;
    Location pos;
    MoneyTypology amount;

    public MoneyItem(int id, String name, String description, Date date, Category category, Location pos, MoneyTypology amount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.category = category;
        this.pos = pos;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Location getPos() {
        return pos;
    }

    public void setPos(Location pos) {
        this.pos = pos;
    }

    public MoneyTypology getAmount() {
        return amount;
    }

    public void setAmount(MoneyTypology amount) {
        this.amount = amount;
    }
}

