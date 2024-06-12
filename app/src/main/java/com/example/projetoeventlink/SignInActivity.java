package com.example.projetoeventlink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword;
    private FirebaseAuth authProfile;

    private static final String TAG = "SignInActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        authProfile = FirebaseAuth.getInstance();

        // Login del usuario
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = etEmail.getText().toString();
                String textPassword = etPassword.getText().toString();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(SignInActivity.this, getString(R.string.toast_writeemail), Toast.LENGTH_SHORT).show();
                    etEmail.setError(getString(R.string.email_required));
                    etEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(SignInActivity.this, getString(R.string.toast_writepassword), Toast.LENGTH_SHORT).show();
                    etPassword.setError(getString(R.string.password_required));
                    etPassword.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(SignInActivity.this, getString(R.string.toast_rewriteemail), Toast.LENGTH_SHORT).show();
                    etEmail.setError(getString(R.string.valid_email_required));
                    etEmail.requestFocus();
                } else {
                    LoginUser(textEmail, textPassword);
                }
            }
        });


    }

    private void LoginUser(String email, String password) {

        authProfile.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    // Obtiene instancia del usuario actual.
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    // Revisa si el email está confirmado.
                    if (firebaseUser.isEmailVerified()){
                        Toast.makeText(SignInActivity.this, getString(R.string.toast_sucessful_login), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        ShowAlertDialog();
                    }

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        etEmail.setError(getString(R.string.error_login_failed_email));
                        etEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        etPassword.setError(getString(R.string.error_login_failed_password));
                        etPassword.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(SignInActivity.this, getString(R.string.error) + " - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(SignInActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void ShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
        builder.setTitle(getString(R.string.alertb_notverified_email_title));
        builder.setMessage(getString(R.string.alertb_notverified_email));

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Para que no se abra en la aplicación y sí de forma separada.
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override // Revisa si el usuario ya está logueado o no.
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null){

            // Abrir cuenta.
            startActivity(new Intent(SignInActivity.this, WelcomeActivity.class));
            finish();

        }
    }
}