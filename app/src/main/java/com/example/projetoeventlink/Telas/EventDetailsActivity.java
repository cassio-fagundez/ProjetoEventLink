package com.example.projetoeventlink.Telas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projetoeventlink.Logica.Reporte;
import com.example.projetoeventlink.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventDetailsActivity extends AppCompatActivity {

    private TextView tvTitulo, tvFechaInicio, tvFechaFinal, tvHoraInicio, tvHoraFinal, tvDescripcion, tvDireccion, tvURL, tvCategorias;
    private ImageView ivEventImage;
    private ImageButton btnLike;
    private Button btnEditar, btnReportar, btnPreguntas;
    private DatabaseReference eventRef;

    private boolean OWNER = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Vincular vistas
        tvTitulo = findViewById(R.id.tvTitulo);
        tvFechaInicio = findViewById(R.id.tvFechaInicio);
        tvFechaFinal = findViewById(R.id.tvFechaFinal);
        tvHoraInicio = findViewById(R.id.tvHoraInicio);
        tvHoraFinal = findViewById(R.id.tvHoraFinal);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        tvDireccion = findViewById(R.id.tvDireccion);
        tvURL = findViewById(R.id.tvURL);
        tvCategorias = findViewById(R.id.tvCategorias);
        ivEventImage = findViewById(R.id.ivEventImage);
        btnEditar = findViewById(R.id.btnEditar);
        btnReportar = findViewById(R.id.btnReportar);
        btnLike = findViewById(R.id.btnLike);
        btnPreguntas = findViewById(R.id.btnPreguntas);

        // Obtener datos del Intent
        Intent intent = getIntent();
        String eventID = intent.getStringExtra("eventId");
        String titulo = intent.getStringExtra("titulo");
        String fechaInicio = intent.getStringExtra("fechaInicio");
        String fechaFinal = intent.getStringExtra("fechaFinal");
        String horaInicio = intent.getStringExtra("horaInicio");
        String horaFinal = intent.getStringExtra("horaFinal");
        String descripcion = intent.getStringExtra("descripcion");
        String direccionPresencial = intent.getStringExtra("direccionPresencial");
        String urlVirtual = intent.getStringExtra("urlVirtual");
        String categorias = intent.getStringExtra("categorias");
        String organizadorUID = intent.getStringExtra("organizadorUID");
        double latitud = intent.getDoubleExtra("latitud", 0.0);
        double longitud = intent.getDoubleExtra("longitud", 0.0);


        // Setear los valores en los campos
        tvTitulo.setText(titulo);
        tvFechaInicio.setText(fechaInicio);
        tvFechaFinal.setText(fechaFinal);
        tvHoraInicio.setText(horaInicio);
        tvHoraFinal.setText(horaFinal);
        tvDescripcion.setText(descripcion);
        tvDireccion.setText(direccionPresencial);
        tvURL.setText(urlVirtual);
        tvCategorias.setText(categorias);

        // Referencia al evento en Firebase
        eventRef = FirebaseDatabase.getInstance().getReference("Events").child(eventID);

        // Verificar si el usuario actual es el creador
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String creatorId = snapshot.child("organizadorUID").getValue(String.class);
                    if (currentUserId.equals(creatorId)) {
                        OWNER = true;
                        btnEditar.setVisibility(Button.VISIBLE);
                    } else {
                        OWNER = false;
                        btnLike.setVisibility(View.VISIBLE);
                        btnReportar.setVisibility(Button.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Manejar errores
            }
        });

        //Listener para Preguntas y Respuestas
        btnPreguntas.setOnClickListener(v -> {
            Intent intent1 = new Intent(EventDetailsActivity.this, EventQuestionsActivity.class);
            intent1.putExtra("eventId", eventID);
            intent1.putExtra("titulo", titulo);
            intent1.putExtra("Owner", OWNER);
            startActivity(intent1);
        });

        // Listener para el botón de editar
        btnEditar.setOnClickListener(v -> {
            Intent intent2 = new Intent(EventDetailsActivity.this, EventEditActivity.class);
            intent2.putExtra("eventId", eventID);
            intent2.putExtra("titulo", titulo);
            intent2.putExtra("fechaInicio", fechaInicio);
            intent2.putExtra("fechaFinal", fechaFinal);
            intent2.putExtra("horaInicio", horaInicio);
            intent2.putExtra("horaFinal", horaFinal);
            intent2.putExtra("descripcion", descripcion);
            intent2.putExtra("direccionPresencial", direccionPresencial);
            intent2.putExtra("urlVirtual", urlVirtual);
            intent2.putExtra("categorias", categorias);
            intent2.putExtra("organizadorUID", organizadorUID);
            intent2.putExtra("latitud", latitud);
            intent2.putExtra("longitud", longitud);

            startActivity(intent2);
        });

        // Listener para el botón de reportar
        btnReportar.setOnClickListener(v -> {
            // Crear un AlertDialog para ingresar el motivo del reporte
            AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailsActivity.this);
            builder.setTitle("Reportar Evento");

            // Crear un EditText para escribir el motivo
            final EditText input = new EditText(EventDetailsActivity.this);
            input.setHint("Escribe el motivo del reporte: ");
            builder.setView(input);

            // Botón para confirmar el reporte
            builder.setPositiveButton("Reportar", (dialog, which) -> {
                String motivoReporte = input.getText().toString().trim();

                if (motivoReporte.isEmpty()) {
                    Toast.makeText(EventDetailsActivity.this, "Por favor, ingresa un motivo o cancela.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String eventoID = intent.getStringExtra("eventId");

                if (eventoID == null || eventoID.isEmpty()) {
                    Toast.makeText(EventDetailsActivity.this, "No se encontró el ID del evento.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Buscar el organizadorUID usando eventoID
                DatabaseReference eventosRef = FirebaseDatabase.getInstance().getReference("Events");
                eventosRef.child(eventoID).get().addOnSuccessListener(eventoSnapshot -> {
                    if (eventoSnapshot.exists()) {

                        if (organizadorUID == null || organizadorUID.isEmpty()) {
                            Toast.makeText(EventDetailsActivity.this, "No se encontró el organizador del evento.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Buscar el documentoOrganizador usando organizadorUID
                        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("Registered Users");
                        usuariosRef.child(organizadorUID).get().addOnSuccessListener(usuarioSnapshot -> {
                            if (usuarioSnapshot.exists()) {
                                String documentoOrganizador = usuarioSnapshot.child("documento").getValue(String.class);

                                if (documentoOrganizador == null || documentoOrganizador.isEmpty()) {
                                    documentoOrganizador = "No disponible"; // Valor por defecto si no tiene documento, que sí debería tener.
                                }

                                // Crear el reporte
                                String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                String reporteID = FirebaseDatabase.getInstance().getReference().child("Reports").push().getKey();

                                Reporte reporte = new Reporte(reporteID, usuarioID, organizadorUID, documentoOrganizador, motivoReporte);

                                // Guardar el reporte en Firebase
                                DatabaseReference reportesRef = FirebaseDatabase.getInstance().getReference("Reports");
                                reportesRef.child(reporteID).setValue(reporte)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(EventDetailsActivity.this, "Reporte enviado exitosamente.", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(EventDetailsActivity.this, "Error al enviar el reporte.", Toast.LENGTH_SHORT).show();
                                        });

                            } else {
                                Toast.makeText(EventDetailsActivity.this, "No se encontró al organizador.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(EventDetailsActivity.this, "Error al buscar al organizador.", Toast.LENGTH_SHORT).show();
                        });

                    } else {
                        Toast.makeText(EventDetailsActivity.this, "No se encontró el evento.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(EventDetailsActivity.this, "Error al buscar el evento.", Toast.LENGTH_SHORT).show();
                });
            });

            // Botón para cancelar
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

            // Mostrar el cuadro de diálogo
            builder.show();
        });

    }
}
