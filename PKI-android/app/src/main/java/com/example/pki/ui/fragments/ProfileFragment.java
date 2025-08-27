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
import com.example.pki.sessions.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextInputLayout tilUsername, tilFirstName, tilLastName, tilPhone, tilAddress, tilUserType;
    private TextInputEditText etUsername, etFirstName, etLastName, etPhone, etAddress, etUserType;
    private MaterialButton btnSave;
    private TextView tvInfo, tvError, linkChangePassword;
    private CircularProgressIndicator progress;

    private BackendService backend;
    private SessionManager sessions;
    private User current; // ulogovani korisnik (direktni pristup poljima)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        backend  = ApiClient.getService();
        sessions = SessionManager.getInstance(requireContext());

        // ako nije ulogovan -> na Login
        current = sessions.getUser();
        if (current == null) {
            Toast.makeText(requireContext(), R.string.login_required, Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, new LoginFragment())
                    .commit();
            return;
        }

        // init UI
        tilUsername = v.findViewById(R.id.tilUsername);
        tilFirstName = v.findViewById(R.id.tilFirstName);
        tilLastName  = v.findViewById(R.id.tilLastName);
        tilPhone     = v.findViewById(R.id.tilPhone);
        tilAddress   = v.findViewById(R.id.tilAddress);
        tilUserType  = v.findViewById(R.id.tilUserType);

        etUsername = v.findViewById(R.id.etUsername);
        etFirstName = v.findViewById(R.id.etFirstName);
        etLastName  = v.findViewById(R.id.etLastName);
        etPhone     = v.findViewById(R.id.etPhone);
        etAddress   = v.findViewById(R.id.etAddress);
        etUserType  = v.findViewById(R.id.etUserType);

        btnSave = v.findViewById(R.id.btnSaveProfile);
        tvInfo  = v.findViewById(R.id.tvInfoMessage);
        tvError = v.findViewById(R.id.tvErrorMessage);
        progress = v.findViewById(R.id.profileProgress);

        linkChangePassword = v.findViewById(R.id.linkChangePassword);

        // popuni polja iz sesije
        bindUserToFields(current);

        // validacija
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { clearErrors(); updateButtonEnabled(); }
            @Override public void afterTextChanged(Editable s) {}
        };
        etUsername.addTextChangedListener(watcher);
        etFirstName.addTextChangedListener(watcher);
        etLastName.addTextChangedListener(watcher);
        etPhone.addTextChangedListener(watcher);
        etAddress.addTextChangedListener(watcher);

        etAddress.setOnEditorActionListener((tv, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && btnSave.isEnabled()) {
                performSave();
                return true;
            }
            return false;
        });

        btnSave.setOnClickListener(v1 -> performSave());

        linkChangePassword.setOnClickListener(v12 ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, new ChangePasswordFragment())
                        .commit()
        );

        updateButtonEnabled();
    }

    private void bindUserToFields(User u) {
        etUsername.setText(u.username != null ? u.username : "");
        etFirstName.setText(u.firstName != null ? u.firstName : "");
        etLastName.setText(u.lastName != null ? u.lastName : "");
        etPhone.setText(u.phone != null ? u.phone : "");
        etAddress.setText(u.address != null ? u.address : "");
        etUserType.setText(getUserTypeName(u.userTypeId));
    }

    private String getUserTypeName(UserType ut) {
        if (ut == null) return "";
        if (ut.name != null && !ut.name.isEmpty()) return ut.name;
        if (ut.userTypeId == 1) return getString(R.string.user_type_organizer);
        if (ut.userTypeId == 2) return getString(R.string.user_type_customer);
        return getString(R.string.user_type_unknown, ut.userTypeId);
    }

    private void setLoading(boolean loading) {
        btnSave.setEnabled(!loading);
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void clearErrors() {
        tilUsername.setError(null);
        tilFirstName.setError(null);
        tilLastName.setError(null);
        tilPhone.setError(null);
        tilAddress.setError(null);
        tvError.setVisibility(View.GONE);
        tvInfo.setVisibility(View.GONE);
    }

    private boolean notEmpty(TextInputEditText et) {
        return et.getText() != null && !et.getText().toString().trim().isEmpty();
    }

    private void updateButtonEnabled() {
        boolean ok = notEmpty(etUsername) && notEmpty(etFirstName) && notEmpty(etLastName)
                && notEmpty(etPhone) && notEmpty(etAddress);
        btnSave.setEnabled(ok);
    }

    private String val(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void performSave() {
        clearErrors();

        String username = val(etUsername);
        String first    = val(etFirstName);
        String last     = val(etLastName);
        String phone    = val(etPhone);
        String address  = val(etAddress);

        boolean ok = true;
        if (username.isEmpty()) { tilUsername.setError(getString(R.string.error_required)); ok = false; }
        if (first.isEmpty())    { tilFirstName.setError(getString(R.string.error_required)); ok = false; }
        if (last.isEmpty())     { tilLastName.setError(getString(R.string.error_required)); ok = false; }
        if (phone.isEmpty())    { tilPhone.setError(getString(R.string.error_required)); ok = false; }
        if (address.isEmpty())  { tilAddress.setError(getString(R.string.error_required)); ok = false; }
        if (!ok) return;

        // pripremi update objekat (zadržavamo userId i userTypeId)
        User updated = new User();
        updated.userId    = current.userId;
        updated.username  = username;
        // lozinku NE diramo ovde
        updated.password  = current.password;
        updated.firstName = first;
        updated.lastName  = last;
        updated.phone     = phone;
        updated.address   = address;
        updated.userTypeId = current.userTypeId;

        setLoading(true);

        backend.updateUser(current.userId, updated).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    // lokalno ažuriraj sesiju i UI
                    sessions.setUser(updated);
                    current = updated;
                    bindUserToFields(current);
                    tvInfo.setText(R.string.profile_updated);
                    tvInfo.setVisibility(View.VISIBLE);
                } else {
                    tvError.setText(R.string.profile_update_failed);
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
