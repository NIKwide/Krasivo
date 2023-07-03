package com.example.krasivo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CatalogDetailsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_details); // установка верстки для активности

        recyclerView = findViewById(R.id.product_list);
        adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Инициализация базы данных Firebase
        database = FirebaseDatabase.getInstance();
        String catalogName = "Catalog"; // catalogNumber может быть переменной или значением, введенным пользователем
        myRef = database.getReference("Store").child(catalogName).child("AllTovar");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Product> productList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String productName = snapshot.child("name").getValue(String.class);
                    String productPrice = snapshot.child("price").getValue(String.class);
                    String productImage = snapshot.child("Image").getValue(String.class);

                    Product product = new Product(productName, productPrice, productImage);
                    productList.add(product);
                }

                RecyclerView recyclerView = findViewById(R.id.product_list);
                ProductAdapter adapter = new ProductAdapter(productList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Обработка ошибок
            }
        });
    }
}