package com.example.pcassembling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

        // 스크린샷 저장하기
        Button btnTakeScreenshot = findViewById(R.id.btnTakeScreenshot);
        btnTakeScreenshot.setOnClickListener((view) -> {
            Bitmap screenshot_bitmap = GetScreenShot.getRecyclerViewScreenshot(cartItemRecyclerView);
            String screenshot_path = GetScreenShot.saveScreenshot(this, screenshot_bitmap);
            if (screenshot_path.length() > 0) {
                Toast.makeText(getApplicationContext(), screenshot_path + "에 스크린샷을 저장하였습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "스크린샷 저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnGoBackList = findViewById(R.id.btnGoBackList);
        btnGoBackList.setOnClickListener((view) -> {
            finish();
        });
    }
}