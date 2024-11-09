package com.example.pcassembling;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

// 압축 해제 by ChatGPT.
public class UnzipUtil {
    public static void unzip(InputStream zipInputStream, File dest) throws IOException {
        try (ZipInputStream zipIn = new ZipInputStream(zipInputStream)) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                String filePath = dest.getAbsolutePath() + File.separator + entry.getName();

                if (entry.isDirectory()) {
                    // 디렉토리인 경우 디렉토리를 생성
                    new File(filePath).mkdirs();
                } else {
                    // 파일인 경우 파일을 생성하고 데이터 쓰기
                    File newFile = new File(filePath);
                    new File(newFile.getParent()).mkdirs(); // 필요한 상위 디렉토리 생성
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFile))) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = zipIn.read(buffer)) != -1) {
                            bos.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }
}