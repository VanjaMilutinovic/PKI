package com.example.pki.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.pki.R;
import com.example.pki.model.User;
import com.example.pki.sessions.SessionManager; // <— sessions (ne session)
import com.example.pki.ui.fragments.AboutUsFragment;
import com.example.pki.ui.fragments.CartFragment;
import com.example.pki.ui.fragments.CreateEventOfferFragment;
import com.example.pki.ui.fragments.EventOffersFragment;
import com.example.pki.ui.fragments.EventsFragment;
import com.example.pki.ui.fragments.LoginFragment;
import com.example.pki.ui.fragments.NotificationsFragment;
import com.example.pki.ui.fragments.ProfileFragment;
import com.example.pki.ui.fragments.PromotionsFragment;
import com.example.pki.ui.fragments.SchedulingFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final int USER_TYPE_ORGANIZER = 1; // Zakazivanja & Nova ponuda
    private static final int USER_TYPE_CUSTOMER  = 2; // Korpa & Obaveštenja

    private BottomNavigationView bottomNavigation;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = SessionManager.getInstance(this);

        CoordinatorLayout root = findViewById(R.id.root_container);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        FrameLayout container = findViewById(R.id.main_fragment_container);

        // Zapamti osnovne (XML) paddinge — da se ne akumuliraju
        final int tbL = topAppBar.getPaddingLeft();
        final int tbT = topAppBar.getPaddingTop();
        final int tbR = topAppBar.getPaddingRight();
        final int tbB = topAppBar.getPaddingBottom();

        final int bnL = bottomNavigation.getPaddingLeft();
        final int bnT = bottomNavigation.getPaddingTop();
        final int bnR = bottomNavigation.getPaddingRight();
        final int bnB = bottomNavigation.getPaddingBottom();

        final int cL = container.getPaddingLeft();
        final int cT = container.getPaddingTop();
        final int cR = container.getPaddingRight();
        final int cB = container.getPaddingBottom();

        // Levo/desno bezbedni insets (baza + sistem)
        final int[] safeLeftRight = new int[2]; // [0]=left, [1]=right

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // TOOLBAR: baza + status bar visina (ne akumuliraj)
            topAppBar.setPadding(tbL, tbT + bars.top, tbR, tbB);

            // BOTTOM NAV: baza + sistemski bottom
            bottomNavigation.setPadding(bnL, bnT, bnR, bnB + bars.bottom);

            // Zapamti leve/desne paddinge kontejnera = baza + sistem
            safeLeftRight[0] = cL + bars.left;
            safeLeftRight[1] = cR + bars.right;

            // Donji padding = bazni + visina bottom bara (ne akumuliraj)
            applyContainerPadding(container, safeLeftRight[0], cT, safeLeftRight[1], cB, bottomNavigation.getHeight());
            return insets;
        });

        // Ažuriraj kada se promeni visina bottom bara (rotacija, promene layout-a)
        bottomNavigation.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> {
            int navHeight = Math.max(0, b - t);
            if (navHeight == 0) navHeight = bottomNavigation.getHeight();
            applyContainerPadding(container, safeLeftRight[0], cT, safeLeftRight[1], cB, navHeight);
        });

        // Burger dugme (jedini action desno): popup sa opcijama
        topAppBar.setOnMenuItemClickListener(item -> {
            showBurgerPopup(topAppBar);
            return true;
        });

        // Donja navigacija
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_about) {
                loadFragment(new AboutUsFragment());
                return true;
            } else if (id == R.id.menu_event_offers) {
                loadFragment(new EventOffersFragment());
                return true;
            } else if (id == R.id.menu_promotions) {
                loadFragment(new PromotionsFragment());
                return true;
            } else if (id == R.id.menu_events) {
                loadFragment(new EventsFragment());
                return true;
            }
            return false;
        });

        // Naslov: prikaži username kada je ulogovan (direktan pristup polju)
        sessionManager.userLiveData().observe(this, user -> {
            if (user != null && user.username != null) {
                topAppBar.setTitle(getString(R.string.app_name) + " • " + user.username);
            } else {
                topAppBar.setTitle(getString(R.string.app_name));
            }
        });

        if (savedInstanceState == null) {
            loadFragment(new AboutUsFragment());
        }
    }

    private void showBurgerPopup(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.app_bar_options, popup.getMenu());

        applyVisibilityByUserType(popup.getMenu());

        popup.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.menu_profile) {
                loadFragment(new ProfileFragment());
                return true;
            } else if (id == R.id.menu_notifications) {
                loadFragment(new NotificationsFragment());
                return true;
            } else if (id == R.id.menu_cart) {
                loadFragment(new CartFragment());
                return true;
            } else if (id == R.id.menu_scheduling) {
                loadFragment(new SchedulingFragment());
                return true;
            } else if (id == R.id.menu_new_offer) {
                loadFragment(new CreateEventOfferFragment());
                return true;
            } else if (id == R.id.menu_login) {
                loadFragment(new LoginFragment());
                return true;
            } else if (id == R.id.menu_logout) {
                sessionManager.clear();
                Toast.makeText(this, "Odjavljeni ste.", Toast.LENGTH_SHORT).show();
                loadFragment(new AboutUsFragment());
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void applyVisibilityByUserType(Menu menu) {
        boolean loggedIn = sessionManager.isLoggedIn();
        int userType = getCurrentUserType();

        MenuItem miLogin         = menu.findItem(R.id.menu_login);
        MenuItem miLogout        = menu.findItem(R.id.menu_logout);
        MenuItem miProfile       = menu.findItem(R.id.menu_profile);
        MenuItem miCart          = menu.findItem(R.id.menu_cart);
        MenuItem miNotifications = menu.findItem(R.id.menu_notifications);
        MenuItem miScheduling    = menu.findItem(R.id.menu_scheduling);
        MenuItem miNewOffer      = menu.findItem(R.id.menu_new_offer);

        if (miLogin  != null) miLogin.setVisible(!loggedIn);
        if (miLogout != null) miLogout.setVisible(loggedIn);
        if (miProfile!= null) miProfile.setVisible(loggedIn);

        if (miCart          != null) miCart.setVisible(false);
        if (miNotifications != null) miNotifications.setVisible(false);
        if (miScheduling    != null) miScheduling.setVisible(false);
        if (miNewOffer      != null) miNewOffer.setVisible(false);

        if (!loggedIn) return;

        if (userType == USER_TYPE_CUSTOMER) {
            if (miCart          != null) miCart.setVisible(true);
            if (miNotifications != null) miNotifications.setVisible(true);
        } else if (userType == USER_TYPE_ORGANIZER) {
            if (miScheduling != null) miScheduling.setVisible(true);
            if (miNewOffer   != null) miNewOffer.setVisible(true);
        }
    }

    // Direktan pristup poljima (bez gettera/refleksije)
    private int getCurrentUserType() {
        User u = sessionManager.getUser();
        if (u == null) return -1;
        // prema tvojoj strukturi: user.userTypeId.userTypeId
        return (u.userTypeId != null) ? u.userTypeId.userTypeId : -1;
    }

    private void applyContainerPadding(FrameLayout container,
                                       int safeLeft, int baseTop, int safeRight,
                                       int baseBottom, int bottomNavHeight) {
        container.setPadding(safeLeft, baseTop, safeRight, baseBottom + bottomNavHeight);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .commit();
    }
}
