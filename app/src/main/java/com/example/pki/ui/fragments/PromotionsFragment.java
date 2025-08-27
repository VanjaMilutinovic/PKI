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
import com.example.pki.adapter.*;
import com.example.pki.model.Photo;
import com.example.pki.model.Promotion;
import com.example.pki.network.ApiClient;
import com.example.pki.service.BackendService;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PromotionsFragment extends Fragment {

    private static final long ROTATE_MS = 5000L;
    private static final Pattern TAGS = Pattern.compile("<[^>]*>");

    private RecyclerView recycler;
    private TextView tvError;
    private CircularProgressIndicator progress;
    private PromotionsAdapter adapter;

    private BackendService backend;

    private final List<Promotion> allPromotions = new ArrayList<>();
    private int currentIndex = 0;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable rotator = new Runnable() {
        @Override public void run() {
            if (allPromotions.isEmpty()) return;
            currentIndex = (currentIndex + 1) % allPromotions.size();
            updateWindow();
            handler.postDelayed(this, ROTATE_MS);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_promotions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        backend  = ApiClient.getService();

        recycler = v.findViewById(R.id.promotionsRecycler);
        tvError  = v.findViewById(R.id.tvErrorMsg);
        progress = v.findViewById(R.id.promotionsProgress);

        // Grid: 1 kolona na manjim ekranima, 2+ na širim
        int span = getResources().getConfiguration().screenWidthDp >= 600 ? 2 : 1;
        recycler.setLayoutManager(new GridLayoutManager(requireContext(), span));
        adapter = new PromotionsAdapter();
        recycler.setAdapter(adapter);

        loadPromotions();
    }

    @Override public void onResume() {
        super.onResume();
        if (allPromotions.size() > 3) {
            handler.removeCallbacks(rotator);
            handler.postDelayed(rotator, ROTATE_MS);
        }
    }

    @Override public void onPause() {
        super.onPause();
        handler.removeCallbacks(rotator);
    }

    private void loadPromotions() {
        showLoading(true);
        backend.getPromotions().enqueue(new Callback<List<Promotion>>() {
            @Override
            public void onResponse(@NonNull Call<List<Promotion>> call, @NonNull Response<List<Promotion>> response) {
                showLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    tvError.setText(R.string.promotions_load_error);
                    tvError.setVisibility(View.VISIBLE);
                    return;
                }
                allPromotions.clear();
                allPromotions.addAll(cleanDescriptions(response.body()));
                currentIndex = 0;
                updateWindow();

                if (allPromotions.size() > 3) {
                    handler.removeCallbacks(rotator);
                    handler.postDelayed(rotator, ROTATE_MS);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Promotion>> call, @NonNull Throwable t) {
                showLoading(false);
                tvError.setText(R.string.network_error_generic);
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        tvError.setVisibility(View.GONE);
        recycler.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    }

    private List<Promotion> cleanDescriptions(List<Promotion> src) {
        List<Promotion> out = new ArrayList<>(src.size());
        for (Promotion p : src) {
            Promotion cp = p; // pretpostavljamo mutable polja (direktan pristup)
            String desc = cp.description != null ? cp.description : "";
            // 1) Skini HTML tagove
            desc = TAGS.matcher(desc).replaceAll("");
            // 2) Dekoduj HTML entitete (&nbsp;, &amp;...)
            Spanned sp = Html.fromHtml(desc, Html.FROM_HTML_MODE_LEGACY);
            desc = sp.toString();
            // 3) Skrati na 150 karaktera
            if (desc.length() > 150) {
                desc = desc.substring(0, 150).trim() + "...";
            }
            cp.description = desc;
            out.add(cp);
        }
        return out;
    }

    private void updateWindow() {
        if (allPromotions.isEmpty()) {
            adapter.setItems(new ArrayList<>());
            return;
        }
        int total = allPromotions.size();
        int show = Math.min(3, total);

        List<Promotion> window = new ArrayList<>(show);
        for (int i = 0; i < show; i++) {
            int index = (currentIndex + i) % total;
            window.add(allPromotions.get(index));
        }
        adapter.setItems(window);
    }

    // Pomoćna za base64 data:image/...;base64,xxx
    static public Bitmap decodeBase64Image(String dataUrl) {
        try {
            String base64 = dataUrl;
            int comma = dataUrl.indexOf(',');
            if (dataUrl.startsWith("data:") && comma > 0) {
                base64 = dataUrl.substring(comma + 1);
            }
            byte[] bytes = Base64.getDecoder().decode(base64);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            return null;
        }
    }

    // Adapter koristi ovu pomoćnu za učitavanje slike iz URL-a ili base64
    public static String pickImageSource(Photo photo) {
        if (photo == null || photo.file == null || photo.file.trim().isEmpty()) return null;
        return photo.file.trim();
    }
}
