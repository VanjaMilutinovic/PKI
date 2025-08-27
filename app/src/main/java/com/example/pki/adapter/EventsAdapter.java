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
import com.example.pki.model.Event;
import com.example.pki.model.EventOffer;
import com.example.pki.ui.fragments.EventsFragment;

import java.util.ArrayList;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventVH> {

    private final List<Event> items = new ArrayList<>();

    @NonNull @Override
    public EventVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventVH h, int pos) {
        Event e = items.get(pos);
        EventOffer o = e.eventOfferId;

        String title = (o != null && o.name != null) ? o.name : "";
        String desc  = (o != null && o.shortDescription != null) ? o.shortDescription : "";
        h.title.setText(title);
        h.desc.setText(desc);

        // Slika: URL ili base64 data:
        String src = (o != null) ? EventsFragment.pickImageSource(o.photoId) : null;
        if (src == null) {
            h.image.setVisibility(View.GONE);
        } else {
            h.image.setVisibility(View.VISIBLE);
            if (src.startsWith("data:")) {
                Bitmap bmp = EventsFragment.decodeBase64Image(src);
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

    public void setItems(List<Event> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    static class EventVH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, desc;
        EventVH(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.imgEvent);
            title = v.findViewById(R.id.tvTitle);
            desc  = v.findViewById(R.id.tvDescription);
        }
    }
}
