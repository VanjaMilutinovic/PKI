package com.example.pki.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pki.R;
import com.example.pki.model.Notification;
import com.example.pki.ui.fragments.NotificationsFragment;

import java.util.ArrayList;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotifVH> {

    public interface OnNotificationAction {
        void onToggleRead(Notification n, int position);
    }

    private final List<Notification> items = new ArrayList<>();
    private final OnNotificationAction listener;

    public NotificationsAdapter(OnNotificationAction l) { this.listener = l; }

    @NonNull @Override
    public NotifVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotifVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifVH h, int position) {
        Notification n = items.get(position);

        String when = n.dateTime != null ? n.dateTime.toString() : "";
        h.tvDate.setText(NotificationsFragment.formatDateTimePretty(when));
        h.tvMessage.setText(n.message != null ? n.message : "");

        boolean unread = !n.isRead;
        h.tvMessage.setTypeface(null, unread ? Typeface.BOLD : Typeface.NORMAL);
        h.dot.setVisibility(unread ? View.VISIBLE : View.INVISIBLE);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onToggleRead(n, h.getBindingAdapterPosition());
        });
    }

    @Override public int getItemCount() { return items.size(); }

    public void setItems(List<Notification> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    static class NotifVH extends RecyclerView.ViewHolder {
        TextView tvDate, tvMessage;
        View dot;
        NotifVH(@NonNull View v) {
            super(v);
            tvDate    = v.findViewById(R.id.tvDate);
            tvMessage = v.findViewById(R.id.tvMessage);
            dot       = v.findViewById(R.id.dotView);
        }
    }
}
