package com.example.krasivo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FavoritesAdapter extends ArrayAdapter<String> {

    private Context mContext;

    public FavoritesAdapter(Context context, List<String> objects) {
        super(context, 0, objects);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.favorite_list_item, parent, false);
        }

        // Получение данных продукта из строки в адаптере
        String productString = getItem(position);
        String[] productParts = productString.split(",");
        String productName = productParts[0];
        String productPrice = productParts[1];
        String productImage = productParts[2];

        // Нахождение элементов макета
        ImageView productImageView = convertView.findViewById(R.id.product_image_view);
        TextView productNameTextView = convertView.findViewById(R.id.product_name_text_view);
        TextView productPriceTextView = convertView.findViewById(R.id.product_price_text_view);

        // Заполнение элементов макета данными продукта
        productNameTextView.setText(productName);
        productPriceTextView.setText(productPrice);

        // Загрузка изображения продукта из сети
        Glide.with(mContext)
                .load(productImage)
                .placeholder(R.drawable.star2)
                .error(R.drawable.star1)
                .into(productImageView);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Получение данных продукта из строки в адаптере
                String productString = getItem(position);
                String[] productParts = productString.split(",");
                String productName = productParts[0];
                String productPrice = productParts[1];
                String productImage = productParts[2];

                // Создание интента для открытия ProductDetailsActivity
                Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                intent.putExtra("product_name", productName);
                intent.putExtra("product_price", productPrice);
                intent.putExtra("product_image", productImage);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }
}