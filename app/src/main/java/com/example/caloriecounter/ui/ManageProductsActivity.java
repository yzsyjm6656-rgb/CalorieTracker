package com.example.caloriecounter.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.caloriecounter.R;
import com.example.caloriecounter.database.DatabaseHelper;
import com.example.caloriecounter.model.Product;
import java.util.List;

public class ManageProductsActivity extends AppCompatActivity {
    private EditText etProdTitle, etProdProt, etProdFats, etProdCarbs, etProdCal;
    private Button btnAddProduct;
    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);
        setTitle("База продуктов");

        dbHelper = new DatabaseHelper(this);

        etProdTitle = findViewById(R.id.etProdTitle);
        etProdProt = findViewById(R.id.etProdProt);
        etProdFats = findViewById(R.id.etProdFats);
        etProdCarbs = findViewById(R.id.etProdCarbs);
        etProdCal = findViewById(R.id.etProdCal);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        rvProducts = findViewById(R.id.rvProducts);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        List<Product> list = dbHelper.getAllProducts();
        adapter = new ProductAdapter(list);
        rvProducts.setAdapter(adapter);

        btnAddProduct.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String title = etProdTitle.getText().toString().trim();
        String protStr = etProdProt.getText().toString().trim();
        String fatsStr = etProdFats.getText().toString().trim();
        String carbsStr = etProdCarbs.getText().toString().trim();
        String calStr = etProdCal.getText().toString().trim();

        if (title.isEmpty()) { etProdTitle.setError("Введите название"); return; }
        if (protStr.isEmpty()) { etProdProt.setError("Заполните"); return; }
        if (fatsStr.isEmpty()) { etProdFats.setError("Заполните"); return; }
        if (carbsStr.isEmpty()) { etProdCarbs.setError("Заполните"); return; }
        if (calStr.isEmpty()) { etProdCal.setError("Заполните"); return; }

        try {
            double p = Double.parseDouble(protStr);
            double f = Double.parseDouble(fatsStr);
            double c = Double.parseDouble(carbsStr);
            int cal = Integer.parseInt(calStr);

            Product product = new Product(title, p, f, c, cal);
            dbHelper.addProduct(product);

            // Сброс формы и обновление списка
            etProdTitle.setText(""); etProdProt.setText(""); etProdFats.setText(""); etProdCarbs.setText(""); etProdCal.setText("");
            adapter.updateData(dbHelper.getAllProducts());
            Toast.makeText(this, "Продукт добавлен!", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Проверьте корректность чисел", Toast.LENGTH_SHORT).show();
        }
    }
}