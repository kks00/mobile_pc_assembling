package com.example.pcassembling;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Items items;
    RecyclerView itemsRecyclerView;
    ItemRecyclerViewAdapter itemRecyclerViewAdapter;

    Spinner spCategory;

    static final int DETAIL_RESULT = 1;
    
    // 표시되는 아이템 목록 변경함수
    public void change_category(String category_name) {
        itemRecyclerViewAdapter = new ItemRecyclerViewAdapter(this, items, category_name);

        // 아이템 선택 시 상세정보 액티비티로 이동
        itemRecyclerViewAdapter.setItemClicked(new ItemRecyclerViewAdapter.OnItemClicked() {
            @Override
            public void onItemClick(View v, int i) {
                Item selected_item = itemRecyclerViewAdapter.items_list.get(i);

                Intent intent = new Intent(MainActivity.this, activity_itemdetail.class);
                intent.putExtra("SELECTED_ITEM", selected_item);
                startActivityForResult(intent, DETAIL_RESULT);
            }
        });

        itemsRecyclerView.setAdapter(itemRecyclerViewAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 상세정보 액티비티에서 선택한 버튼에 따라 처리
        if (requestCode == DETAIL_RESULT) {
            if (resultCode == RESULT_OK) {
                // 장바구니에 담기
                Item selected_item = (Item)data.getSerializableExtra("SELECTED_ITEM");
                items.cartItemDB.addCartItem(selected_item);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 아이템 목록 초기화
        items = new Items(this);

        // 장바구니로 이동
        Button btnGotoCart = findViewById(R.id.btnGotoCart);
        btnGotoCart.setOnClickListener((view) -> {
            Intent intent = new Intent(MainActivity.this, activity_cart.class);
            startActivity(intent);
        });

        spCategory = findViewById(R.id.spCategory);
        ArrayAdapter<String> category_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items.category_names);
        category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(category_adapter);
        // 카테고리 선택 시 표시되는 아이템 목록 변경
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected_category = category_adapter.getItem(i);
                change_category(selected_category);
            }
        });

        // 아이템 목록 RecyclerView 초기화
        itemsRecyclerView = findViewById(R.id.itemsRecyclerView);
        itemsRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        change_category("CPU");
    }
}