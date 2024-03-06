package com.example.projetoeventlink;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        Button btnSpanish = findViewById(R.id.btnSpanish);
        Button btnPortuguese = findViewById(R.id.btnPortuguese);
        Button btnEnglish = findViewById(R.id.btnEnglish);

        btnSpanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("es");
                openNextActivity();
            }
        });

        btnPortuguese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("pt");
                openNextActivity();
            }
        });

        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("en");
                openNextActivity();
            }
        });
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("LANGUAGE", languageCode);
        editor.apply();
    }

    private void openNextActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish();
    }
}
