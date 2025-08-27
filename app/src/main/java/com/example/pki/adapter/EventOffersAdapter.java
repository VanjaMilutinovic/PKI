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
import com.example.pki.model.EventOffer;
import com.example.pki.ui.fragments.EventOffersFragment;

import java.util.ArrayList;
import java.util.List;

public class EventOffersAdapter extends RecyclerView.Adapter<EventOffersAdapter.OfferVH> {

    public interface OnEventOfferClick {
        void onOpen(EventOffer offer);
    }

    private final List<EventOffer> items = new ArrayList<>();
    private final OnEventOfferClick listener;

    public EventOffersAdapter(OnEventOfferClick l) {
        this.listener = l;
    }

    @NonNull @Override
    public OfferVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_offer, parent, false);
        return new OfferVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferVH h, int pos) {
        EventOffer e = items.get(pos);
        h.title.setText(e.name != null ? e.name : "");
        h.desc.setText(e.shortDescription != null ? e.shortDescription : "");
        h.viewMore.setOnClickListener(v -> {
            if (listener != null) listener.onOpen(e);
        });

        String src = EventOffersFragment.pickImageSource(e.photoId);
        if (src == null) {
            h.image.setVisibility(View.GONE);
        } else {
            h.image.setVisibility(View.VISIBLE);
            if (src.startsWith("data:")) {
                Bitmap bmp = EventOffersFragment.decodeBase64Image(src);
                if (bmp != null) h.image.setImageBitmap(bmp);
                else h.image.setVisibility(View.GONE);
            } else {
                Glide.with(h.image.getContext()).load(src).centerCrop().into(h.image);
            }
        }
    }

    @Override public int getItemCount() { return items.size(); }

    public void setItems(List<EventOffer> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    static class OfferVH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, desc, viewMore;
        OfferVH(@NonNull View v) {
            super(v);
            image    = v.findViewById(R.id.imgOffer);
            title    = v.findViewById(R.id.tvTitle);
            desc     = v.findViewById(R.id.tvDescription);
            viewMore = v.findViewById(R.id.tvViewMore);
        }
    }
}
