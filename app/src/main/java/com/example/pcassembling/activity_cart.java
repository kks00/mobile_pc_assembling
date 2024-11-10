package com.example.pcassembling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class activity_cart extends AppCompatActivity {
    RecyclerView cartItemRecyclerView;
    TextView tvCartSum;

    // 장바구니에 담긴 상품 금액의 합계를 표시하는 함수
    public void set_cart_sum() {
        int sum = 0;
        ArrayList<Item> items_list = CartItemDB.instance.getCartItems();
        for (Item item: items_list) {
            sum += Integer.parseInt(item.price.replace(",", ""));
        }

        // int값을 원 단위로 변환
        NumberFormat formatter = NumberFormat.getInstance(Locale.KOREA);
        String formattedAmount = formatter.format(sum);

        tvCartSum.setText(formattedAmount);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        tvCartSum = findViewById(R.id.tvCartSum);
        set_cart_sum();

        ArrayList<Item> items_list = CartItemDB.instance.getCartItems();

        cartItemRecyclerView = findViewById(R.id.cartItemRecyclerView);
        cartItemRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        CartItemRecyclerViewAdapter cartItemRecyclerViewAdapter = new CartItemRecyclerViewAdapter(this, items_list);
        cartItemRecyclerView.setAdapter(cartItemRecyclerViewAdapter);

        Button btnGoBackList = findViewById(R.id.btnGoBackList);
        btnGoBackList.setOnClickListener((view) -> {
            finish();
        });
    }
}