package com.example.projetoeventlink.Telas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetoeventlink.Logica.Usuario;
import com.example.projetoeventlink.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class UserRegisterActivity extends AppCompatActivity {

    private EditText etFirstName, etSecondName, etFirstLastName, etSecondLastName, etEmail, etPassword, etDateOfBirth;
    private TextView tvPasswordMistake, tvCBConsentimientoDatos, tvCBTerminosCondiciones;
    private RadioButton rbtnSelected;
    private RadioGroup rgGender;
    private Button btnRegister;
    private CheckBox cbConsentimientoDatos, cbTerminosCondiciones;
    private static final String TAG = "LOGSignUpActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        // Inicialización de EditText
        etFirstName = findViewById(R.id.etFirstName);
        etSecondName = findViewById(R.id.etSecondName);
        etFirstLastName = findViewById(R.id.etFirstLastName);
        etSecondLastName = findViewById(R.id.etSecondLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        cbConsentimientoDatos = findViewById(R.id.cbConsentimientoDatos);
        cbTerminosCondiciones = findViewById(R.id.cbTerminosCondiciones);
        tvCBConsentimientoDatos = findViewById(R.id.tvCBConsentimientoDatos);
        tvCBTerminosCondiciones = findViewById(R.id.tvCBTerminosCondiciones);
        etDateOfBirth.setOnClickListener(v -> showDatePickerDialog());

        // Inicialización de RadioGroup y TextView
        tvPasswordMistake = findViewById(R.id.tvPasswordMistake);
        rgGender = findViewById(R.id.rgGender);
        rgGender.clearCheck();

        // Revisar checkboxes
        cbTerminosCondiciones.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateRegisterButtonState(); // Llamar a la función para actualizar el estado del botón
            }
        });

        cbConsentimientoDatos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateRegisterButtonState(); // Llamar a la función para actualizar el estado del botón
            }
        });

        // Click listener para el texto de los CheckBox
        tvCBTerminosCondiciones.setOnClickListener(v -> {
            // Mostrar el diálogo con 'true' porque es Términos y Condiciones
            showTermsDialog(false);
        });

        tvCBConsentimientoDatos.setOnClickListener(v -> {
            // Mostrar el diálogo con 'false' porque es Política de Privacidad
            showTermsDialog(true);
        });




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
                    Toast.makeText(UserRegisterActivity.this, getString(R.string.toast_completeall), Toast.LENGTH_SHORT).show();
                    return; // Salir del método para evitar procesamiento adicional
                }


                int selectedGenderId = rgGender.getCheckedRadioButtonId();

                // Validar nombres y apellidos
                if (!etFirstName.getText().toString().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) {
                    etFirstName.setError(getString(R.string.error_invalid_name));
                    etFirstName.requestFocus();
                    return;
                }

                if (!TextUtils.isEmpty(etSecondName.getText()) &&
                        !etSecondName.getText().toString().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) {
                    etSecondName.setError(getString(R.string.error_invalid_name));
                    etSecondName.requestFocus();
                    return;
                }

                if (!etFirstLastName.getText().toString().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) {
                    etFirstLastName.setError(getString(R.string.error_invalid_lastname));
                    etFirstLastName.requestFocus();
                    return;
                }

                if (!TextUtils.isEmpty(etSecondLastName.getText()) &&
                        !etSecondLastName.getText().toString().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) {
                    etSecondLastName.setError(getString(R.string.error_invalid_lastname));
                    etSecondLastName.requestFocus();
                    return;
                }

                // Validar edad
                if (!isAgeValid(etDateOfBirth.getText().toString())) {
                    etDateOfBirth.setError(getString(R.string.error_invalid_age));
                    etDateOfBirth.requestFocus();
                    return;
                }



                // Inicialización de RadioButton con el valor seleccionado arriba.
                rbtnSelected = findViewById(selectedGenderId);

                // Obtener demás datos.
                String Name1 = etFirstName.getText().toString().replaceAll("\\s", "");
                String Name2 = etSecondName.getText().toString().replaceAll("\\s", "");
                String LastName1 = etFirstLastName.getText().toString().trim();
                String LastName2 = etSecondLastName.getText().toString().trim();

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
                RegisterUser(Name1, Name2, LastName1, LastName2, Email, Password, Gender, BirthDate);

            }
        });



    }

    // Registro del usuario usando los datos proporcionados.
    private void RegisterUser(String name1, String name2, String lastName1, String lastName2, String email, String password, char gender, String birthDate) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(UserRegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "onComplete Authenticator called");
                        if (task.isSuccessful()){
                            Toast.makeText(UserRegisterActivity.this, getString(R.string.message_register_success), Toast.LENGTH_LONG).show();
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            // Guardar datos del usuario (no correo ni contraseña) en Firebase Realtime Database
                            Usuario writeUsuario = new Usuario(name1, name2,lastName1, lastName2,Character.toString(gender),birthDate);

                            // Referencia al usuario registrado (Si no existe, la crea).
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference referenceProfile = database.getReference("Registered Users");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUsuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(TAG, "onComplete RealtimeDatabase called");

                                    if (task.isSuccessful()){
                                        Log.d(TAG, "RealtimeDatabase write successful");
                                        // Enviar email de verificacion.
                                        firebaseUser.sendEmailVerification();

                                        Toast.makeText(UserRegisterActivity.this, R.string.toast_emailverification, Toast.LENGTH_LONG).show();

                                    // Abrir perfil del usuario luego del registro.
                                    Intent intent = new Intent(UserRegisterActivity.this, WelcomeActivity.class);

                                    //Esto cierra por completo las anteriores activity al momento de abrir la nueva, haciendo imposible regresar al registro una vez que lo haya completado.
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish(); // Aquí se cierra la Activity de registro.

                                    }else{
                                        Exception e = task.getException();
                                        Log.e(TAG, "Error al escribir en la base de datos en tiempo real", e);
                                    }


                                }
                            });

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
                                Toast.makeText(UserRegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

    }

    private boolean isAgeValid(String birthDate) {
        try {
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setLenient(false);
            java.util.Date birthDateParsed = dateFormat.parse(birthDate);

            Calendar birthCalendar = Calendar.getInstance();
            birthCalendar.setTime(birthDateParsed);

            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age >= 14;
        } catch (Exception e) {
            return false; // Fecha inválida
        }
    }


    private boolean esEmailValido(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void updateRegisterButtonState() {
        // Si ambos CheckBox están marcados, habilitar el botón
        if (cbTerminosCondiciones.isChecked() && cbConsentimientoDatos.isChecked()) {
            btnRegister.setEnabled(true); // Habilitar el botón
        } else {
            btnRegister.setEnabled(false); // Deshabilitar el botón
        }
    }


    // Método para mostrar el DialogFragment
    private void showTermsDialog(boolean isTerms) {
        TermsDialogFragment dialogFragment = TermsDialogFragment.newInstance(isTerms);
        dialogFragment.show(getSupportFragmentManager(), "TermsDialog");
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

        // Establecer la fecha máxima como la fecha actual (no permitir fechas futuras)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }
}