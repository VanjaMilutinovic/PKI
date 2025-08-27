package com.example.pki.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pki.R;
import com.example.pki.model.User;
import com.example.pki.network.ApiClient;
import com.example.pki.service.BackendService;
import com.example.pki.sessions.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends Fragment {

    private TextInputLayout tilUsername, tilOld, tilNew, tilConfirm;
    private TextInputEditText etUsername, etOld, etNew, etConfirm;
    private MaterialButton btnSubmit;
    private TextView tvError, linkLogin, linkRegister;
    private CircularProgressIndicator progress;

    private BackendService backend;
    private SessionManager sessions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        backend  = ApiClient.getService();
        sessions = SessionManager.getInstance(requireContext());

        tilUsername = v.findViewById(R.id.tilUsername);
        tilOld      = v.findViewById(R.id.tilOldPassword);
        tilNew      = v.findViewById(R.id.tilNewPassword);
        tilConfirm  = v.findViewById(R.id.tilConfirmPassword);

        etUsername = v.findViewById(R.id.etUsername);
        etOld      = v.findViewById(R.id.etOldPassword);
        etNew      = v.findViewById(R.id.etNewPassword);
        etConfirm  = v.findViewById(R.id.etConfirmPassword);

        btnSubmit = v.findViewById(R.id.btnChangePassword);
        tvError   = v.findViewById(R.id.tvErrorMessage);
        progress  = v.findViewById(R.id.changeProgress);

        linkLogin    = v.findViewById(R.id.linkLogin);
        linkRegister = v.findViewById(R.id.linkRegister);

        // Prefill username ako je ulogovan
        User u = sessions.getUser();
        if (u != null && u.username != null) {
            etUsername.setText(u.username);
        }

        // Footer linkovi samo ako NEMA ulogovanog korisnika
        boolean showFooter = (u == null || u.userId == 0);
        linkLogin.setVisibility(showFooter ? View.VISIBLE : View.GONE);
        linkRegister.setVisibility(showFooter ? View.VISIBLE : View.GONE);

        linkLogin.setOnClickListener(v1 ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, new LoginFragment())
                        .commit()
        );
        linkRegister.setOnClickListener(v12 ->
                Toast.makeText(requireContext(), "Otvaranje registracije…", Toast.LENGTH_SHORT).show()
        );

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateButtonEnabled(); clearErrors(); }
            @Override public void afterTextChanged(Editable s) {}
        };
        etUsername.addTextChangedListener(watcher);
        etOld.addTextChangedListener(watcher);
        etNew.addTextChangedListener(watcher);
        etConfirm.addTextChangedListener(watcher);

        etConfirm.setOnEditorActionListener((tv, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && btnSubmit.isEnabled()) {
                performChange();
                return true;
            }
            return false;
        });

        btnSubmit.setOnClickListener(view -> performChange());

        updateButtonEnabled();
    }

    private void setLoading(boolean loading) {
        btnSubmit.setEnabled(!loading);
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void clearErrors() {
        tilUsername.setError(null);
        tilOld.setError(null);
        tilNew.setError(null);
        tilConfirm.setError(null);
        tvError.setVisibility(View.GONE);
    }

    private void updateButtonEnabled() {
        String u = val(etUsername);
        String o = val(etOld);
        String n = val(etNew);
        String c = val(etConfirm);
        btnSubmit.setEnabled(!u.isEmpty() && !o.isEmpty() && !n.isEmpty() && !c.isEmpty());
    }

    private String val(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void performChange() {
        clearErrors();

        String username = val(etUsername);
        String oldPass  = val(etOld);
        String newPass  = val(etNew);
        String confPass = val(etConfirm);

        boolean ok = true;
        if (username.isEmpty()) { tilUsername.setError(getString(R.string.error_required)); ok = false; }
        if (oldPass.isEmpty())  { tilOld.setError(getString(R.string.error_required)); ok = false; }
        if (newPass.isEmpty())  { tilNew.setError(getString(R.string.error_required)); ok = false; }
        if (confPass.isEmpty()) { tilConfirm.setError(getString(R.string.error_required)); ok = false; }

        if (!newPass.equals(confPass)) {
            tilConfirm.setError(getString(R.string.passwords_mismatch));
            ok = false;
        }
        if (newPass.equals(oldPass)) {
            tilNew.setError(getString(R.string.password_same_as_old));
            ok = false;
        }
        if (!ok) return;

        setLoading(true);

        // 1) Proveri staru lozinku: login(username, oldPass)
        backend.login(username, oldPass).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    setLoading(false);
                    tvError.setText(R.string.login_error);
                    tvError.setVisibility(View.VISIBLE);
                    return;
                }

                User logged = resp.body();
                int userId = logged.userId; // direktan pristup polju

                // 2) Pošalji novu lozinku
                backend.changePassword(userId, newPass).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        setLoading(false);
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), R.string.password_changed_success, Toast.LENGTH_SHORT).show();
                            // opciono: vrati korisnika na login ekran
                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.main_fragment_container, new LoginFragment())
                                    .commit();
                        } else {
                            tvError.setText(R.string.password_change_failed);
                            tvError.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        setLoading(false);
                        tvError.setText(getString(R.string.network_error_generic));
                        tvError.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                setLoading(false);
                tvError.setText(getString(R.string.network_error_generic));
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }
}
