package com.example.projetoeventlink.Telas;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projetoeventlink.Logica.DocumentValidator;
import com.example.projetoeventlink.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SettingsProfileActivity extends AppCompatActivity {

    private TextView tvOrganizerMessage;
    private EditText etFirstName, etSecondName, etFirstLastName, etSecondLastName, etDateOfBirth, etDocument;
    private RadioButton rbtnGenderM, rbtnGenderF, rbtnGenderO;
    private Button btnSaveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_profile);

        // Inicializar vistas
        tvOrganizerMessage = findViewById(R.id.tvOrganizerMessage);
        etFirstName = findViewById(R.id.etFirstName);
        etSecondName = findViewById(R.id.etSecondName);
        etFirstLastName = findViewById(R.id.etFirstLastName);
        etSecondLastName = findViewById(R.id.etSecondLastName);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etDocument = findViewById(R.id.etDocument);
        rbtnGenderM = findViewById(R.id.rbtnGenderM);
        rbtnGenderF = findViewById(R.id.rbtnGenderF);
        rbtnGenderO = findViewById(R.id.rbtnGenderO);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Configurar el DatePicker para el campo de fecha
        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        // Configurar el botón "Guardar cambios"
        btnSaveChanges.setOnClickListener(v -> saveChanges());

        // Cargar datos del usuario desde Firebase
        loadUserData();
    }

    private void loadUserData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(userId);

        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                // Obtener datos desde Firebase
                String firstName = snapshot.child("nombre1").getValue(String.class);
                String secondName = snapshot.child("nombre2").exists() ? snapshot.child("nombre2").getValue(String.class) : "";
                String firstLastName = snapshot.child("apellido1").getValue(String.class);
                String secondLastName = snapshot.child("apellido2").exists() ? snapshot.child("apellido2").getValue(String.class) : "";
                String dateOfBirth = snapshot.child("fechaNacimiento").getValue(String.class);
                String document = snapshot.child("documento").exists() ? snapshot.child("documento").getValue(String.class) : "";
                String gender = snapshot.child("genero").getValue(String.class);

                // Asignar valores
                etFirstName.setText(firstName);
                etSecondName.setText(secondName);
                etFirstLastName.setText(firstLastName);
                etSecondLastName.setText(secondLastName);
                etDateOfBirth.setText(dateOfBirth);

                if (document.isEmpty()) {
                    etDocument.setEnabled(true); // Permitir agregar si no existe documento
                } else {
                    etDocument.setText(document);
                    etDocument.setEnabled(false); // Bloquear edición si ya existe documento
                }

                if ("M".equals(gender)) rbtnGenderM.setChecked(true);
                else if ("F".equals(gender)) rbtnGenderF.setChecked(true);
                else rbtnGenderO.setChecked(true);

            } else {
                Toast.makeText(this, "No se encontraron datos del usuario.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(error ->
                Toast.makeText(this, "Error al cargar datos: " + error.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDateOfBirth.setText(formattedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void saveChanges() {
        String firstName = etFirstName.getText().toString().trim();
        String secondName = etSecondName.getText().toString().trim();
        String firstLastName = etFirstLastName.getText().toString().trim();
        String secondLastName = etSecondLastName.getText().toString().trim();
        String dateOfBirth = etDateOfBirth.getText().toString().trim();
        String document = etDocument.getText().toString().trim();

        if (firstName.isEmpty() || firstLastName.isEmpty() || dateOfBirth.isEmpty()) {
            Toast.makeText(this, "Por favor, completa los campos obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!document.isEmpty()) {
            if (!(DocumentValidator.validarCI(document) || DocumentValidator.validarCPF(document))) {
                Toast.makeText(this, "Documento inválido.", Toast.LENGTH_SHORT).show();
                return;
            }

            checkIfDocumentExists(document, exists -> {
                if (exists) {
                    Toast.makeText(this, "El documento ya está registrado.", Toast.LENGTH_SHORT).show();
                } else {
                    updateUserData(firstName, secondName, firstLastName, secondLastName, dateOfBirth, document);
                }
            });
        } else {
            updateUserData(firstName, secondName, firstLastName, secondLastName, dateOfBirth, null);
        }
    }

    private void checkIfDocumentExists(String document, DocumentExistsCallback callback) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Registered Users");

        usersRef.orderByChild("documento").equalTo(document).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exists = false;
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                for (DataSnapshot child : snapshot.getChildren()) {
                    String uid = child.getKey();
                    if (!currentUserId.equals(uid)) {
                        exists = true;
                        break;
                    }
                }
                callback.onResult(exists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onResult(false);
            }
        });
    }

    private void updateUserData(String firstName, String secondName, String firstLastName, String secondLastName, String dateOfBirth, String document) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre1", firstName);
        updates.put("apellido1", firstLastName);
        updates.put("fechaNacimiento", dateOfBirth);

        if (secondName != null) updates.put("nombre2", secondName);
        if (secondLastName != null) updates.put("apellido2", secondLastName);
        if (document != null) updates.put("documento", document);

        userRef.updateChildren(updates).addOnSuccessListener(aVoid -> {
            etDocument.setEnabled(false);
            Toast.makeText(this, "Datos actualizados correctamente.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(error ->
                Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show());
    }

    interface DocumentExistsCallback {
        void onResult(boolean exists);
    }
}
