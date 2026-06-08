package com.example.caloriecounter.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.caloriecounter.R;
import com.example.caloriecounter.model.Meal;
import java.util.List;
import java.util.Locale;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private List<Meal> meals;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Meal meal);
    }

    public MealAdapter(List<Meal> meals, OnItemClickListener listener) {
        this.meals = meals;
        this.listener = listener;
    }

    public void updateData(List<Meal> newMeals) {
        this.meals = newMeals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.bind(meal, listener);
    }

    @Override
    public int getItemCount() { return meals.size(); }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCalories, tvDate, tvCategory, tvMacronutrients;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvCalories = itemView.findViewById(R.id.tvItemCalories);
            tvCategory = itemView.findViewById(R.id.tvItemCategory);
            tvDate = itemView.findViewById(R.id.tvItemDate);
            tvMacronutrients = itemView.findViewById(R.id.tvItemMacros);
        }

        public void bind(final Meal meal, final OnItemClickListener listener) {
            tvTitle.setText(meal.getTitle());
            tvCalories.setText(meal.getCalories() + " ккал");
            tvCategory.setText(meal.getCategory());
            tvDate.setText(meal.getDate());

            String macrosText = String.format(Locale.getDefault(), "Б: %.1f | Ж: %.1f | У: %.1f",
                    meal.getProteins(), meal.getFats(), meal.getCarbs());
            tvMacronutrients.setText(macrosText);

            itemView.setOnClickListener(v -> listener.onItemClick(meal));
        }
    }
}