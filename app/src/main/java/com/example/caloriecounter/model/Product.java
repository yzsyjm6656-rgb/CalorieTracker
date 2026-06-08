package com.example.caloriecounter.model;

import java.io.Serializable;
import java.util.Locale;

public class Product implements Serializable {
    private int id;
    private String title;
    private double proteins;
    private double fats;
    private double carbs;
    private int calories;

    public Product(int id, String title, double proteins, double fats, double carbs, int calories) {
        this.id = id;
        this.title = title;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
        this.calories = calories;
    }

    public Product(String title, double proteins, double fats, double carbs, int calories) {
        this.title = title;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
        this.calories = calories;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public double getProteins() { return proteins; }
    public double getFats() { return fats; }
    public double getCarbs() { return carbs; }
    public int getCalories() { return calories; }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s (%d ккал/100г)", title, calories);
    }
}