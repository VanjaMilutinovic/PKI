package com.example.pki.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pki.R;
import com.example.pki.model.Event;
import com.example.pki.model.EventOffer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartVH> {

    public interface OnCartAction {
        void onRemove(Event e);
    }

    private final List<Event> items = new ArrayList<>();
    private final OnCartAction listener;

    public CartAdapter(OnCartAction l) { this.listener = l; }

    @NonNull @Override
    public CartVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_row, parent, false);
        return new CartVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartVH h, int position) {
        Event e = items.get(position);
        EventOffer o = e.eventOfferId;

        String name  = (o != null && o.name != null) ? o.name : "";
        String date  = formatDateDMY(e.date);         // e.date je java.util.Date
        int people   = e.numberOfPeople;              // primitivan int – nema null
        double price = (o != null) ? o.price : 0.0;   // ako o nema, prikaži 0.0

        h.tvName.setText(name);
        h.tvDate.setText(date);
        h.tvPeople.setText(String.valueOf(people));
        h.tvPrice.setText(h.itemView.getContext().getString(R.string.price_eur_format, price));

        h.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onRemove(e);
        });
    }

    @Override public int getItemCount() { return items.size(); }

    public void setItems(List<Event> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    private static String formatDateDMY(java.util.Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    static class CartVH extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvPeople, tvPrice;
        ImageButton btnDelete;
        CartVH(@NonNull View v) {
            super(v);
            tvName   = v.findViewById(R.id.tvName);
            tvDate   = v.findViewById(R.id.tvDate);
            tvPeople = v.findViewById(R.id.tvPeople);
            tvPrice  = v.findViewById(R.id.tvPrice);
            btnDelete= v.findViewById(R.id.btnDelete);
        }
    }
}
