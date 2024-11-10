package com.example.pcassembling;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

// 장바구니에 담긴 아이템들을 저장하기 위해  SQLite DB 사용
class CartItemDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "cart_items";
    private static final int DB_VERSION = 1;

    public static CartItemDB instance; // 애플리케이션 전체에서 사용하기 위해 static으로 인스턴스 필드 생성해둠
    static Items items; // 필요한 필드도 static으로 선언

    public CartItemDB(@Nullable Context context, Items items) {
        super(context, DB_NAME, null, DB_VERSION);
        this.items = items;
        instance = this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE CartItems (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "category TEXT," +
                "category_index TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CartItems");
        onCreate(db);
    }

    // 장바구니 테이블에 존재하는 모든 아이템의 ArrayList를 반환하는 함수
    public ArrayList<Item> getCartItems() {
        SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
        } catch (SQLiteException e) {
            db = this.getReadableDatabase();
        }

        ArrayList<Item> cart_items = new ArrayList<Item>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM CartItems", null);
            while (cursor.moveToNext()) {
                String curr_category = cursor.getString(1);
                int curr_index = Integer.parseInt(cursor.getString(2));

                if (!items.category_items.containsKey(curr_category)) {
                    Log.d("getCartItems", curr_category + " 카테고리를 찾을 수 없습니다.");
                    continue;
                }

                Item curr_item = items.category_items.get(curr_category).get(curr_index);
                cart_items.add(curr_item);
            }
        } catch (Exception e) {
            Log.e("getCartItems", "장바구니 아이템을 가져오는 도중 오류가 발생하였습니다.");
        }
        return cart_items;
    }

    // 장바구니에 아이템을 추가하는 함수
    public boolean addCartItem(Item item) {
        SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
        } catch (SQLiteException e) {
            db = this.getReadableDatabase();
        }

        try {
            db.execSQL("INSERT INTO CartItems(category, category_index) VALUES(" +
                    "'" + item.category + "', " +
                    "'" + item.index + "'" +
                    ")"
            );
            Log.d("addCartItem", "장바구니에 아이템을 추가하였습니다.");
            return true;
        } catch (SQLiteException e) {
            Log.e("addCartItem", "장바구니에 아이템을 추가하는 동안 오류가 발생하였습니다.");
        }
        return false;
    }

    // 장바구니에서 아이템을 삭제하는 함수
    public boolean removeCartItem(Item item) {
        SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
        } catch (SQLiteException e) {
            db = this.getReadableDatabase();
        }

        try {
            db.execSQL("DELETE FROM CartItems WHERE " +
                    "category='" + item.category + "' " +
                    "AND category_index='" + item.index + "'"
            );
            Log.d("removeCartItem", "장바구니에서 아이템을 삭제하였습니다.");
            return true;
        } catch (SQLiteException e) {
            Log.e("removeCartItem", "장바구니에서 아이템을 삭제하는 동안 오류가 발생하였습니다.");
        }
        return false;
    }
}

public class Items {
    Context context;
    File resource_items_path;

    public static String[] category_names = {"CPU", "그래픽카드", "HDD", "SSD", "메모리", "메인보드", "파워", "쿨러", "케이스"};
    static String[] imagefile_dirs = {"items_cpu", "items_gpu", "items_hdd", "items_ssd", "items_memory", "items_board", "items_power", "items_cooler", "items_case"};
    static String[] itemlist_file_names = {
            "items_cpu.txt", "items_gpu.txt", "items_hdd.txt", "items_ssd.txt", "items_memory.txt", "items_board.txt", "items_power.txt", "items_cooler.txt", "items_case.txt"
    };
    static int[] itemlist_file_res = {
            R.raw.items_cpu, R.raw.items_gpu, R.raw.items_hdd, R.raw.items_ssd, R.raw.items_memory, R.raw.items_board, R.raw.items_power, R.raw.items_cooler, R.raw.items_case
    };

