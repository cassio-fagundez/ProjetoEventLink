package com.example.projetoeventlink.Telas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

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
            setDefaultLanguage("es");
        }

        // Botón Idioma
        Button buttonLanguage = findViewById(R.id.buttonLanguage);
        buttonLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí inicia la actividad de Idioma
                startActivity(new Intent(MainActivity.this, LanguageActivity.class));
            }
        });


        // Botón Bienvenida
        Button buttonWelcome = findViewById(R.id.buttonWelcome);
        buttonWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí inicia la actividad de Bienvenida
                startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            }
        });

        // Botón Login
        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí inicia la actividad de Login
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
            }
        });

        // Botón Register
        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí inicia la actividad de Register
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });
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
