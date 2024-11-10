package com.example.pcassembling;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartItemRecyclerViewAdapter extends RecyclerView.Adapter<CartItemRecyclerViewAdapter.ViewHolder> {
    Context context;
    ArrayList<Item> items_list;

    interface OnItemClicked {
        public void onItemClick(View v, int i);
    }
    OnItemClicked onItemClicked;
    public void setItemClicked(OnItemClicked onItemClicked) {
        this.onItemClicked = onItemClicked;
    }

    public CartItemRecyclerViewAdapter(Context context, ArrayList<Item> items_list) {
        this.context = context;
        this.items_list = items_list;
    }

    @NonNull
    @Override
    public CartItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemRecyclerViewAdapter.ViewHolder holder, int position) {
        Item curr_item = items_list.get(position);
        if (curr_item != null) {
            String view_name = curr_item.name;
            // 아이템 이름 괄호 앞까지만 자르기
            if (view_name.indexOf('(') >= 0)
                view_name = view_name.substring(0, view_name.indexOf('('));
            holder.tvCartItemName.setText(view_name);

            holder.tvCartItemPrice.setText(curr_item.price);
            holder.tvCartItemCategory.setText(curr_item.category);

            byte[] image_buf = curr_item.GetItemImageBuffer();
            if (image_buf != null)
                holder.imgCartItemImage.setImageBitmap(BitmapFactory.decodeByteArray(image_buf, 0, image_buf.length));

            // 장바구니 삭제 버튼 이벤트 등록
            holder.btnCartItemRemove.setOnClickListener((view) -> {
                CartItemDB.instance.removeCartItem(items_list.get(position));
                items_list = CartItemDB.instance.getCartItems();

                notifyDataSetChanged(); // RecyclerView 새로고침

                // 장바구니에 담긴 상품 금액의 합계 업데이트
                activity_cart parent = (activity_cart)context;
                parent.set_cart_sum();
            });
        }
    }

    @Override
    public int getItemCount() {
        return items_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        ImageView imgCartItemImage;
        TextView tvCartItemName, tvCartItemCategory, tvCartItemPrice;
        Button btnCartItemRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgCartItemImage = itemView.findViewById(R.id.imgCartItemImage);
            tvCartItemName = itemView.findViewById(R.id.tvCartItemName);
            tvCartItemCategory = itemView.findViewById(R.id.tvCartItemCategory);
            tvCartItemPrice = itemView.findViewById(R.id.tvCartItemPrice);
            btnCartItemRemove = itemView.findViewById(R.id.btnCartItemRemove);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClicked != null)
                onItemClicked.onItemClick(view, getAdapterPosition());
        }
    }

}
