package com.example.projetoeventlink.Telas;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projetoeventlink.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class EventEditActivity extends AppCompatActivity {

    private TextView tvTituloUbicacion;
    private EditText etTitulo, etFechaInicio, etFechaFinal, etHoraInicio, etHoraFinal, etDescripcion, etDireccionPresencial, etURLVirtual, etCategorias;
    private Button btnGuardarCambios, btnEliminarEvento, btnSeleccionarUbicacion;
    private ImageButton btnLimpiarFechaFinal, btnLimpiarHoraFinal;
    private double Latitud, Longitud;

    private String eventId; // Para identificar el evento en Firebase

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        // Vincular vistas
        etTitulo = findViewById(R.id.etTitulo);
        etFechaInicio = findViewById(R.id.etFechaInicio);
        etFechaFinal = findViewById(R.id.etFechaFinal);
        etHoraInicio = findViewById(R.id.etHoraInicio);
        etHoraFinal = findViewById(R.id.etHoraFinal);
        etDescripcion = findViewById(R.id.etDescripcion);
        etDireccionPresencial = findViewById(R.id.etDireccionPresencial);
        etURLVirtual = findViewById(R.id.etURLVirtual);
        etCategorias = findViewById(R.id.etCategorias);
        btnGuardarCambios = findViewById(R.id.btnGuardarEvento);
        btnEliminarEvento = findViewById(R.id.btnEliminarEvento);
        btnLimpiarFechaFinal = findViewById(R.id.btnClearFechaFinal);
        btnLimpiarHoraFinal = findViewById(R.id.btnClearHoraFinal);
        btnSeleccionarUbicacion = findViewById(R.id.btnSeleccionarUbicacion);
        tvTituloUbicacion = findViewById(R.id.tvTituloUbicacion);

        // Obtener datos del Intent
        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        etTitulo.setText(intent.getStringExtra("titulo"));
        etFechaInicio.setText(intent.getStringExtra("fechaInicio"));
        etFechaFinal.setText(intent.getStringExtra("fechaFinal"));
        etHoraInicio.setText(intent.getStringExtra("horaInicio"));
        etHoraFinal.setText(intent.getStringExtra("horaFinal"));
        etDescripcion.setText(intent.getStringExtra("descripcion"));
        etDireccionPresencial.setText(intent.getStringExtra("direccionPresencial"));
        etURLVirtual.setText(intent.getStringExtra("urlVirtual"));
        etCategorias.setText(intent.getStringExtra("categorias"));
        Latitud = intent.getDoubleExtra("latitud", 0.0);
        Longitud = intent.getDoubleExtra("longitud", 0.0);
        tvTituloUbicacion.setText("Seleccionar ubicación: ("+Latitud+","+Longitud+")");

        // Configurar seleccionadores de fecha y hora
        etFechaInicio.setOnClickListener(v -> showDatePickerDialog(etFechaInicio));
        etFechaFinal.setOnClickListener(v -> showDatePickerDialog(etFechaFinal));
        etHoraInicio.setOnClickListener(v -> showTimePickerDialog(etHoraInicio));
        etHoraFinal.setOnClickListener(v -> showTimePickerDialog(etHoraFinal));

        // Configurar botones para limpiar campos
        btnLimpiarFechaFinal.setOnClickListener(v -> {
            etFechaFinal.setText("");
        });
        btnLimpiarHoraFinal.setOnClickListener(v -> {
            etHoraFinal.setText("");
        });

        // Listener para guardar cambios, eliminar evento y mapa.
        btnGuardarCambios.setOnClickListener(v -> guardarCambios());
        btnEliminarEvento.setOnClickListener(v -> mostrarConfirmacionEliminar());
        btnSeleccionarUbicacion.setOnClickListener(v -> {
            // Crea una instancia del fragmento
            MapLocationSelectorFragment mapFragment = new MapLocationSelectorFragment();

            // Establece un listener para recibir las coordenadas seleccionadas
            mapFragment.setOnLocationSelectedListener((latitude, longitude) -> {
                Latitud = latitude;
                Longitud = longitude;
                tvTituloUbicacion.setText("Seleccionar ubicación: ("+Latitud+","+Longitud+")");
                Toast.makeText(this, "Ubicación seleccionada:\n Lat: " + Latitud + "\nLon: " + Longitud, Toast.LENGTH_SHORT).show();
            });

            // Muestra el fragmento como diálogo
            mapFragment.show(getSupportFragmentManager(), "MapLocationSelector");
        });

        if (esEventoPasado(etFechaInicio.getText().toString(), etFechaFinal.getText().toString())){
            etTitulo.setEnabled(false);
            etFechaInicio.setEnabled(false);
            etFechaFinal.setEnabled(false);
            etHoraInicio.setEnabled(false);
            etHoraFinal.setEnabled(false);
            etDescripcion.setEnabled(false);
            etDireccionPresencial.setEnabled(false);
            etURLVirtual.setEnabled(false);
            etCategorias.setEnabled(false);
            btnSeleccionarUbicacion.setEnabled(false);
            btnGuardarCambios.setEnabled(false);

            // Mostrar un mensaje al usuario indicando que el evento no se puede editar
            Toast.makeText(this, "Este evento pertenece a una fecha pasada y no se puede editar.", Toast.LENGTH_LONG).show();
        }

    }

    private boolean esEventoPasado(String fechaInicio, @Nullable String fechaFinal) {
        try {
            // Formato de fecha esperado
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setLenient(false);

            // Convertir fecha de inicio
            java.util.Date fechaInicioEvento = dateFormat.parse(fechaInicio);

            // Si fechaFinal es nula o está vacía, usar la misma fecha que fechaInicio
            java.util.Date fechaFinalEvento = (fechaFinal == null || fechaFinal.isEmpty())
                    ? fechaInicioEvento
                    : dateFormat.parse(fechaFinal);

            // Obtener la fecha actual sin la hora
            Calendar hoy = Calendar.getInstance();
            hoy.set(Calendar.HOUR_OF_DAY, 0);
            hoy.set(Calendar.MINUTE, 0);
            hoy.set(Calendar.SECOND, 0);
            hoy.set(Calendar.MILLISECOND, 0);

            // Comparar fechas: el evento es pasado si la fecha final es anterior al día actual
            return fechaFinalEvento.before(hoy.getTime());
        } catch (Exception e) {
            // Manejo de errores: si ocurre algún problema, asumir que no es un evento pasado
            Log.e("FechaEvento", "Error al analizar las fechas: " + e.getMessage());
            return false;
        }
    }

    private void showDatePickerDialog(EditText editText) {
        // Obtener fecha actual
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Mostrar DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            editText.setText(selectedDate);
        }, year, month, day);

        // Establecer la fecha mínima como la fecha actual (no permitir fechas pasadas)
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void showTimePickerDialog(EditText editText) {
        // Obtener hora actual
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Mostrar TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
            editText.setText(selectedTime);
        }, hour, minute, true);

        // Establecer la hora mínima como la hora actual (no permitir horas pasadas)
        timePickerDialog.updateTime(hour, minute);

        timePickerDialog.show();
    }

    private void mostrarConfirmacionEliminar() {
        // Crear el cuadro de diálogo de confirmación
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Evento")
                .setMessage("¿Estás seguro de que deseas eliminar este evento? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarEvento())
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void eliminarEvento() {
        if (eventId != null && !eventId.isEmpty()) {
            // Eliminar el evento de Firebase
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Events").child(eventId);
            databaseRef.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Mostrar mensaje de éxito
                        Toast.makeText(this, "Evento eliminado con éxito", Toast.LENGTH_SHORT).show();
                        // Finalizar Activity y regresar
                        startActivity(new Intent(EventEditActivity.this, WelcomeActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // Mostrar mensaje de error
                        Toast.makeText(this, "Error al eliminar el evento: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Mostrar mensaje de error si el ID del evento es nulo
            Toast.makeText(this, "No se pudo identificar el evento a eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarCambios() {
        // Obtener los datos ingresados
        String titulo = etTitulo.getText().toString();
        String fechaInicio = etFechaInicio.getText().toString();
        String fechaFinal = etFechaFinal.getText().toString();
        String horaInicio = etHoraInicio.getText().toString();
        String horaFinal = etHoraFinal.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String direccionPresencial = etDireccionPresencial.getText().toString();
        String urlVirtual = etURLVirtual.getText().toString();
        String categorias = etCategorias.getText().toString();

        // Validación de campos obligatorios
        if (titulo.isEmpty() || fechaInicio.isEmpty() || horaInicio.isEmpty() || descripcion.isEmpty() || categorias.isEmpty()) {
            // Mostrar mensaje de error si hay campos obligatorios vacíos
            Toast.makeText(this, "Por favor, complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener referencia de la base de datos para el evento
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Events").child(eventId);

        // Actualizar campos obligatorios
        databaseRef.child("titulo").setValue(titulo);
        databaseRef.child("fechaInicio").setValue(fechaInicio);
        databaseRef.child("horaInicio").setValue(horaInicio);
        databaseRef.child("descripcion").setValue(descripcion);
        databaseRef.child("categorias").setValue(categorias);
        databaseRef.child("latitud").setValue(Latitud);
        databaseRef.child("longitud").setValue(Longitud);

        // Actualizar o eliminar campos opcionales si es necesario
        if (!fechaFinal.isEmpty()) {
            databaseRef.child("fechaFinal").setValue(fechaFinal);
        } else {
            databaseRef.child("fechaFinal").removeValue(); // Eliminar si está vacío
        }

        if (!horaFinal.isEmpty()) {
            databaseRef.child("horaFinal").setValue(horaFinal);
        } else {
            databaseRef.child("horaFinal").removeValue(); // Eliminar si está vacío
        }

        if (!direccionPresencial.isEmpty()) {
            databaseRef.child("direccionPresencial").setValue(direccionPresencial);
        } else {
            databaseRef.child("direccionPresencial").removeValue(); // Eliminar si está vacío
        }

        if (!urlVirtual.isEmpty()) {
            databaseRef.child("urlVirtual").setValue(urlVirtual);
        } else {
            databaseRef.child("urlVirtual").removeValue(); // Eliminar si está vacío
        }

        // Finalizar Activity y regresar
        finish();
    }
}
