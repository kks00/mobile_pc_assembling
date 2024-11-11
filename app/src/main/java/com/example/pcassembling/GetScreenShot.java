package com.example.pcassembling;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

// 스크린샷 찰영을 위한 함수들 by ChatGPT.
public class GetScreenShot {
    public static String saveScreenshot(Context context, Bitmap bitmap) {
        // 저장할 디렉토리 및 파일 이름 설정
        File path = new File(context.getExternalFilesDir(null), "/Screenshots");
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, "screenshot_" + System.currentTimeMillis() + ".png");
        Log.d("saveScreenshot", "Path: " + file.getAbsolutePath());

        // Bitmap을 PNG 파일로 저장
        try (FileOutputStream fos = new FileOutputStream(file)) {
            Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(bitmap, 0, 0, null);

            newBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static Bitmap getRecyclerViewScreenshot(RecyclerView recyclerView) {
        // 전체 RecyclerView의 높이 계산
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        int itemCount = adapter.getItemCount();
        int totalHeight = 0;

        // 각 아이템의 높이를 합산하여 RecyclerView 전체 높이 측정
        for (int i = 0; i < itemCount; i++) {
            RecyclerView.ViewHolder holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i));
            adapter.onBindViewHolder(holder, i);
            holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            totalHeight += holder.itemView.getMeasuredHeight();
        }

        // Bitmap 생성
        Bitmap bitmap = Bitmap.createBitmap(recyclerView.getWidth(), totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        recyclerView.setDrawingCacheEnabled(true);

        // 각 아이템을 캔버스에 그리기
        int yOffset = 0;
        for (int i = 0; i < itemCount; i++) {
            RecyclerView.ViewHolder holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i));
            adapter.onBindViewHolder(holder, i);
            holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
            holder.itemView.draw(canvas);
            canvas.translate(0, holder.itemView.getHeight());
        }

        recyclerView.setDrawingCacheEnabled(false);
        return bitmap;
    }
}
