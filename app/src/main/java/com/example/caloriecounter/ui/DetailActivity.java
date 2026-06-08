package com.example.caloriecounter.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.caloriecounter.R;
import com.example.caloriecounter.database.DatabaseHelper;
import com.example.caloriecounter.model.Meal;

public class DetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvCalories, tvCategory, tvDate;
    private Button btnEdit, btnDelete;
    private DatabaseHelper dbHelper;
    private int mealId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle("Детали записи");

        dbHelper = new DatabaseHelper(this);
        mealId = getIntent().getIntExtra("meal_id", -1);

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvCalories = findViewById(R.id.tvDetailCalories);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvDate = findViewById(R.id.tvDetailDate);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, AddEditActivity.class);
            intent.putExtra("meal_id", mealId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMealDetails();
    }

    private void loadMealDetails() {
        try {
            Meal meal = dbHelper.getMealById(mealId);
            tvTitle.setText(meal.getTitle());
            tvCalories.setText(meal.getCalories() + " ккал");
            tvCategory.setText("Категория: " + meal.getCategory());
            tvDate.setText("Дата: " + meal.getDate());
        } catch (Exception e) {
            finish(); // Если запись удалена, закрываем экран
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Удаление записи")
                .setMessage("Вы действительно хотите удалить запись?")
                .setPositiveButton("Да", (dialog, which) -> {
                    dbHelper.deleteMeal(mealId);
                    Toast.makeText(DetailActivity.this, "Запись удалена", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Нет", null)
                .show();
    }
}