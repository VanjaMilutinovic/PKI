package com.example.pki.ui.fragments;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.pki.R;
import com.example.pki.model.EventOffer;
import com.example.pki.model.Photo;
import com.example.pki.model.User;
import com.example.pki.network.ApiClient;
import com.example.pki.service.BackendService;
import com.example.pki.sessions.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventOfferFragment extends Fragment {

    private BackendService backend;
    private SessionManager sessions;
    private User logged;

    private TextInputLayout tilName, tilShort, tilDesc, tilPrice;
    private TextInputEditText etName, etShort, etDesc, etPrice;
    private ImageView imgPreview;
    private MaterialButton btnPickImage, btnSubmit;
    private MaterialTextView tvError;
    private CircularProgressIndicator progress;

    private String selectedBase64DataUrl; // "data:image/...;base64,XXXX"
    private Photo uploadedPhoto;          // rezultat /photos

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onImagePicked);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_event_offer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        backend  = ApiClient.getService();
        sessions = SessionManager.getInstance(requireContext());
        logged   = sessions.getUser(); // i dalje dostupno ako zatreba, ali ne blokira prikaz

        tilName  = v.findViewById(R.id.tilName);
        tilShort = v.findViewById(R.id.tilShort);
        tilDesc  = v.findViewById(R.id.tilDesc);
        tilPrice = v.findViewById(R.id.tilPrice);

        etName   = v.findViewById(R.id.etName);
        etShort  = v.findViewById(R.id.etShort);
        etDesc   = v.findViewById(R.id.etDesc);
        etPrice  = v.findViewById(R.id.etPrice);

        imgPreview   = v.findViewById(R.id.imgPreview);
        btnPickImage = v.findViewById(R.id.btnPickImage);
        btnSubmit    = v.findViewById(R.id.btnSubmit);
        tvError      = v.findViewById(R.id.tvErrorMsg);
        progress     = v.findViewById(R.id.createOfferProgress);

        btnPickImage.setOnClickListener(v1 -> pickImageLauncher.launch("image/*"));
        btnSubmit.setOnClickListener(v12 -> onSubmit());
    }

    private void setEnabled(boolean enabled) {
        etName.setEnabled(enabled);
        etShort.setEnabled(enabled);
        etDesc.setEnabled(enabled);
        etPrice.setEnabled(enabled);
        btnPickImage.setEnabled(enabled);
        btnSubmit.setEnabled(enabled);
    }

    private void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        setEnabled(!show);
        tvError.setVisibility(View.GONE);
    }

    private void onImagePicked(Uri uri) {
        if (uri == null) return;
        try {
            selectedBase64DataUrl = encodeUriToDataUrl(uri);
            Glide.with(this).load(uri).centerCrop().into(imgPreview);
        } catch (Exception e) {
            Toast.makeText(requireContext(), R.string.photo_pick_error, Toast.LENGTH_SHORT).show();
        }
    }

    private String encodeUriToDataUrl(Uri uri) throws Exception {
        ContentResolver cr = requireContext().getContentResolver();
        String mime = cr.getType(uri);
        if (mime == null) mime = "image/*";
        try (InputStream in = cr.openInputStream(uri);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            if (in == null) throw new IllegalStateException("InputStream null");
            byte[] buf = new byte[8192];
            int r;
            while ((r = in.read(buf)) != -1) bos.write(buf, 0, r);
            String b64 = Base64.encodeToString(bos.toByteArray(), Base64.NO_WRAP);
            return "data:" + mime + ";base64," + b64;
        }
    }

    private void onSubmit() {
        // Validacija
        String name  = txt(etName);
        String sdesc = txt(etShort);
        String desc  = txt(etDesc);
        String prc   = txt(etPrice);

        boolean ok = true;
        if (TextUtils.isEmpty(name))  { tilName.setError(getString(R.string.error_required)); ok = false; } else tilName.setError(null);
        if (TextUtils.isEmpty(sdesc)) { tilShort.setError(getString(R.string.error_required)); ok = false; } else tilShort.setError(null);

        double price = 0.0;
        try {
            price = Double.parseDouble(prc);
            if (price < 0) { tilPrice.setError(getString(R.string.error_price_non_negative)); ok = false; }
            else tilPrice.setError(null);
        } catch (Exception e) {
            tilPrice.setError(getString(R.string.error_price_non_negative));
            ok = false;
        }
        if (!ok) return;

        // final kopije za callback
        final String fName  = name;
        final String fSdesc = sdesc;
        final String fDesc  = desc;
        final double fPrice = price;

        showLoading(true);

        if (!TextUtils.isEmpty(selectedBase64DataUrl)) {
            Photo p = new Photo();
            p.file = selectedBase64DataUrl; // direktan pristup polju
            backend.addPhoto(p).enqueue(new Callback<Photo>() {
                @Override public void onResponse(@NonNull Call<Photo> call, @NonNull Response<Photo> resp) {
                    if (resp.isSuccessful() && resp.body() != null) {
                        uploadedPhoto = resp.body();
                        createOfferOnBackend(fName, fSdesc, fDesc, fPrice, uploadedPhoto);
                    } else {
                        showLoading(false);
                        Toast.makeText(requireContext(), R.string.photo_upload_failed, Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(@NonNull Call<Photo> call, @NonNull Throwable t) {
                    showLoading(false);
                    Toast.makeText(requireContext(), R.string.network_error_generic, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // bez slike
            createOfferOnBackend(fName, fSdesc, fDesc, fPrice, null);
        }
    }

    private void createOfferOnBackend(String name, String sdesc, String desc, double price, @Nullable Photo photo) {
        EventOffer eo = new EventOffer();
        eo.name = name;
        eo.shortDescription = sdesc;
        eo.description = desc;
        eo.price = price;
        eo.photoId = photo;

        backend.addEventOffer(eo).enqueue(new Callback<Void>() {
            @Override public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), R.string.new_offer_success, Toast.LENGTH_SHORT).show();
                    // Povratak na listu ponuda
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment_container, new EventOffersFragment())
                            .commit();
                } else {
                    tvError.setText(R.string.new_offer_failed);
                    tvError.setVisibility(View.VISIBLE);
                }
            }
            @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                showLoading(false);
                tvError.setText(R.string.network_error_generic);
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    private static String txt(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
