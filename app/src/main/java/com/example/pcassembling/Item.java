package com.example.pcassembling;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

// Extra로 주고받기 위해 Serializable 인터페이스를 구현하는 형태로 작성
public class Item implements Serializable {
    public int index;

    public String category;
    public String name;
    public String price;
    public String spec;

    public File image_file;

    public byte[] GetItemImageBuffer() {
        byte[] byte_array = null;

        try {
            FileInputStream fis = new FileInputStream(image_file);

            byte_array = new byte[fis.available()];
            fis.read(byte_array);

            fis.close();
        } catch (IOException e) {
            Log.e("GetItemImageBuffer", this.category + "_" + this.name + "이미지를 읽는 동안 문제가 발생하였습니다.");
        }
        return byte_array;
    }

    public Item(String category, String data_line) {
        String[] splited_data = data_line.split("\\|");

        index = Integer.parseInt(splited_data[0]);
        this.category = category;
        name = splited_data[1];
        price = splited_data[2];
        spec = splited_data[3];
    }
}
