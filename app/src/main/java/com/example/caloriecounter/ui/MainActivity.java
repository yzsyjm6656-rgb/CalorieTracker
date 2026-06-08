package com.example.caloriecounter.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.caloriecounter.R;
import com.example.caloriecounter.database.DatabaseHelper;
import com.example.caloriecounter.model.Meal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MealAdapter adapter;
    private DatabaseHelper dbHelper;
    private TextView tvEmptyState, tvTotalCal, tvTotalProt, tvTotalFats, tvTotalCarbs;
    private Button btnSelectDate, btnManageProducts;
    private FloatingActionButton fabAdd;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        tvTotalCal = findViewById(R.id.tvTotalCal);
        tvTotalProt = findViewById(R.id.tvTotalProt);
        tvTotalFats = findViewById(R.id.tvTotalFats);
        tvTotalCarbs = findViewById(R.id.tvTotalCarbs);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnManageProducts = findViewById(R.id.btnManageProducts);
        fabAdd = findViewById(R.id.fabAdd);

        // Инициализируем текущую дату по умолчанию
        Calendar calendar = Calendar.getInstance();
        setCurrentDateString(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MealAdapter(new ArrayList<>(), meal -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("meal_id", meal.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        btnSelectDate.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(MainActivity.this, (view, year, month, dayOfMonth) -> {
                setCurrentDateString(year, month, dayOfMonth);
                loadData();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        btnManageProducts.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ManageProductsActivity.class));
        });

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            intent.putExtra("current_date", selectedDate);
            startActivity(intent);
        });
    }

    private void setCurrentDateString(int year, int month, int day) {
        selectedDate = String.format(Locale.getDefault(), "%02d.%02d.%04d", day, month + 1, year);
        btnSelectDate.setText("Дата: " + selectedDate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        ArrayList<Meal> meals = (ArrayList<Meal>) dbHelper.getMealsByDate(selectedDate);

        int totalCalories = 0;
        double totalProt = 0, totalFats = 0, totalCarbs = 0;

        for (Meal m : meals) {
            totalCalories += m.getCalories();
            totalProt += m.getProteins();
            totalFats += m.getFats();
            totalCarbs += m.getCarbs();
        }

        tvTotalCal.setText(totalCalories + " ккал");
        tvTotalProt.setText(String.format(Locale.getDefault(), "Б: %.1fg", totalProt));
        tvTotalFats.setText(String.format(Locale.getDefault(), "Ж: %.1fg", totalFats));
        tvTotalCarbs.setText(String.format(Locale.getDefault(), "У: %.1fg", totalCarbs));

        if (meals.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.updateData(meals);
        }
    }
}