package pki.adapter;

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
import com.example.pki.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SchedulingAdapter extends RecyclerView.Adapter<SchedulingAdapter.VH> {

    public interface OnSchedulingAction {
        void onApprove(Event e, int position);
        void onDecline(Event e, int position);
    }

    private final List<Event> items = new ArrayList<>();
    private final OnSchedulingAction listener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public SchedulingAdapter(OnSchedulingAction l) { this.listener = l; }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scheduling_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Event e = items.get(position);
        EventOffer offer = e.eventOfferId;
        User cust = e.customerId;

        String name   = (offer != null && offer.name != null) ? offer.name : "";
        String user   = (cust != null ? (nz(cust.firstName) + " " + nz(cust.lastName)).trim() : "");
        String date   = (e.date != null) ? sdf.format(e.date) : "";
        String people = String.valueOf(e.numberOfPeople);

        h.tvName.setText(name);
        h.tvUser.setText(user);
        h.tvDate.setText(date);
        h.tvPeople.setText(people);

        h.btnApprove.setOnClickListener(v -> {
            if (listener == null) return;
            int pos = h.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onApprove(items.get(pos), pos);
            }
        });
        h.btnDecline.setOnClickListener(v -> {
            if (listener == null) return;
            int pos = h.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onDecline(items.get(pos), pos);
            }
        });
    }

    @Override public int getItemCount() { return items.size(); }

    /** Postavi celu listu (pravi kopiju) */
    public void setItems(List<Event> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    /** Ukloni na poziciji i obavesti adapter – vraća uklonjeni Event. */
    public Event removeAt(int position) {
        if (position < 0 || position >= items.size()) return null;
        Event removed = items.remove(position);
        notifyItemRemoved(position);
        // Opciono: da bi se korektno rebind-ovale preostale pozicije
        notifyItemRangeChanged(position, items.size() - position);
        return removed;
    }

    /** Da li je lista prazna */
    public boolean isEmpty() { return items.isEmpty(); }

    private static String nz(String s) { return s == null ? "" : s; }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvUser, tvDate, tvPeople;
        ImageButton btnApprove, btnDecline;
        VH(@NonNull View v) {
            super(v);
            tvName   = v.findViewById(R.id.tvName);
            tvUser   = v.findViewById(R.id.tvUser);
            tvDate   = v.findViewById(R.id.tvDate);
            tvPeople = v.findViewById(R.id.tvPeople);
            btnApprove = v.findViewById(R.id.btnApprove);
            btnDecline = v.findViewById(R.id.btnDecline);
        }
    }
}