    public HashMap<String, ArrayList<Item>> category_items;
    public CartItemDB cartItemDB;

    public Boolean init_item_list_files(String out_file_name, int res_id) {
        try {
            File out_file = new File(resource_items_path, out_file_name);
            if (out_file.exists()) {
                Log.d("init_item_list_files", out_file.getAbsolutePath() + " 파일이 이미 존재합니다.");
                return true;
            }

            InputStream fis = context.getResources().openRawResource(res_id);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);

            FileOutputStream fos = new FileOutputStream(out_file);
            fos.write(buffer);
            fos.close();

            fis.close();

            Log.d("init_item_list_files", out_file.getAbsolutePath() + " 파일을 작성하였습니다.");
            return true;
        } catch (IOException e) {
            Log.e("init_item_list_files", out_file_name + " 파일을 작성하는 동안 오류가 발생하였습니다.");
        }
        return false;
    }

    public Boolean init_item_image_files() {
        File out_dir = new File(resource_items_path, "images");
        if (out_dir.exists()) {
            Log.d("init_item_image_files", out_dir.getAbsolutePath() + " 디렉토리가 이미 존재합니다.");
            return true;
        }
        out_dir.mkdirs(); // 디렉토리 없으면 생성

        InputStream images_zip_file = context.getResources().openRawResource(R.raw.images);
        try {
            UnzipUtil.unzip(images_zip_file, out_dir); // 압축 해제
            return true;
        } catch (IOException e) {
            Log.e("init_item_image_files", "images 파일 압축을 푸는동안 오류가 발생하였습니다.");
        }
        return false;
    }

    public boolean init_category_items(int category_index) {
        String curr_category_name = category_names[category_index];
        Log.d("init_category_items", "category_name= " + curr_category_name);

        ArrayList<Item> curr_category_items = new ArrayList<Item>();
        category_items.put(curr_category_name, curr_category_items);

        File curr_category_items_file = new File(resource_items_path, itemlist_file_names[category_index]);
        try {
            FileInputStream fis = new FileInputStream(curr_category_items_file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            // 한 줄씩 읽어서 아이템 객체화하여 ArrayList에 추가
            String curr_line;
            int index = 0;
            while ((curr_line = br.readLine()) != null) {
                Item new_item = new Item(curr_category_name, curr_line);

                // 아이템 이미지 파일 객체 미리 생성해두기
                new_item.image_file = new File(resource_items_path, "images/" + imagefile_dirs[category_index] + "/" + index + ".jpg");
                if (!new_item.image_file.exists()) {
                    Log.d("init_category_items", "이미지 파일이 존재하지 않습니다. path=" + new_item.image_file.getAbsolutePath());
                }

                category_items.get(curr_category_name).add(new_item);
                index++;
            }

            br.close();
            isr.close();
            fis.close();

            return true;
        } catch (IOException e) {
            Log.e("init_category_items", curr_category_name + " 카테고리의 아이템 리스트를 읽는 동안 오류가 발생하였습니다.");
        }
        return false;
    }

    public Items(Context context) {
        this.context = context;
        resource_items_path = context.getExternalFilesDir(null);

        // Resource raw의 아이템 리스트 파일들을 외장 메모리의 files 디렉토리로 꺼내기
        for (int i =0; i < itemlist_file_names.length; i++)
            init_item_list_files(itemlist_file_names[i], itemlist_file_res[i]);

        // Resource raw의 아이템 이미지 파일 압축풀기
         init_item_image_files();

         // 아이템 리스트 파일을 읽어 아이템 객체화
        category_items = new HashMap<String, ArrayList<Item>>();
        for (int  i = 0; i < category_names.length; i++)
            init_category_items(i);

        // CartItemDB 객체 생성
        cartItemDB = new CartItemDB(context, this);
        Log.d("Items.Constructor", "장바구니 아이템 개수: " + cartItemDB.getCartItems().size());
    }
}
