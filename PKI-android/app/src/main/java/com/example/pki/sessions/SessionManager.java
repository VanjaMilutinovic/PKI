package com.example.pki.sessions;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pki.model.User;
import com.google.gson.Gson;

/**
 * Jednostavan SessionManager koji drži ulogovanog korisnika u memoriji i u SharedPreferences
 * kako bi stanje preživelo restart aplikacije.
 */
public final class SessionManager {

    private static final String PREFS = "pki_session_prefs";
    private static final String KEY_USER_JSON = "current_user_json";

    private static SessionManager instance;

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private final MutableLiveData<User> currentUserLive = new MutableLiveData<>(null);

    private SessionManager(Context appContext) {
        prefs = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_USER_JSON, null);
        if (json != null) {
            try {
                User u = gson.fromJson(json, User.class);
                currentUserLive.setValue(u);
            } catch (Exception ignored) {}
        }
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void setUser(@Nullable User user) {
        currentUserLive.setValue(user);
        if (user == null) {
            prefs.edit().remove(KEY_USER_JSON).apply();
        } else {
            prefs.edit().putString(KEY_USER_JSON, gson.toJson(user)).apply();
        }
    }

    @Nullable
    public User getUser() {
        return currentUserLive.getValue();
    }

    public boolean isLoggedIn() {
        return getUser() != null;
    }

    public LiveData<User> userLiveData() {
        return currentUserLive;
    }

    public void clear() {
        setUser(null);
    }
}
