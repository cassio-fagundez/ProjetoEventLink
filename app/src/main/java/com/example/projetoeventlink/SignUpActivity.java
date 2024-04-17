package com.example.projetoeventlink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {

    private EditText etFirstName, etSecondName, etFirstLastName, etSecondLastName, etEmail, etPassword, etDateOfBirth;
    private TextView tvPasswordMistake;
    private RadioButton rbtnSelected;
    private RadioGroup rgGender;
    private Button btnRegister;
    private static final String TAG = "SignUp Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Inicialización de EditText
        etFirstName = findViewById(R.id.etFirstName);
        etSecondName = findViewById(R.id.etSecondName);
        etFirstLastName = findViewById(R.id.etFirstLastName);
        etSecondLastName = findViewById(R.id.etSecondLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etDateOfBirth.setOnClickListener(v -> showDatePickerDialog());

        // Inicialización de RadioGroup y TextView
        tvPasswordMistake = findViewById(R.id.tvPasswordMistake);
        rgGender = findViewById(R.id.rgGender);
        rgGender.clearCheck();



        // Inicialización del botón Register
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Verificar si algún campo está vacío
                if (TextUtils.isEmpty(etFirstName.getText())
                        || TextUtils.isEmpty(etFirstLastName.getText())
                        || TextUtils.isEmpty(etEmail.getText())
                        || TextUtils.isEmpty(etPassword.getText())
                        || TextUtils.isEmpty(etDateOfBirth.getText())
                        || rgGender.getCheckedRadioButtonId() == -1) {
                    // Mostrar Toast indicando que se deben completar todos los campos
                    Toast.makeText(SignUpActivity.this, getString(R.string.toast_completeall), Toast.LENGTH_SHORT).show();
                    return; // Salir del método para evitar procesamiento adicional
                }

                int selectedGenderId = rgGender.getCheckedRadioButtonId();

                // Inicialización de RadioButton con el valor seleccionado arriba.
                rbtnSelected = findViewById(selectedGenderId);

                // Obtener demás datos.
                String Names = etFirstName.getText().toString().replaceAll("\\s", "") + " " +
                        etSecondName.getText().toString().replaceAll("\\s", "");
                String LastNames = etFirstLastName.getText().toString().trim() + " " +
                        etSecondLastName.getText().toString().trim();

                String Email = etEmail.getText().toString().replaceAll("\\s", "");
                String Password = etPassword.getText().toString();
                String BirthDate = etDateOfBirth.getText().toString();

                char Gender = ' ';
                if (selectedGenderId == R.id.rbtnGenderM) {
                    Gender = 'M';
                } else if (selectedGenderId == R.id.rbtnGenderF) {
                    Gender = 'F';
                } else if (selectedGenderId == R.id.rbtnGenderO) {
                    Gender = 'O';
                }

                // Llama el método de registro.
                RegisterUser(Names, LastNames, Email, Password, Gender, BirthDate);
                
            }
        });



    }

    // Registro del usuario usando los datos proporcionados.
    private void RegisterUser(String names, String lastNames, String email, String password, char gender, String birthDate) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this, getString(R.string.message_register_success), Toast.LENGTH_LONG).show();
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            // Enviar email de verificacion.
                            firebaseUser.sendEmailVerification();

                            // Abrir perfil del usuario luego del registro.
                            Intent intent = new Intent(SignUpActivity.this, WelcomeActivity.class);

                            //Esto cierra por completo las anteriores activity al momento de abrir la nueva, haciendo imposible regresar al registro una vez que lo haya completado.
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish(); // Aquí se cierra la Activity de registro.

                        } else {
                            tvPasswordMistake.setVisibility(View.VISIBLE);
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e){
                                etPassword.setError(getString(R.string.error_weakpassword));
                                tvPasswordMistake.setText(R.string.error_weakpassword);
                                etPassword.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                etEmail.setError(getString(R.string.error_invalid_email));
                                etEmail.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e){
                                etEmail.setError(getString(R.string.error_used_email));
                                etEmail.requestFocus();
                            } catch (Exception e){
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

    }



    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDateOfBirth.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }
}