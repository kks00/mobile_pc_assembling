package com.example.pcassembling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class activity_itemdetail extends AppCompatActivity {
    ImageView imgDetailItemImage;
    TextView tvDetailItemName, tvDetailItemPrice, tvDetailItemSpec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemdetail);

        Intent intent = getIntent();
        Item selected_item = (Item)intent.getSerializableExtra("SELECTED_ITEM");

        imgDetailItemImage = findViewById(R.id.imgDetailItemImage);

        byte[] image_buf = selected_item.GetItemImageBuffer();
        if (image_buf != null)
            imgDetailItemImage.setImageBitmap(BitmapFactory.decodeByteArray(image_buf, 0, image_buf.length));

        tvDetailItemName = findViewById(R.id.tvDetailItemName);
        tvDetailItemPrice = findViewById(R.id.tvDetailItemPrice);
        tvDetailItemSpec = findViewById(R.id.tvDetailItemSpec);
        
        tvDetailItemName.setText(selected_item.name);
        tvDetailItemPrice.setText(selected_item.price);
        tvDetailItemSpec.setText(selected_item.spec);

        Button btnAddToCart = findViewById(R.id.btnAddToCart);
        btnAddToCart.setOnClickListener((view) -> {
            setResult(RESULT_OK);
            finish();
        });

        Button btnGoBack = findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener((view) -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}