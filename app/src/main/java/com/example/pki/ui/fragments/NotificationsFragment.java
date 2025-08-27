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
import com.example.pki.adapter.NotificationsAdapter;
import com.example.pki.model.Notification;
import com.example.pki.model.User;
import com.example.pki.network.ApiClient;
import com.example.pki.service.BackendService;
import com.example.pki.sessions.SessionManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment implements NotificationsAdapter.OnNotificationAction {

    private RecyclerView recycler;
    private CircularProgressIndicator progress;
    private MaterialTextView tvHeader, tvError, tvEmpty;

    private NotificationsAdapter adapter;
    private BackendService backend;
    private SessionManager sessions;
    private User logged;

    private final List<Notification> notifications = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        backend  = ApiClient.getService();
        sessions = SessionManager.getInstance(requireContext());
        logged   = sessions.getUser();

        tvHeader = v.findViewById(R.id.tvHeader);
        tvError  = v.findViewById(R.id.tvErrorMsg);
        tvEmpty  = v.findViewById(R.id.tvEmpty);
        progress = v.findViewById(R.id.notificationsProgress);
        recycler = v.findViewById(R.id.notificationsRecycler);

        adapter = new NotificationsAdapter(this);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        // Samo user type = 2 ima pristup
        if (logged == null || logged.userId == 0 || logged.userTypeId == null || logged.userTypeId.userTypeId != 2) {
            showNotAllowed();
            return;
        }

        loadNotifications();
    }

    private void showNotAllowed() {
        tvHeader.setVisibility(View.GONE);
        recycler.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        tvError.setText(R.string.notifications_login_required);
        tvError.setVisibility(View.VISIBLE);
    }

    private void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        tvError.setVisibility(View.GONE);
    }

    private void loadNotifications() {
        showLoading(true);
        backend.getNotifications(logged.userId).enqueue(new Callback<List<Notification>>() {
            @Override public void onResponse(@NonNull Call<List<Notification>> call, @NonNull Response<List<Notification>> res) {
                showLoading(false);
                if (!res.isSuccessful() || res.body() == null) {
                    tvError.setText(R.string.notifications_load_error);
                    tvError.setVisibility(View.VISIBLE);
                    updateEmpty(new ArrayList<>());
                    return;
                }
                notifications.clear();
                notifications.addAll(res.body());
                adapter.setItems(notifications);
                updateEmpty(notifications);
            }
            @Override public void onFailure(@NonNull Call<List<Notification>> call, @NonNull Throwable t) {
                showLoading(false);
                tvError.setText(R.string.network_error_generic);
                tvError.setVisibility(View.VISIBLE);
                updateEmpty(new ArrayList<>());
            }
        });
    }

    private void updateEmpty(List<Notification> list) {
        boolean empty = list == null || list.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onToggleRead(Notification n, int position) {
        if (n == null || n.notificationId == null) return;
        if (Boolean.TRUE.equals(n.isRead)) return; // već pročitano

        backend.markNotificationAsRead(n.notificationId).enqueue(new Callback<Void>() {
            @Override public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    n.isRead = true;
                    adapter.notifyItemChanged(position);
                } else {
                    Toast.makeText(requireContext(), R.string.notifications_mark_failed, Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), R.string.network_error_generic, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Format pomoć: prihvati bilo kakav string (ISO/Date.toString) i vrati "dd.MM.yyyy HH:mm"
    public static String formatDateTimePretty(String src) {
        if (TextUtils.isEmpty(src)) return "";
        // pokušaj ISO: "yyyy-MM-ddTHH:mm:ss" ili "yyyy-MM-dd HH:mm:ss"
        try {
            String s = src.replace('T', ' ');
            if (s.length() >= 16) {
                String y = s.substring(0, 4);
                String m = s.substring(5, 7);
                String d = s.substring(8, 10);
                String hh = s.substring(11, 13);
                String mm = s.substring(14, 16);
                return String.format(Locale.getDefault(), "%s.%s.%s %s:%s", d, m, y, hh, mm);
            }
        } catch (Exception ignored) {}
        return src; // fallback
    }
}
