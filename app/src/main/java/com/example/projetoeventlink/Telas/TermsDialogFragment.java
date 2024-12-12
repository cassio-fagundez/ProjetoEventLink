package com.example.projetoeventlink.Telas;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.projetoeventlink.R;

public class TermsDialogFragment extends DialogFragment {

    private static final String ARG_IS_PRIVACY_POLICY = "is_privacy_policy";


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Obtener el tamaño de la pantalla
            int width = ViewGroup.LayoutParams.MATCH_PARENT; // Ancho máximo
            int height = ViewGroup.LayoutParams.WRAP_CONTENT; // Alto dinámico

            // Aplicar el tamaño y los márgenes
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = width; // Configurar el ancho
            params.height = height; // Configurar el alto
            getDialog().getWindow().setAttributes(params);

            // Establecer márgenes con un contenedor FrameLayout
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE)); // Fondo transparente
            View decorView = getDialog().getWindow().getDecorView();
            decorView.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10)); // Márgenes en dp
        }
    }

    // Convertir dp a px
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }


    // Este método crea el fragmento con el argumento que indica si se debe mostrar la política de privacidad
    public static TermsDialogFragment newInstance(boolean isPrivacyPolicy) {
        TermsDialogFragment fragment = new TermsDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_PRIVACY_POLICY, isPrivacyPolicy);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflamos el layout
        View view = inflater.inflate(R.layout.fragment_terminos, container, false);

        // Obtenemos el argumento
        boolean isPrivacyPolicy = getArguments() != null && getArguments().getBoolean(ARG_IS_PRIVACY_POLICY);

        // Configuramos el título y el texto según el valor del argumento
        TextView tvTitle = view.findViewById(R.id.tvTermoTitle);
        TextView tvContent = view.findViewById(R.id.tvTermoText);
        Button btnClose = view.findViewById(R.id.btnCloseTerms);

        if (isPrivacyPolicy) {
            // Si es la política de privacidad, cambiamos el título y el contenido
            tvTitle.setText(getString(R.string.privacy_policy_title));
            tvContent.setText(getString(R.string.privacy_policy_text)); // Asegúrate de definir este string en strings.xml
        } else {
            // Si son los términos y condiciones, mostramos los términos
            tvTitle.setText(getString(R.string.terms_conditions_title));
            tvContent.setText(getString(R.string.terms_conditions_text)); // Asegúrate de definir este string en strings.xml
        }

        // Configuramos el botón para cerrar el diálogo
        btnClose.setOnClickListener(v -> dismiss());

        return view;
    }
}
