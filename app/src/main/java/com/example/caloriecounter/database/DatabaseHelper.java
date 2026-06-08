package com.example.caloriecounter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.caloriecounter.model.Meal;
import com.example.caloriecounter.model.Product;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "calories.db";
    // Повышаем версию для выполнения безопасной миграции данных
    private static final int DATABASE_VERSION = 2;

    // ТаблицаMeals
    public static final String TABLE_MEALS = "meals";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_DATE = "date";
    // Новые поля для подневного учета БЖУК
    public static final String COLUMN_PROTEINS = "proteins";
    public static final String COLUMN_FATS = "fats";
    public static final String COLUMN_CARBS = "carbs";
    public static final String COLUMN_GRAMS = "grams";
    public static final String COLUMN_PRODUCT_ID = "product_id";

    // Новая таблицабазы продуктов
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_PROD_ID = "id";
    public static final String COLUMN_PROD_TITLE = "title";
    public static final String COLUMN_PROD_PROTEINS = "proteins";
    public static final String COLUMN_PROD_FATS = "fats";
    public static final String COLUMN_PROD_CARBS = "carbs";
    public static final String COLUMN_PROD_CALORIES = "calories";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы продуктов
        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COLUMN_PROD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PROD_TITLE + " TEXT, " +
                COLUMN_PROD_PROTEINS + " REAL, " +
                COLUMN_PROD_FATS + " REAL, " +
                COLUMN_PROD_CARBS + " REAL, " +
                COLUMN_PROD_CALORIES + " INTEGER)";
        db.execSQL(createProductsTable);

        // Создание таблицы приемов пищи со всеми полями КБЖУ
        String createMealsTable = "CREATE TABLE " + TABLE_MEALS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CALORIES + " INTEGER, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_PROTEINS + " REAL, " +
                COLUMN_FATS + " REAL, " +
                COLUMN_CARBS + " REAL, " +
                COLUMN_GRAMS + " REAL, " +
                COLUMN_PRODUCT_ID + " INTEGER)";
        db.execSQL(createMealsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Безопасное добавление новых колонок без удаления старых данных пользователей
            db.execSQL("ALTER TABLE " + TABLE_MEALS + " ADD COLUMN " + COLUMN_PROTEINS + " REAL DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_MEALS + " ADD COLUMN " + COLUMN_FATS + " REAL DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_MEALS + " ADD COLUMN " + COLUMN_CARBS + " REAL DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_MEALS + " ADD COLUMN " + COLUMN_GRAMS + " REAL DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_MEALS + " ADD COLUMN " + COLUMN_PRODUCT_ID + " INTEGER DEFAULT -1");

            // Создаем таблицу продуктов, если её не было
            String createProductsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCTS + " (" +
                    COLUMN_PROD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PROD_TITLE + " TEXT, " +
                    COLUMN_PROD_PROTEINS + " REAL, " +
                    COLUMN_PROD_FATS + " REAL, " +
                    COLUMN_PROD_CARBS + " REAL, " +
                    COLUMN_PROD_CALORIES + " INTEGER)";
            db.execSQL(createProductsTable);
        }
    }

    // --- ОПЕРАЦИИ ДЛЯ ПРОДУКТОВ (CRUD) ---
    public long addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROD_TITLE, product.getTitle());
        values.put(COLUMN_PROD_PROTEINS, product.getProteins());
        values.put(COLUMN_PROD_FATS, product.getFats());
        values.put(COLUMN_PROD_CARBS, product.getCarbs());
        values.put(COLUMN_PROD_CALORIES, product.getCalories());
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " ORDER BY " + COLUMN_PROD_TITLE + " ASC", null);
        if (cursor.moveToFirst()) {
            do {
                Product p = new Product(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROD_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROD_TITLE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PROD_PROTEINS)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PROD_FATS)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PROD_CARBS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROD_CALORIES))
                );
                list.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // --- ОПЕРАЦИИ ДЛЯ ПРИЕМОВ ПИЩИ ---
    public long addMeal(Meal meal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, meal.getTitle());
        values.put(COLUMN_CALORIES, meal.getCalories());
        values.put(COLUMN_CATEGORY, meal.getCategory());
        values.put(COLUMN_DATE, meal.getDate());
        values.put(COLUMN_PROTEINS, meal.getProteins());
        values.put(COLUMN_FATS, meal.getFats());
        values.put(COLUMN_CARBS, meal.getCarbs());
        values.put(COLUMN_GRAMS, meal.getGrams());
        values.put(COLUMN_PRODUCT_ID, meal.getProductId());
        return db.insert(TABLE_MEALS, null, values);
    }

    // Выборка строго по конкретному дню (Подневный учет)
    public List<Meal> getMealsByDate(String date) {
        List<Meal> mealList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEALS, null, COLUMN_DATE + "=?", new String[]{date}, null, null, COLUMN_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Meal meal = new Meal(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CALORIES)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PROTEINS)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FATS)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CARBS)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_GRAMS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID))
                );
                mealList.add(meal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return mealList;
    }

    public Meal getMealById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEALS, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) cursor.moveToFirst();

        Meal meal = new Meal(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CALORIES)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PROTEINS)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FATS)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CARBS)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_GRAMS)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID))
        );
        cursor.close();
        return meal;
    }

    public int updateMeal(Meal meal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, meal.getTitle());
        values.put(COLUMN_CALORIES, meal.getCalories());
        values.put(COLUMN_CATEGORY, meal.getCategory());
        values.put(COLUMN_DATE, meal.getDate());
        values.put(COLUMN_PROTEINS, meal.getProteins());
        values.put(COLUMN_FATS, meal.getFats());
        values.put(COLUMN_CARBS, meal.getCarbs());
        values.put(COLUMN_GRAMS, meal.getGrams());
        values.put(COLUMN_PRODUCT_ID, meal.getProductId());
        return db.update(TABLE_MEALS, values, COLUMN_ID + "=?", new String[]{String.valueOf(meal.getId())});
    }

    public void deleteMeal(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEALS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }
}