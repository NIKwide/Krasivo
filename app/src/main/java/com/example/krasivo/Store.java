package com.example.krasivo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Store extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private CatalogAdapter Adapter;
    private List<Product> productList = new ArrayList<>();
    private List<Catalog> CatalogList = new ArrayList<>();

    private CatalogAdapter CatalogAdapter;
    private Button bStart, bSignUp, bSignIn, signOut;
    private TextView tvUserName;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference myRef1;
    private Button bCart;
    private Button bFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        init();
        recyclerView = findViewById(R.id.product_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void init() {
        recyclerView = findViewById(R.id.product_list);
        adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);

        recyclerView = findViewById(R.id.catalog);
        CatalogAdapter = new CatalogAdapter(CatalogList);
        recyclerView.setAdapter(CatalogAdapter);
        signOut = findViewById(R.id.bSignOut);
        tvUserName = findViewById(R.id.tvUserEmail);
        bStart = findViewById(R.id.bStart);
        bSignIn = findViewById(R.id.bSignIn);
        bSignUp = findViewById(R.id.bSignUp);
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.product_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        bCart = findViewById(R.id.bCart);
        bFavorites = findViewById(R.id.bFavorites);

        // Инициализация базы данных Firebase
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Store").child("Catalog1").child("AllTovar");


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
        RecyclerView recyclerView = findViewById(R.id.catalog);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        myRef1 = database.getReference("Store").child("Catalog1");
        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Catalog> CatalogList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String catalogImage = dataSnapshot.child("Image").getValue(String.class);
                    String CatalogName = dataSnapshot.child("Name").getValue(String.class);
                    Catalog catalog = new Catalog(CatalogName, catalogImage);
                    CatalogList.add(catalog);
                }

                RecyclerView recyclerView = findViewById(R.id.catalog);
                CatalogAdapter Adapter = new CatalogAdapter(CatalogList);
                recyclerView.setAdapter(Adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Обработка ошибок
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser cUser = mAuth.getCurrentUser();
        if (cUser == null) {
            Intent intent = new Intent(Store.this, LogoActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        String email = sharedPreferences.getString("email", null);
        String password =sharedPreferences.getString("password", null);

        if (email != null && password != null) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null && user.isEmailVerified()) {
                                    // Пользователь успешно вошел в систему и имеет доступ к активности Store

                                } else {
                                    // Пользователь не подтвердил адрес электронной почты
                                    Toast.makeText(getApplicationContext(), "Пожалуйста, подтвердите адрес электронной почты", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Ошибка при проверке подлинности
                                Toast.makeText(getApplicationContext(), "Ошибка входа пользователя", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    public void onClickStart(View view) {
        Intent i = new Intent(Store.this, MainActivity.class);
        startActivity(i);
    }
    public void onClickCart(View view) {
        Intent intent = new Intent(Store.this, CartActivity.class);
        startActivity(intent);
    }

    public void onClickFavorites(View view) {
        Intent intent = new Intent(Store.this, FavoritesActivity.class);
        startActivity(intent);
    }
}