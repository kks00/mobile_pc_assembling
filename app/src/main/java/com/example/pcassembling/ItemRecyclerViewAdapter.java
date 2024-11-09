package com.example.pcassembling;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {
    Context context;
    ArrayList<Item> items_list;

    interface OnItemClicked {
        public void onItemClick(View v, int i);
    }
    OnItemClicked onItemClicked;
    public void setItemClicked(OnItemClicked onItemClicked) {
        this.onItemClicked = onItemClicked;
    }

    public ItemRecyclerViewAdapter(Context context, Items items, String category_name) {
        this.context = context;
        if (!items.category_items.containsKey(category_name)) {
            return;
        }
        items_list = items.category_items.get(category_name);
    }

    @NonNull
    @Override
    public ItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRecyclerViewAdapter.ViewHolder holder, int position) {
        Item curr_item = items_list.get(position);
        if (curr_item != null) {
            String view_name = curr_item.name;
            // 아이템 이름 괄호 앞까지만 자르기
            if (view_name.indexOf('(') >= 0)
                view_name = view_name.substring(0, view_name.indexOf('('));
            holder.tvItemName.setText(view_name);

            holder.tvItemPrice.setText(curr_item.price);
            holder.tvItemCategory.setText(curr_item.category);
            holder.tvItemPrice.setText(curr_item.price);

            byte[] image_buf = curr_item.GetItemImageBuffer();
            if (image_buf != null)
                holder.imgItemImage.setImageBitmap(BitmapFactory.decodeByteArray(image_buf, 0, image_buf.length));
        }
    }

    @Override
    public int getItemCount() {
        return items_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        ImageView imgItemImage;
        TextView tvItemName, tvItemCategory, tvItemPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgItemImage = itemView.findViewById(R.id.imgItemImage);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemCategory = itemView.findViewById(R.id.tvItemCategory);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClicked != null)
                onItemClicked.onItemClick(view, getAdapterPosition());
        }
    }

}
