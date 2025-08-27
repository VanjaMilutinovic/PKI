package com.example.pki.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pki.R;
import com.example.pki.model.Event;

import java.util.ArrayList;
import java.util.List;

public class EventCommentsAdapter extends RecyclerView.Adapter<EventCommentsAdapter.CmtVH> {

    private final List<Event> items = new ArrayList<>();

    @NonNull
    @Override
    public CmtVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_comment, parent, false);
        return new CmtVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CmtVH h, int position) {
        Event e = items.get(position);

        String fn = (e.customerId != null && e.customerId.firstName != null) ? e.customerId.firstName : "";
        String ln = (e.customerId != null && e.customerId.lastName  != null) ? e.customerId.lastName  : "";
        String user = (fn + " " + ln).trim();
        if (user.isEmpty()) user = h.itemView.getContext().getString(R.string.unknown_user);

        h.tvUser.setText(user);
        h.tvGrade.setText(e.grade != null ? String.valueOf(e.grade) : "N/A");
        h.tvComment.setText(e.comment != null ? e.comment : h.itemView.getContext().getString(R.string.no_comment));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Event> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    static class CmtVH extends RecyclerView.ViewHolder {
        TextView tvUser, tvGrade, tvComment;
        CmtVH(@NonNull View v) {
            super(v);
            tvUser    = v.findViewById(R.id.tvUser);
            tvGrade   = v.findViewById(R.id.tvGrade);
            tvComment = v.findViewById(R.id.tvComment);
        }
    }
}
