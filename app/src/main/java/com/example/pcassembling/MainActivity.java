package com.example.pcassembling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    
    // 표시되는 아이템 목록 변경함수
    public void change_category(String category_name) {
        itemRecyclerViewAdapter = new ItemRecyclerViewAdapter(this, items, category_name);
        itemsRecyclerView.setAdapter(itemRecyclerViewAdapter);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 아이템 목록 초기화
        items = new Items(this);

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