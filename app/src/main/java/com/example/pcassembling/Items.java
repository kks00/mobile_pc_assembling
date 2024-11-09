package com.example.pcassembling;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Items {
    Context context;
    File resource_items_path;

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

    public static String[] category_names = {"CPU", "그래픽카드", "HDD", "SSD", "메모리", "메인보드", "파워", "쿨러", "케이스"};
    static String[] imagefile_dirs = {"items_cpu", "items_gpu", "items_hdd", "items_ssd", "items_memory", "items_board", "items_power", "items_cooler", "items_case"};
    String[] itemlist_file_names = {
            "items_cpu.txt", "items_gpu.txt", "items_hdd.txt", "items_ssd.txt", "items_memory.txt", "items_board.txt", "items_power.txt", "items_cooler.txt", "items_case.txt"
    };
    int[] itemlist_file_res = {
            R.raw.items_cpu, R.raw.items_gpu, R.raw.items_hdd, R.raw.items_ssd, R.raw.items_memory, R.raw.items_board, R.raw.items_power, R.raw.items_cooler, R.raw.items_case
    };

    public HashMap<String, ArrayList<Item>> category_items;

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
    }
}
