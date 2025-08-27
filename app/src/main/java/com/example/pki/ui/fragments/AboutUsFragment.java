package com.example.pki.ui.fragments;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pki.R;

public class AboutUsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);

        TextView textView = view.findViewById(R.id.aboutUsText);

        String htmlContent = "<h3>Dobrodošli u \"Trenuci za pamćenje\"</h3>" +
                "<p>Mi smo agencija posvećena organizaciji nezaboravnih događaja. " +
                "Naša misija je da svaki trenutak koji proslavljate sa nama bude poseban i upamćen.</p>" +
                "<h4>Naša vizija</h4>" +
                "<p>Verujemo da svaki događaj zaslužuje pažnju i personalizovan pristup. " +
                "Naš tim stručnjaka radi sa vama kako bismo kreirali događaj iz snova, " +
                "bilo da je u pitanju intimna proslava ili veliki korporativni događaj.</p>" +
                "<h4>Šta nudimo</h4>" +
                "<ul>" +
                "<li>Organizacija rođendana i punoletstava</li>" +
                "<li>Venčanja i godišnjice braka</li>" +
                "<li>Krštenja i porodične proslave</li>" +
                "<li>Korporativni događaji i team building aktivnosti</li>" +
                "<li>Tematske zabave i promotivne kampanje</li>" +
                "</ul>" +
                "<h4>Zašto odabrati nas</h4>" +
                "<ol>" +
                "<li><strong>Iskustvo:</strong> Više od 10 godina iskustva u organizaciji događaja.</li>" +
                "<li><strong>Stručni tim:</strong> Kreativni i posvećeni organizatori koji slušaju vaše želje.</li>" +
                "<li><strong>Personalizacija:</strong> Svaki događaj je prilagođen vašim potrebama.</li>" +
                "<li><strong>Kvalitet:</strong> Saradnja sa proverenim dobavljačima i pružanje vrhunske usluge.</li>" +
                "<li><strong>Podrška:</strong> Konstantna komunikacija i podrška pre, tokom i nakon događaja.</li>" +
                "</ol>" +
                "<h4>Kontakt</h4>" +
                "<p>Agencija \"Trenuci za pamćenje\"<br>" +
                "Adresa: Ulica Primer 12, Beograd<br>" +
                "Telefon: +381 11 123 4567<br>" +
                "Email: kontakt@trenucizapamcenje.rs</p>" +
                "<p>Pridružite nam se i dozvolite da svaki vaš trenutak postane nezaboravan!</p>";

        textView.setText(Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY));
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }
}
