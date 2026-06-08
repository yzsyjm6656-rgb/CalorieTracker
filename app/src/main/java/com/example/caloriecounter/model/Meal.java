package com.example.caloriecounter.model;

import java.io.Serializable;

public class Meal implements Serializable {
    private int id;
    private String title;
    private int calories;
    private String category;
    private String date;

    // Новые свойства для хранения макронутриентов конкретного веса блюда
    private double proteins;
    private double fats;
    private double carbs;
    private double grams;
    private int productId;

    public Meal(int id, String title, int calories, String category, String date, double proteins, double fats, double carbs, double grams, int productId) {
        this.id = id;
        this.title = title;
        this.calories = calories;
        this.category = category;
        this.date = date;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
        this.grams = grams;
        this.productId = productId;
    }

    public Meal(String title, int calories, String category, String date, double proteins, double fats, double carbs, double grams, int productId) {
        this.title = title;
        this.calories = calories;
        this.category = category;
        this.date = date;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
        this.grams = grams;
        this.productId = productId;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getCalories() { return calories; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public double getProteins() { return proteins; }
    public double getFats() { return fats; }
    public double getCarbs() { return carbs; }
    public double getGrams() { return grams; }
    public int getProductId() { return productId; }
}