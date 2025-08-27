package com.example.pki.ui.fragments;

import android.os.Bundle;
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
import com.example.pki.model.Event;
import com.example.pki.model.User;
import com.example.pki.network.ApiClient;
import com.example.pki.service.BackendService;
import com.example.pki.sessions.SessionManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import pki.adapter.SchedulingAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SchedulingFragment extends Fragment implements SchedulingAdapter.OnSchedulingAction {

    private MaterialTextView tvHeader, tvError, tvEmpty;
    private RecyclerView recycler;
    private CircularProgressIndicator progress;

    private SchedulingAdapter adapter;

    private BackendService backend;
    private SessionManager sessions;
    private User logged;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scheduling, container, false);
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
        recycler = v.findViewById(R.id.schedulingRecycler);
        progress = v.findViewById(R.id.schedulingProgress);

        adapter = new SchedulingAdapter(this);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        // Samo korisnik tipa 1
        if (logged == null || logged.userId == 0 || logged.userTypeId == null || logged.userTypeId.userTypeId != 1) {
            showNotAllowed();
            return;
        }

        loadPending();
    }

    private void showNotAllowed() {
        tvHeader.setVisibility(View.GONE);
        recycler.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        tvError.setText(R.string.scheduling_not_allowed);
        tvError.setVisibility(View.VISIBLE);
    }

    private void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        tvError.setVisibility(View.GONE);
    }

    private void updateEmptyByAdapter() {
        boolean empty = adapter == null || adapter.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void loadPending() {
        showLoading(true);
        backend.getEventsByStatus(4).enqueue(new Callback<List<Event>>() {
            @Override public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> res) {
                showLoading(false);
                if (!res.isSuccessful() || res.body() == null) {
                    tvError.setText(R.string.scheduling_load_error);
                    tvError.setVisibility(View.VISIBLE);
                    adapter.setItems(new ArrayList<>());
                    updateEmptyByAdapter();
                    return;
                }
                adapter.setItems(res.body());
                updateEmptyByAdapter();
            }
            @Override public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                showLoading(false);
                tvError.setText(R.string.network_error_generic);
                tvError.setVisibility(View.VISIBLE);
                adapter.setItems(new ArrayList<>());
                updateEmptyByAdapter();
            }
        });
    }

    @Override
    public void onApprove(Event e, int position) {
        if (e == null || e.eventId == null || e.eventId <= 0) return;
        backend.approveEvent(e.eventId).enqueue(new Callback<Void>() {
            @Override public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    adapter.removeAt(position);   // <-- ukloni iz adaptera (izvor istine)
                    updateEmptyByAdapter();
                } else {
                    Toast.makeText(requireContext(), R.string.scheduling_approve_error, Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), R.string.network_error_generic, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDecline(Event e, int position) {
        if (e == null || e.eventId == null || e.eventId <= 0) return;
        backend.declineEvent(e.eventId).enqueue(new Callback<Void>() {
            @Override public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    adapter.removeAt(position);   // <-- ukloni iz adaptera (izvor istine)
                    updateEmptyByAdapter();
                } else {
                    Toast.makeText(requireContext(), R.string.scheduling_decline_error, Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), R.string.network_error_generic, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
