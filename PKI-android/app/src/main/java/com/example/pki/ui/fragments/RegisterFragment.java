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
import com.example.pki.model.UserType;
import com.example.pki.network.ApiClient;
import com.example.pki.service.BackendService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private TextInputLayout tilUsername, tilPassword, tilFirstName, tilLastName, tilPhone, tilAddress;
    private TextInputEditText etUsername, etPassword, etFirstName, etLastName, etPhone, etAddress;
    private MaterialButton btnRegister;
    private TextView tvError, linkLogin;
    private CircularProgressIndicator progress;

    private BackendService backend;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        backend = ApiClient.getService();

        tilUsername = v.findViewById(R.id.tilUsername);
        tilPassword = v.findViewById(R.id.tilPassword);
        tilFirstName = v.findViewById(R.id.tilFirstName);
        tilLastName  = v.findViewById(R.id.tilLastName);
        tilPhone     = v.findViewById(R.id.tilPhone);
        tilAddress   = v.findViewById(R.id.tilAddress);

        etUsername = v.findViewById(R.id.etUsername);
        etPassword = v.findViewById(R.id.etPassword);
        etFirstName = v.findViewById(R.id.etFirstName);
        etLastName  = v.findViewById(R.id.etLastName);
        etPhone     = v.findViewById(R.id.etPhone);
        etAddress   = v.findViewById(R.id.etAddress);

        btnRegister = v.findViewById(R.id.btnRegister);
        tvError = v.findViewById(R.id.tvErrorMessage);
        progress = v.findViewById(R.id.registerProgress);
        linkLogin = v.findViewById(R.id.linkLogin);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateButtonEnabled(); clearErrors(); }
            @Override public void afterTextChanged(Editable s) {}
        };
        etUsername.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);
        etFirstName.addTextChangedListener(watcher);
        etLastName.addTextChangedListener(watcher);
        etPhone.addTextChangedListener(watcher);
        etAddress.addTextChangedListener(watcher);

        etAddress.setOnEditorActionListener((tv, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && btnRegister.isEnabled()) {
                performRegister();
                return true;
            }
            return false;
        });

        btnRegister.setOnClickListener(view -> performRegister());

        linkLogin.setOnClickListener(v1 ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, new LoginFragment())
                        .commit()
        );

        updateButtonEnabled();
    }

    private void setLoading(boolean loading) {
        btnRegister.setEnabled(!loading);
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void clearErrors() {
        tilUsername.setError(null);
        tilPassword.setError(null);
        tilFirstName.setError(null);
        tilLastName.setError(null);
        tilPhone.setError(null);
        tilAddress.setError(null);
        tvError.setVisibility(View.GONE);
    }

    private void updateButtonEnabled() {
        boolean allFilled =
                notEmpty(etUsername) &&
                        notEmpty(etPassword) &&
                        notEmpty(etFirstName) &&
                        notEmpty(etLastName) &&
                        notEmpty(etPhone) &&
                        notEmpty(etAddress);
        btnRegister.setEnabled(allFilled);
    }

    private boolean notEmpty(TextInputEditText et) {
        return et.getText() != null && !et.getText().toString().trim().isEmpty();
    }

    private String val(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void performRegister() {
        clearErrors();

        String username = val(etUsername);
        String password = val(etPassword);
        String first    = val(etFirstName);
        String last     = val(etLastName);
        String phone    = val(etPhone);
        String address  = val(etAddress);

        boolean ok = true;
        if (username.isEmpty()) { tilUsername.setError(getString(R.string.error_required)); ok = false; }
        if (password.isEmpty()) { tilPassword.setError(getString(R.string.error_required)); ok = false; }
        if (first.isEmpty())    { tilFirstName.setError(getString(R.string.error_required)); ok = false; }
        if (last.isEmpty())     { tilLastName.setError(getString(R.string.error_required)); ok = false; }
        if (phone.isEmpty())    { tilPhone.setError(getString(R.string.error_required)); ok = false; }
        if (address.isEmpty())  { tilAddress.setError(getString(R.string.error_required)); ok = false; }
        if (!ok) return;

        setLoading(true);

        // Popuni User objekat – direktan pristup poljima
        UserType customer = new UserType();
        customer.userTypeId = 2; // default: kupac

        User newUser = new User();
        newUser.username  = username;
        newUser.password  = password;
        newUser.firstName = first;
        newUser.lastName  = last;
        newUser.phone     = phone;
        newUser.address   = address;
        newUser.userTypeId = customer;

        backend.register(newUser).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), R.string.register_success, Toast.LENGTH_SHORT).show();

                    // >>> NA USPEH: Prebaci na Login ekran <<<
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment_container, new LoginFragment())
                            .commit();

                } else {
                    // Po želji: granaj po kodu (409 = korisničko ime zauzeto, itd.)
                    tvError.setText(R.string.register_failed);
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
}
