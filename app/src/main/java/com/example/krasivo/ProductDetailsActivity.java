package com.example.krasivo;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageButton addToFavoritesButton;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Инициализация представлений
        ImageView imageView = findViewById(R.id.product_image);
        TextView nameTextView = findViewById(R.id.product_name);
        TextView priceTextView = findViewById(R.id.product_price);
        addToFavoritesButton = findViewById(R.id.favorite_button);

        // Получение деталей продукта из intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("product_name");
        String price = intent.getStringExtra("product_price");
        String image = intent.getStringExtra("product_image");

        // Установка деталей продукта в представления
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder)
                .override(500, 500)
                .centerCrop();

        Glide.with(this)
                .load(image)
                .apply(requestOptions)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Получение среднего цвета изображения
                        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                        int color = getDominantColor(bitmap);
                        imageView.setBackgroundColor(color);
                        return false;
                    }
                })
                .into(imageView);
        nameTextView.setText(name);
        priceTextView.setText(price);

        // Получение текущего списка избранных продуктов
        SharedPreferences preferences = getSharedPreferences("favorites", MODE_PRIVATE);
        Set<String> favoriteProducts = preferences.getStringSet("products", new HashSet<String>());

        // Проверка, находится ли текущий продукт в списке избранных
        String productString = name + "," + price + "," + image;
        isFavorite = favoriteProducts.contains(productString);

        // Установка изображения кнопки "Добавить в избранное" в соответствии с тем, добавлен ли продукт в избранное
        if (!isFavorite) {
            addToFavoritesButton.setImageResource(R.drawable.star1);
        }
        if (isFavorite) {
            addToFavoritesButton.setImageResource(R.drawable.star2);
        }
        else{
            addToFavoritesButton.setImageResource(R.drawable.star1);
        }

        // Установка OnClickListener для кнопки "Добавить в избранное"
        addToFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получение текущего списка избранных продуктов
                SharedPreferences preferences = getSharedPreferences("favorites", MODE_PRIVATE);
                Set<String> favoriteProducts = preferences.getStringSet("products", new HashSet<String>());

                // Проверка, находится ли текущий продукт в списке избранных
                String productString = name + "," + price + "," + image;
                boolean isCurrentlyFavorite = favoriteProducts.contains(productString);

                if (isCurrentlyFavorite) {
                    // Удаление продукта из списка избранных
                    removeProductFromFavorites(productString);

                    // Обновление изображения кнопки "Добавить в избранное"
                    addToFavoritesButton.setImageResource(R.drawable.star1);

                    // Показать сообщение об успешном удалении из избранного
                    Toast.makeText(ProductDetailsActivity.this, "Удалено из избранного", Toast.LENGTH_SHORT).show();
                } else {
                    // Добавление продукта в список избранных
                    addProductToFavorites(productString);

                    // Обновление изображения кнопки "Добавить в избранное"
                    addToFavoritesButton.setImageResource(R.drawable.star2);

                    // Показать сообщение об успешном добавлении в избранное
                    Toast.makeText(ProductDetailsActivity.this, "Добавлено в избранное", Toast.LENGTH_SHORT).show();
                }

                // Обновление значения переменной isFavorite
                isFavorite = !isFavorite || isCurrentlyFavorite;
            }
        });
        LinearLayout characteristicsLayout = findViewById(R.id.characteristicsLayout);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String productName = getIntent().getStringExtra("product_name");
        DatabaseReference productsRef = database.getReference("Store").child("Catalog1").child("AllTovar");
        Query productQuery = productsRef.orderByChild("name").equalTo(productName);
        productQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        DatabaseReference productRef = productSnapshot.child("characteristics").getRef();
                        productRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Map<String, String> characteristics = new HashMap<>();
                                    for (DataSnapshot characteristicSnapshot : dataSnapshot.getChildren()) {
                                        String characteristicName = characteristicSnapshot.getKey();
                                        String characteristicValue = characteristicSnapshot.getValue(String.class);
                                        characteristics.put(characteristicName, characteristicValue);
                                    }
                                    for (Map.Entry<String, String> entry : characteristics.entrySet()) {
                                        String characteristicName = entry.getKey();
                                        String characteristicValue = entry.getValue();
                                        TextView characteristicTextView = new TextView(ProductDetailsActivity.this);
                                        characteristicTextView.setText(characteristicName + ": " + characteristicValue);
                                        characteristicsLayout.addView(characteristicTextView);
                                    }
                                } else {
                                    Log.d(TAG, "No characteristics found for product: " + productName);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.w(TAG, "Failed to read characteristics data", databaseError.toException());
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "No products found with name: " + productName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read products data", databaseError.toException());
            }
        });
    }

    public int getDominantColor(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int sumR = 0, sumG = 0, sumB = 0;
        for (int i = 0; i < size; i++) {
            sumR += Color.red(pixels[i]);
            sumG += Color.green(pixels[i]);
            sumB += Color.blue(pixels[i]);
        }
        return Color.rgb(sumR / size, sumG / size, sumB / size);
    }

    private void addProductToFavorites(String productString) {
        // Получение текущего списка избранных продуктов
        SharedPreferences preferences = getSharedPreferences("favorites", MODE_PRIVATE);
        Set<String> favoriteProducts = preferences.getStringSet("products", new HashSet<String>());

        // Добавление текущего продукта в список
        favoriteProducts.add(productString);

        // Сохранение обновленного списка избранных продуктов
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("products", favoriteProducts);
        editor.apply();

        // Сохранение продукта в Firestore
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getEmail();
            String[] productDetails = productString.split(",");
            Map<String, Object> product = new HashMap<>();
            product.put("name", productDetails[0]);
            product.put("price", productDetails[1]);
            product.put("image", productDetails[2]);
            FirebaseFirestore.getInstance().collection("users").document(userId).collection("products").add(product)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Продукт успешно сохранен в Firebase");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Не удалось сохранитьпродукт в Firebase", e);
                        }
                    });
        }
    }

    private void removeProductFromFavorites(String productString) {
        // Получение текущего списка избранных продуктов
        SharedPreferences preferences = getSharedPreferences("favorites", MODE_PRIVATE);
        Set<String> favoriteProducts = preferences.getStringSet("products", new HashSet<String>());

        // Удаление текущего продукта из списка
        favoriteProducts.remove(productString);

        // Сохранение обновленного списка избранных продуктов
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("products", favoriteProducts);
        editor.apply();

        // Удаление продукта из Firestore
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getEmail();
            String[] productDetails = productString.split(",");
            FirebaseFirestore.getInstance().collection("users").document(userId).collection("products")
                    .whereEqualTo("name", productDetails[0])
                    .whereEqualTo("price", productDetails[1])
                    .whereEqualTo("image", productDetails[2])
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                documentSnapshot.getReference().delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "Продукт успешно удален из Firebase");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Не удалось удалить продукт из Firebase", e);
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Не удалось получить продукт из Firebase для удаления", e);
                        }
                    });
        }
    }
}