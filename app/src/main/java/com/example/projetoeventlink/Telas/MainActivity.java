package com.example.projetoeventlink.Telas;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.projetoeventlink.R;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Verificar si ya se ha configurado un idioma previamente
        String savedLanguage = getSavedLanguage();
        if (savedLanguage == null) {
            // Configurar el idioma por defecto
            startActivity(new Intent(MainActivity.this, SettingsLanguageActivity.class));
            finish();
        } else {
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            finish();
        }

    }

    private String getSavedLanguage() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("LANGUAGE", null);
    }

    private void setDefaultLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("LANGUAGE", languageCode);
        editor.apply();
    }

}
