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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin;
    private TextView tvError, linkRegister, linkChangePassword;
    private CircularProgressIndicator progress;

    private BackendService backendService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        backendService = ApiClient.getService();
        sessionManager = SessionManager.getInstance(requireContext());

        tilUsername = v.findViewById(R.id.tilUsername);
        tilPassword = v.findViewById(R.id.tilPassword);
        etUsername  = v.findViewById(R.id.etUsername);
        etPassword  = v.findViewById(R.id.etPassword);
        btnLogin    = v.findViewById(R.id.btnLogin);
        tvError     = v.findViewById(R.id.tvErrorMessage);
        linkRegister = v.findViewById(R.id.linkRegister);
        linkChangePassword = v.findViewById(R.id.linkChangePassword);
        progress    = v.findViewById(R.id.loginProgress);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateButtonEnabled(); clearErrors(); }
            @Override public void afterTextChanged(Editable s) {}
        };
        etUsername.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);

        etPassword.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && btnLogin.isEnabled()) {
                performLogin();
                return true;
            }
            return false;
        });

        btnLogin.setOnClickListener(view -> performLogin());

        linkRegister.setOnClickListener(v1 ->
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_fragment_container, new RegisterFragment())
                                .commit()
        );
        linkChangePassword.setOnClickListener(v12 ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, new ChangePasswordFragment())
                        .commit()
        );

        updateButtonEnabled();
    }

    private void updateButtonEnabled() {
        String u = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String p = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        btnLogin.setEnabled(!u.isEmpty() && !p.isEmpty());
    }

    private void clearErrors() {
        tilUsername.setError(null);
        tilPassword.setError(null);
        tvError.setVisibility(View.GONE);
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void performLogin() {
        clearErrors();

        String u = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String p = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        boolean ok = true;
        if (u.isEmpty()) { tilUsername.setError(getString(R.string.error_required)); ok = false; }
        if (p.isEmpty()) { tilPassword.setError(getString(R.string.error_required)); ok = false; }
        if (!ok) return;

        setLoading(true);

        // Retrofit poziv: @POST("users/login") sa @Query param.
        backendService.login(u, p).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    User logged = response.body();

                    // Globalno zapamti korisnika
                    sessionManager.setUser(logged);

                    Toast.makeText(requireContext(), R.string.login_success, Toast.LENGTH_SHORT).show();

                    // vrati se na About/početni ekran ili zatvori login
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment_container, new AboutUsFragment())
                            .commit();
                } else {
                    tvError.setText(R.string.login_error);
                    tvError.setVisibility(View.VISIBLE);
                }
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
