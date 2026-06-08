package com.example.caloriecounter.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.caloriecounter.R;
import com.example.caloriecounter.database.DatabaseHelper;
import com.example.caloriecounter.model.Meal;
import com.example.caloriecounter.model.Product;
import java.util.List;

public class AddEditActivity extends AppCompatActivity {
    private Spinner spinnerProducts;
    private EditText etGrams, etCategory, etDate;
    private TextView tvSelectionPrompt;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private List<Product> productList;
    private int mealId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        dbHelper = new DatabaseHelper(this);

        spinnerProducts = findViewById(R.id.spinnerProducts);
        etGrams = findViewById(R.id.etGrams);
        etCategory = findViewById(R.id.etCategory);
        etDate = findViewById(R.id.etDate);
        tvSelectionPrompt = findViewById(R.id.tvSelectionPrompt);
        btnSave = findViewById(R.id.btnSave);

        // Инициализируем список продуктов в выпадающем окне
        productList = dbHelper.getAllProducts();
        if (productList.isEmpty()) {
            tvSelectionPrompt.setText("Сначала добавьте продукты в базу!");
            btnSave.setEnabled(false);
        } else {
            ArrayAdapter<Product> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProducts.setAdapter(adapter);
        }

        if (getIntent().hasExtra("meal_id")) {
            mealId = getIntent().getIntExtra("meal_id", -1);
            isEditMode = true;
            setTitle("Редактировать приём");
            loadMealData();
        } else {
            setTitle("Добавить приём пищи");
            String date = getIntent().getStringExtra("current_date");
            etDate.setText(date);
        }

        btnSave.setOnClickListener(v -> saveMeal());
    }

    private void loadMealData() {
        Meal meal = dbHelper.getMealById(mealId);
        etGrams.setText(String.valueOf(meal.getGrams()));
        etCategory.setText(meal.getCategory());
        etDate.setText(meal.getDate());

        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getId() == meal.getProductId()) {
                spinnerProducts.setSelection(i);
                break;
            }
        }
    }

    private void saveMeal() {
        if (productList.isEmpty()) return;

        Product selectedProduct = (Product) spinnerProducts.getSelectedItem();
        String gramsStr = etGrams.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if (gramsStr.isEmpty()) {
            etGrams.setError("Укажите вес в граммах");
            return;
        }

        double grams;
        try {
            grams = Double.parseDouble(gramsStr);
            if (grams <= 0) {
                etGrams.setError("Вес должен быть больше 0");
                return;
            }
        } catch (NumberFormatException e) {
            etGrams.setError("Некорректное число");
            return;
        }

        if (category.isEmpty()) {
            etCategory.setError("Введите категорию");
            return;
        }
        if (date.isEmpty()) {
            etDate.setError("Введите дату");
            return;
        }

        // Автоматический расчет по ТЗ: значение = (значение_на_100г * граммы) / 100
        int calculatedCalories = (int) ((selectedProduct.getCalories() * grams) / 100);
        double calculatedProt = (selectedProduct.getProteins() * grams) / 100;
        double calculatedFats = (selectedProduct.getFats() * grams) / 100;
        double calculatedCarbs = (selectedProduct.getCarbs() * grams) / 100;

        String mealTitle = selectedProduct.getTitle() + " (" + (int)grams + "г)";

        if (isEditMode) {
            Meal meal = new Meal(mealId, mealTitle, calculatedCalories, category, date, calculatedProt, calculatedFats, calculatedCarbs, grams, selectedProduct.getId());
            dbHelper.updateMeal(meal);
            Toast.makeText(this, "Запись обновлена", Toast.LENGTH_SHORT).show();
        } else {
            Meal meal = new Meal(mealTitle, calculatedCalories, category, date, calculatedProt, calculatedFats, calculatedCarbs, grams, selectedProduct.getId());
            dbHelper.addMeal(meal);
            Toast.makeText(this, "Запись добавлена", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}