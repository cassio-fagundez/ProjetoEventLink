package com.example.projetoeventlink.Telas;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projetoeventlink.Logica.Evento;
import com.example.projetoeventlink.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class EventRegisterActivity extends AppCompatActivity {

    private TextView tvURLVirtual, tvDireccionPresencial, tvTituloUbicacion;
    private TextInputLayout tilURLVirtual, tilDireccionPresencial;
    private EditText etTitulo, etFechaInicio, etFechaFinal, etHoraInicio, etHoraFinal, etDescripcion, etCategorias, etURLVirtual, etDireccionPresencial;
    private RadioGroup rgTipoEvento, rgTipoFecha, rgTipoHorario;
    private RadioButton rbtnSelected;
    private Button btnRegistrarEvento, btnSeleccionarUbicacion;
    private FirebaseAuth authProfile;
    private static final String TAG = "LOGRegisterEventActivity";
    private static final int MAP_REQUEST_CODE = 1001;
    private double Latitud; // Default
    private double Longitud; // Default



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_register);

        // Inicialización de EditText
        etTitulo = findViewById(R.id.etTitulo);
        etFechaInicio = findViewById(R.id.etFechaInicio);
        etFechaFinal = findViewById(R.id.etFechaFinal);
        etHoraInicio = findViewById(R.id.etHoraInicio);
        etHoraFinal = findViewById(R.id.etHoraFinal);
        etDescripcion = findViewById(R.id.etDescripcion);
        etCategorias = findViewById(R.id.etCategorias);
        tilURLVirtual = findViewById(R.id.tilURLVirtual);
        tvURLVirtual = findViewById(R.id.tvURLVirtual);
        etURLVirtual = findViewById(R.id.etURLVirtual);
        tilDireccionPresencial = findViewById(R.id.tilDireccionPresencial);
        tvDireccionPresencial = findViewById(R.id.tvDireccionPresencial);
        etDireccionPresencial = findViewById(R.id.etDireccionPresencial);
        tvTituloUbicacion = findViewById(R.id.tvTituloUbicacion);

        // Inicialización de RadioGroup y RadioButton
        rgTipoFecha = findViewById(R.id.rgEventoDuracion);
        rgTipoHorario = findViewById(R.id.rgHorario);
        rgTipoEvento = findViewById(R.id.rgTipoEvento);

        btnSeleccionarUbicacion = findViewById(R.id.btnSeleccionarUbicacion);
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



        // Inicialización del botón Registrar Evento
        btnRegistrarEvento = findViewById(R.id.btnRegistrarEvento);
        btnRegistrarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Verificar si algún campo está vacío
                if (TextUtils.isEmpty(etTitulo.getText())
                        || TextUtils.isEmpty(etFechaInicio.getText())
                        || TextUtils.isEmpty(etHoraInicio.getText())
                        || TextUtils.isEmpty(etDescripcion.getText())
                        || TextUtils.isEmpty(etCategorias.getText())) {
                    // Mostrar Toast indicando que se deben completar todos los campos
                    Toast.makeText(EventRegisterActivity.this, getString(R.string.toast_completeall), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Obtener los datos ingresados
                String titulo = etTitulo.getText().toString().trim();
                String fechaInicio = etFechaInicio.getText().toString().trim();
                String fechaFinal = etFechaFinal.getText().toString().trim();
                String horaInicio = etHoraInicio.getText().toString().trim();
                String horaFinal = etHoraFinal.getText().toString().trim();
                String descripcion = etDescripcion.getText().toString().trim();
                String categorias = etCategorias.getText().toString().trim();

                // Obtener el tipo de evento seleccionado
                int selectedTipoEventoId = rgTipoEvento.getCheckedRadioButtonId();
                rbtnSelected = findViewById(selectedTipoEventoId);

                String tipoEvento = rbtnSelected.getText().toString();
                String urlVirtual = null;
                String direccionPresencial = null;

                if (tipoEvento.equals("Virtual")) {
                    urlVirtual = etURLVirtual.getText().toString().trim();
                } else if (tipoEvento.equals("Presencial")) {
                    direccionPresencial = etDireccionPresencial.getText().toString().trim();
                }

                // Llamada al método de registro de evento
                registerEvent(titulo, fechaInicio, fechaFinal, horaInicio, horaFinal, tipoEvento, urlVirtual, direccionPresencial, descripcion, categorias);
            }
        });

        // Mostrar el DatePicker cuando se haga clic en la fecha de inicio
        etFechaInicio.setOnClickListener(v -> showDatePickerDialog("Inicio"));

        // Mostrar el DatePicker cuando se haga clic en la fecha final
        etFechaFinal.setOnClickListener(v -> showDatePickerDialog("Final"));

        // Mostrar el TimePicker para la hora de inicio
        etHoraInicio.setOnClickListener(v -> showTimePickerDialog("Hora Inicio"));

        // Mostrar el TimePicker para la hora final
        etHoraFinal.setOnClickListener(v -> showTimePickerDialog("Hora Final"));

        // Cambio de visibilidad de los campos de fecha según el tipo de evento
        rgTipoFecha.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbtnUnDia) {
                // Mostrar solo la fecha de inicio
                etFechaInicio.setVisibility(View.VISIBLE);
                etFechaFinal.setText("");
                etFechaFinal.setVisibility(View.GONE);
            } else if (checkedId == R.id.rbtnVariosDias) {
                // Mostrar tanto la fecha de inicio como la fecha final
                etFechaInicio.setVisibility(View.VISIBLE);
                etFechaFinal.setVisibility(View.VISIBLE);
            }
        });

        // Cambio de visibilidad de los campos de hora según el tipo de evento
        rgTipoHorario.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbtnFinalNo) {
                // Mostrar solo la hora de inicio
                etHoraInicio.setVisibility(View.VISIBLE);
                etHoraFinal.setText("");
                etHoraFinal.setVisibility(View.GONE);
            } else if (checkedId == R.id.rbtnFinalSi) {
                // Mostrar tanto la hora de inicio como la hora final
                etHoraInicio.setVisibility(View.VISIBLE);
                etHoraFinal.setVisibility(View.VISIBLE);
            }
        });


        // Cambio de visibilidad de los campos según el tipo de evento
        rgTipoEvento.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbtnVirtual) {
                // Mostrar el campo URL y ocultar la dirección presencial
                tvURLVirtual.setVisibility(View.VISIBLE);
                tilURLVirtual.setVisibility(View.VISIBLE);
                etURLVirtual.setVisibility(View.VISIBLE);
                tvDireccionPresencial.setVisibility(View.GONE);
                tilDireccionPresencial.setVisibility(View.GONE);
                etDireccionPresencial.setVisibility(View.GONE);
                tvTituloUbicacion.setVisibility(View.GONE);
                btnSeleccionarUbicacion.setVisibility(View.GONE);
            } else if (checkedId == R.id.rbtnPresencial) {
                // Mostrar la dirección presencial y ocultar el URL virtual
                tvURLVirtual.setVisibility(View.GONE);
                tilURLVirtual.setVisibility(View.GONE);
                etURLVirtual.setVisibility(View.GONE);
                tvDireccionPresencial.setVisibility(View.VISIBLE);
                tilDireccionPresencial.setVisibility(View.VISIBLE);
                etDireccionPresencial.setVisibility(View.VISIBLE);
                tvTituloUbicacion.setVisibility(View.VISIBLE);
                btnSeleccionarUbicacion.setVisibility(View.VISIBLE);
            }
        });


        rgTipoFecha.check(R.id.rbtnVariosDias);
        rgTipoHorario.check(R.id.rbtnFinalSi);
        rgTipoEvento.check(R.id.rbtnPresencial);



    }


    // Registro del evento usando los datos proporcionados
    private void registerEvent(String titulo, String fechaInicio, String fechaFinal, String horaInicio, String horaFinal, String tipoEvento, String urlVirtual, String direccionPresencial, String descripcion, String categorias) {
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        // Referencia a la base de datos de Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference referenceEvent = database.getReference("Events");

        // Generar EventID único
        String eventID = referenceEvent.push().getKey();

        // Crear objeto Evento con la EventID generada
        Evento evento = new Evento(eventID, titulo, descripcion, direccionPresencial, urlVirtual, fechaInicio, fechaFinal, horaInicio, horaFinal, categorias, firebaseUser.getUid(), Latitud, Longitud);// Guardar evento en Firebase con EventID como clave

        referenceEvent.child(eventID).setValue(evento).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EventRegisterActivity.this, getString(R.string.message_register_success), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Log.e(TAG, "Error al registrar evento", task.getException());
                    Toast.makeText(EventRegisterActivity.this, getString(R.string.error_register_failed), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Método para mostrar el DatePicker
    private void showDatePickerDialog(String tipoFecha) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    if (tipoFecha.equals("Inicio")) {
                        etFechaInicio.setText(selectedDate);
                    } else if (tipoFecha.equals("Final")) {
                        etFechaFinal.setText(selectedDate);
                    }
                }, year, month, day);

        // Establecer la fecha mínima como la fecha actual (no permitir fechas pasadas)
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    // Método para mostrar el TimePicker
    private void showTimePickerDialog(String tipoHora) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    String selectedTime = selectedHour + ":" + (selectedMinute < 10 ? "0" + selectedMinute : selectedMinute);
                    if (tipoHora.equals("Hora Inicio")) {
                        etHoraInicio.setText(selectedTime);
                    } else if (tipoHora.equals("Hora Final")) {
                        etHoraFinal.setText(selectedTime);
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }



}
