package com.example.pki.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pki.R;
import com.example.pki.model.Promotion;
import com.example.pki.ui.fragments.PromotionsFragment;

import java.util.ArrayList;
import java.util.List;

public class PromotionsAdapter extends RecyclerView.Adapter<PromotionsAdapter.PromoVH> {

    private final List<Promotion> items = new ArrayList<>();

    @NonNull @Override
    public PromoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_promotion, parent, false);
        return new PromoVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoVH h, int pos) {
        Promotion p = items.get(pos);
        h.title.setText(p.name != null ? p.name : "");
        h.desc.setText(p.description != null ? p.description : "");

        // Slika: URL ili base64 data:
        String src = PromotionsFragment.pickImageSource(p.photoId);
        if (src == null) {
            h.image.setVisibility(View.GONE);
        } else {
            h.image.setVisibility(View.VISIBLE);
            if (src.startsWith("data:")) {
                Bitmap bmp = PromotionsFragment.decodeBase64Image(src);
                if (bmp != null) {
                    h.image.setImageBitmap(bmp);
                } else {
                    h.image.setVisibility(View.GONE);
                }
            } else {
                Glide.with(h.image.getContext())
                        .load(src)
                        .centerCrop()
                        .into(h.image);
            }
        }
    }

    @Override public int getItemCount() { return items.size(); }

    public void setItems(List<Promotion> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    static class PromoVH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, desc;
        PromoVH(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.imgPromotion);
            title = v.findViewById(R.id.tvTitle);
            desc  = v.findViewById(R.id.tvDescription);
        }
    }
}
