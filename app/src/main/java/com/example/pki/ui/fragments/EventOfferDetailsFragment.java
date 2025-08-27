package com.example.pki.ui.fragments;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pki.R;
import com.example.pki.adapter.EventCommentsAdapter;
import com.example.pki.model.Event;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventOfferDetailsFragment extends Fragment {

    private static final String ARG_OFFER_ID = "offer_id";

    public static EventOfferDetailsFragment newInstance(int offerId) {
        Bundle b = new Bundle();
        b.putInt(ARG_OFFER_ID, offerId);
        EventOfferDetailsFragment f = new EventOfferDetailsFragment();
        f.setArguments(b);
        return f;
    }

    private BackendService backend;
    private SessionManager sessions;

    private int offerId;

    private ImageView img;
    private TextView tvTitle, tvShort, tvLong, tvPrice, tvError, tvCommentsHeader, tvRateHeader;
    private RecyclerView rvComments;
    private EventCommentsAdapter commentsAdapter;
    private CircularProgressIndicator progress;

    private LinearLayout rateContainer;

    private TextInputLayout tilDate, tilPeople;
    private TextInputEditText etDate, etPeople;
    private MaterialButton btnAddToCart;

    private EventOffer eventOffer;
    private User logged;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_offer_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        backend  = ApiClient.getService();
        sessions = SessionManager.getInstance(requireContext());

        offerId = getArguments() != null ? getArguments().getInt(ARG_OFFER_ID, 0) : 0;
        if (offerId <= 0) {
            Toast.makeText(requireContext(), R.string.invalid_offer_id, Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        img = v.findViewById(R.id.imgOffer);
        tvTitle = v.findViewById(R.id.tvOfferTitle);
        tvShort = v.findViewById(R.id.tvShortDesc);
        tvLong  = v.findViewById(R.id.tvLongDesc);
        tvPrice = v.findViewById(R.id.tvPrice);
        tvError = v.findViewById(R.id.tvErrorMsg);
        tvCommentsHeader = v.findViewById(R.id.tvCommentsHeader);
        tvRateHeader = v.findViewById(R.id.tvRateHeader);
        rvComments = v.findViewById(R.id.rvComments);
        progress    = v.findViewById(R.id.offerProgress);
        rateContainer = v.findViewById(R.id.rateContainer);

        tilDate = v.findViewById(R.id.tilDate);
        tilPeople = v.findViewById(R.id.tilPeople);
        etDate = v.findViewById(R.id.etDate);
        etPeople = v.findViewById(R.id.etPeople);
        btnAddToCart = v.findViewById(R.id.btnAddToCart);

        // Recycler za komentare
        commentsAdapter = new EventCommentsAdapter();
        rvComments.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvComments.setNestedScrollingEnabled(false);
        rvComments.setAdapter(commentsAdapter);

        // Tekst može da bude duži
        tvLong.setMovementMethod(new ScrollingMovementMethod());

        logged = sessions.getUser();

        // Date picker
        etDate.setOnClickListener(v1 -> showDatePicker());
        etDate.setOnFocusChangeListener((vv, hasFocus) -> { if (hasFocus) showDatePicker(); });

        btnAddToCart.setOnClickListener(v12 -> addToCart());

        loadOffer();
    }

    private void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        tvError.setVisibility(View.GONE);
    }

    private void loadOffer() {
        showLoading(true);
        backend.getOffer(offerId).enqueue(new Callback<EventOffer>() {
            @Override
            public void onResponse(@NonNull Call<EventOffer> call, @NonNull Response<EventOffer> response) {
                showLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    tvError.setText(R.string.offer_load_error);
                    tvError.setVisibility(View.VISIBLE);
                    return;
                }
                eventOffer = response.body();
                bindOffer(eventOffer);
            }

            @Override
            public void onFailure(@NonNull Call<EventOffer> call, @NonNull Throwable t) {
                showLoading(false);
                tvError.setText(R.string.network_error_generic);
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void bindOffer(EventOffer o) {
        // Naslov
        tvTitle.setText(nz(o.name));

        // Slika
        String src = pickImageSource(o.photoId);
        if (TextUtils.isEmpty(src)) {
            img.setVisibility(View.GONE);
        } else {
            img.setVisibility(View.VISIBLE);
            if (src.startsWith("data:")) {
                Bitmap bmp = decodeBase64Image(src);
                if (bmp != null) img.setImageBitmap(bmp);
                else img.setVisibility(View.GONE);
            } else {
                Glide.with(this).load(src).centerCrop().into(img);
            }
        }

        // Opisi + cena
        tvShort.setText(nz(o.shortDescription));
        if (!TextUtils.isEmpty(o.description)) {
            tvLong.setText(o.description);
            tvLong.setVisibility(View.VISIBLE);
        } else {
            tvLong.setVisibility(View.GONE);
        }

        // o.price je obavezan — direktno formatiranje
        tvPrice.setText(getString(R.string.price_eur_format, o.price));

        // Komentari (samo događaji sa unetim komentarom; možeš vratiti i uslov za status=2)
        List<Event> comments = new ArrayList<>();
        if (o.eventList != null) {
            for (Event e : o.eventList) {
                if (e.comment != null && !e.comment.trim().isEmpty()) {
                    comments.add(e);
                }
            }
        }
        tvCommentsHeader.setVisibility(comments.isEmpty() ? View.GONE : View.VISIBLE);
        commentsAdapter.setItems(comments);

        // Događaji za ocenjivanje za ulogovanog korisnika
        populateRateList(o);
    }

    private void populateRateList(EventOffer o) {
        rateContainer.removeAllViews();
        if (logged == null || logged.userId == 0 || o.eventList == null || o.eventList.isEmpty()) {
            tvRateHeader.setVisibility(View.GONE);
            return;
        }

        List<Event> toRate = new ArrayList<>();
        for (Event e : o.eventList) {
            boolean finished = (e.eventStatusId != null && e.eventStatusId.eventStatusId == 2);
            boolean mine     = (e.customerId != null && e.customerId.userId == logged.userId);
            boolean notRated = (e.grade == null);
            if (finished && mine && notRated) toRate.add(e);
        }

        if (toRate.isEmpty()) {
            tvRateHeader.setVisibility(View.GONE);
            return;
        }
        tvRateHeader.setVisibility(View.VISIBLE);

        LayoutInflater inf = LayoutInflater.from(requireContext());
        for (Event e : toRate) {
            View item = inf.inflate(R.layout.item_event_to_rate, rateContainer, false);

            TextView tvInfo = item.findViewById(R.id.tvRateInfo);
            RatingBar ratingBar = item.findViewById(R.id.ratingBar);
            TextInputEditText etComment = item.findViewById(R.id.etRateComment);
            MaterialButton btnSubmit = item.findViewById(R.id.btnSubmitRating);

            String title = (e.eventOfferId != null) ? nz(e.eventOfferId.name) : "";
            String dateStr = formatDateToDMY(e.date);
            // numberOfPeople može biti Integer wrapper → izbegni auto-unbox NPE
            int people = zi(e.numberOfPeople);

            String info = title + " • " + getString(R.string.guests_date_format, people, dateStr);
            tvInfo.setText(info);

            btnSubmit.setOnClickListener(v -> {
                int grade = Math.round(ratingBar.getRating());
                String comment = etComment.getText() != null ? etComment.getText().toString().trim() : "";
                submitComment(e.eventId, grade, comment);
            });

            rateContainer.addView(item);
        }
    }

    private static String formatDateToDMY(java.util.Date date) {
        if (date == null) return "";
        java.text.SimpleDateFormat out = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        return out.format(date);
    }


    private void submitComment(Integer eventId, int grade, String comment) {
        if (eventId == null || eventId <= 0) return;
        if (grade < 1 || grade > 5) {
            Toast.makeText(requireContext(), R.string.rate_validation, Toast.LENGTH_SHORT).show();
            return;
        }
        backend.addComment(eventId, comment, grade).enqueue(new Callback<Void>() {
            @Override public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), R.string.comment_added, Toast.LENGTH_SHORT).show();
                    loadOffer();
                } else {
                    Toast.makeText(requireContext(), R.string.comment_failed, Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), R.string.network_error_generic, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToCart() {
        if (logged == null || logged.userId == 0 || eventOffer == null) {
            Toast.makeText(requireContext(), R.string.login_required, Toast.LENGTH_SHORT).show();
            return;
        }
        String dateIso = etDate.getText() != null ? etDate.getText().toString().trim() : "";
        String pplStr  = etPeople.getText() != null ? etPeople.getText().toString().trim() : "";

        if (TextUtils.isEmpty(dateIso)) {
            tilDate.setError(getString(R.string.error_required));
            return;
        } else tilDate.setError(null);

        if (TextUtils.isEmpty(pplStr)) {
            tilPeople.setError(getString(R.string.error_required));
            return;
        } else tilPeople.setError(null);

        int people = 0;
        try { people = Integer.parseInt(pplStr); } catch (Exception ignored) {}
        if (people <= 0) {
            tilPeople.setError(getString(R.string.error_positive_number));
            return;
        }

        backend.addToCart(logged.userId, eventOffer.eventOfferId, people, dateIso)
                .enqueue(new Callback<Void>() {
                    @Override public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), R.string.added_to_cart, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), R.string.add_to_cart_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(requireContext(), R.string.network_error_generic, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dlg = new DatePickerDialog(requireContext(),
                (DatePicker view, int y, int m, int d) -> {
                    String iso = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d);
                    etDate.setText(iso);
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dlg.show();
    }

    // Helpers
    private static String nz(String s) { return s == null ? "" : s; }
    private static int zi(Integer v)   { return v == null ? 0  : v; }

    public static String pickImageSource(Photo photo) {
        if (photo == null || photo.file == null) return null;
        String f = photo.file.trim();
        return f.isEmpty() ? null : f;
    }

    public static Bitmap decodeBase64Image(String dataUrl) {
        try {
            String base64 = dataUrl;
            int comma = dataUrl.indexOf(',');
            if (dataUrl.startsWith("data:") && comma > 0) base64 = dataUrl.substring(comma + 1);
            byte[] bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            return null;
        }
    }
}
