package com.example.krasivo;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesActivity extends AppCompatActivity {

    private ListView favoritesListView;
    private ArrayAdapter<String> favoritesAdapter;
    private FirebaseFirestore db;
    private Button clearFavoritesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesListView = findViewById(R.id.favorites_list);
        favoritesAdapter = new FavoritesAdapter(this, new ArrayList<String>());
        favoritesListView.setAdapter(favoritesAdapter);

        // Получение списка избранных продуктов пользователя из Firestore
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getEmail();
            db.collection("users").document(userId).collection("products").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<String> favoriteProducts = new ArrayList<>();
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String name = documentSnapshot.getString("name");
                                String price = documentSnapshot.getString("price");
                                String image = documentSnapshot.getString("image");
                                String productString = name + "," + price + "," + image;
                                favoriteProducts.add(productString);
                            }
                            favoritesAdapter.addAll(favoriteProducts);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Не удалось получить список избранных продуктов из Firestore", e);
                        }
                    });
        }

        clearFavoritesButton = findViewById(R.id.clear_favorites_button);
        clearFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFavorites();
            }
        });
        
    }

    private void clearFavorites() {
        // Удаляем все избранное пользователя из Firestore
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getEmail();
            db.collection("users").document(userId).collection("products").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<String> productIds = new ArrayList<>();
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String productId = documentSnapshot.getId();
                                productIds.add(productId);
                            }
                            for (String productId : productIds) {
                                db.collection("users").document(userId).collection("products").document(productId).delete();
                            }
                            favoritesAdapter.clear();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Не удалось удалить список избранных продуктов из Firestore", e);
                        }
                    });
        }
    }
}