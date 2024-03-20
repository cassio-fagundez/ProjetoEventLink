package com.example.projetoeventlink;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {

    private EditText etFirstName, etSecondName, etFirstLastName, etSecondLastName, etEmail, etPassword, etDateOfBirth;
    private RadioGroup rgGender;
    private RadioButton rbtnSelected;
    private Button btnRegister;


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

        // Inicialización de RadioGroup e ImageButton
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