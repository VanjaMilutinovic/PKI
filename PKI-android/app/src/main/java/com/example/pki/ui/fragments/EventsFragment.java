package com.example.pki.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pki.R;
import com.example.pki.adapter.EventsAdapter;
import com.example.pki.model.Event;
import com.example.pki.model.Photo;
import com.example.pki.model.EventOffer;
import com.example.pki.network.ApiClient;
import com.example.pki.service.BackendService;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsFragment extends Fragment {

    private static final long ROTATE_MS = 5000L; // 5s
    private static final Pattern TAGS = Pattern.compile("<[^>]*>");

    private RecyclerView recycler;
    private TextView tvError;
    private CircularProgressIndicator progress;
    private EventsAdapter adapter;

    private BackendService backend;

    private final List<Event> allEvents = new ArrayList<>();
    private int currentIndex = 0;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable rotator = new Runnable() {
        @Override public void run() {
            if (allEvents.isEmpty()) return;
            currentIndex = (currentIndex + 1) % allEvents.size();
            updateWindow();
            handler.postDelayed(this, ROTATE_MS);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        backend  = ApiClient.getService();

        recycler = v.findViewById(R.id.eventsRecycler);
        tvError  = v.findViewById(R.id.tvErrorMsg);
        progress = v.findViewById(R.id.eventsProgress);

        int span = getResources().getConfiguration().screenWidthDp >= 600 ? 2 : 1;
        recycler.setLayoutManager(new GridLayoutManager(requireContext(), span));
        adapter = new EventsAdapter();
        recycler.setAdapter(adapter);

        loadEvents();
    }

    @Override public void onResume() {
        super.onResume();
        if (allEvents.size() > 3) {
            handler.removeCallbacks(rotator);
            handler.postDelayed(rotator, ROTATE_MS);
        }
    }

    @Override public void onPause() {
        super.onPause();
        handler.removeCallbacks(rotator);
    }

    private void loadEvents() {
        showLoading(true);
        backend.getEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                showLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    tvError.setText(R.string.events_load_error);
                    tvError.setVisibility(View.VISIBLE);
                    return;
                }
                allEvents.clear();
                allEvents.addAll(cleanDescriptions(response.body()));
                currentIndex = 0;
                updateWindow();

                if (allEvents.size() > 3) {
                    handler.removeCallbacks(rotator);
                    handler.postDelayed(rotator, ROTATE_MS);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                showLoading(false);
                tvError.setText(getString(R.string.network_error_generic));
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        tvError.setVisibility(View.GONE);
        recycler.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    }

    /** Skida HTML tagove/entitete iz shortDescription i skraćuje na 150 char. */
    private List<Event> cleanDescriptions(List<Event> src) {
        List<Event> out = new ArrayList<>(src.size());
        for (Event e : src) {
            EventOffer offer = e.eventOfferId;
            if (offer != null && offer.shortDescription != null) {
                String desc = offer.shortDescription;
                // 1) skini tagove
                desc = TAGS.matcher(desc).replaceAll("");
                // 2) decode HTML entiteta
                Spanned sp = Html.fromHtml(desc, Html.FROM_HTML_MODE_LEGACY);
                desc = sp.toString();
                // 3) skrati
                if (desc.length() > 150) {
                    desc = desc.substring(0, 150).trim() + "...";
                }
                offer.shortDescription = desc;
            }
            out.add(e);
        }
        return out;
    }

    private void updateWindow() {
        if (allEvents.isEmpty()) {
            adapter.setItems(new ArrayList<>());
            return;
        }
        int total = allEvents.size();
        int show = Math.min(3, total);

        List<Event> window = new ArrayList<>(show);
        for (int i = 0; i < show; i++) {
            int index = (currentIndex + i) % total;
            window.add(allEvents.get(index));
        }
        adapter.setItems(window);
    }

    /** Pomoćna: vrati sliku iz photo.file ili null. */
    public static String pickImageSource(Photo photo) {
        if (photo == null || photo.file == null) return null;
        String f = photo.file.trim();
        return f.isEmpty() ? null : f;
    }

    /** Pomoćna: dekodira data:image/...;base64, */
    public static Bitmap decodeBase64Image(String dataUrl) {
        try {
            String base64 = dataUrl;
            int comma = dataUrl.indexOf(',');
            if (dataUrl.startsWith("data:") && comma > 0) {
                base64 = dataUrl.substring(comma + 1);
            }
            byte[] bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            return null;
        }
    }
}
