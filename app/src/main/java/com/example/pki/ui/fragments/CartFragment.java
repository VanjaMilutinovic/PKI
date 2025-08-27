package com.example.pki.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pki.R;
import com.example.pki.adapter.CartAdapter;
import com.example.pki.model.Event;
import com.example.pki.model.User;
import com.example.pki.network.ApiClient;
import com.example.pki.service.BackendService;
import com.example.pki.sessions.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment implements CartAdapter.OnCartAction {

    private RecyclerView recycler;
    private TextView tvHeader, tvError, tvEmpty;
    private MaterialButton btnConfirm;
    private CircularProgressIndicator progress;

    private CartAdapter adapter;
    private final List<Event> cart = new ArrayList<>();

    private BackendService backend;
    private SessionManager sessions;
    private User logged;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        backend  = ApiClient.getService();
        sessions = SessionManager.getInstance(requireContext());
        logged   = sessions.getUser();

        tvHeader  = v.findViewById(R.id.tvHeader);
        tvError   = v.findViewById(R.id.tvErrorMsg);
        tvEmpty   = v.findViewById(R.id.tvEmpty);
        recycler  = v.findViewById(R.id.cartRecycler);
        btnConfirm= v.findViewById(R.id.btnConfirm);
        progress  = v.findViewById(R.id.cartProgress);

        adapter = new CartAdapter(this);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        // Guard: samo za ulogovane korisnike tipa 2
        if (logged == null || logged.userId == 0 || logged.userTypeId == null || logged.userTypeId.userTypeId != 2) {
            showNotAllowed();
            return;
        }

        btnConfirm.setOnClickListener(v1 -> confirmCart());
        loadCart();
    }

    private void showNotAllowed() {
        tvError.setText(R.string.cart_login_required);
        tvError.setVisibility(View.VISIBLE);
        tvHeader.setVisibility(View.GONE);
        recycler.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        btnConfirm.setVisibility(View.GONE);
    }

    private void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        tvError.setVisibility(View.GONE);
        btnConfirm.setEnabled(!show);
    }

    private void loadCart() {
        showLoading(true);
        backend.getCart(logged.userId).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                showLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    tvError.setText(R.string.cart_load_error);
                    tvError.setVisibility(View.VISIBLE);
                    updateEmptyState(new ArrayList<>());
                    return;
                }
                cart.clear();
                cart.addAll(response.body());
                adapter.setItems(cart);
                updateEmptyState(cart);
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                showLoading(false);
                tvError.setText(R.string.network_error_generic);
                tvError.setVisibility(View.VISIBLE);
                updateEmptyState(new ArrayList<>());
            }
        });
    }

    private void updateEmptyState(List<Event> list) {
        boolean empty = list == null || list.isEmpty();
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        btnConfirm.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onRemove(Event e) {
        if (e == null || e.eventId == null || e.eventId <= 0) return;
        backend.removeFromCart(e.eventId).enqueue(new Callback<Void>() {
            @Override public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    cart.remove(e);
                    adapter.setItems(cart);
                    updateEmptyState(cart);
                } else {
                    Toast.makeText(requireContext(), R.string.cart_remove_error, Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), R.string.network_error_generic, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmCart() {
        showLoading(true);
        backend.confirmCart(logged.userId).enqueue(new Callback<Void>() {
            @Override public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    cart.clear();
                    adapter.setItems(cart);
                    updateEmptyState(cart);
                    Toast.makeText(requireContext(), R.string.cart_confirm_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), R.string.cart_confirm_error, Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), R.string.network_error_generic, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Pomoćna za datum u dd.MM.yyyy (ulaz je ISO: yyyy-MM-dd ili yyyy-MM-ddTHH:mm...)
    public static String formatIsoToDMYdots(String iso) {
        if (TextUtils.isEmpty(iso) || iso.length() < 10) return iso == null ? "" : iso;
        try {
            String y = iso.substring(0,4);
            String m = iso.substring(5,7);
            String d = iso.substring(8,10);
            return String.format(Locale.getDefault(), "%s.%s.%s", d, m, y);
        } catch (Exception e) {
            return iso;
        }
    }
}
