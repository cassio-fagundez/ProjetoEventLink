package com.example.projetoeventlink.Telas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    private TextView tvTitulo, tvFechaInicio, tvFechaFinal, tvHoraInicio, tvHoraFinal, tvDescripcion, tvDireccion, tvURL, tvCategorias, tvInteresados;
    private ImageView ivEventImage;
    private ImageButton btnLike;
    private Button btnEditar, btnReportar, btnPreguntas;
    private DatabaseReference eventRef;

    private int cont_interested;
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
        tvInteresados = findViewById(R.id.tvInteresados);

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

        checkUserInterestStatus(eventID);

        // Setear los valores en los campos
        tvInteresados.setText(cont_interested + getString(R.string.interested_count));
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

        // Listener para el botón de "Like"
        btnLike.setOnClickListener(v -> {

            eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        DataSnapshot interestedBySnapshot = snapshot.child("InterestedBy");
                        int interestedCount = snapshot.hasChild("Interested") ? snapshot.child("Interested").getValue(Integer.class) : 0;

                        if (interestedBySnapshot.hasChild(currentUserId)) {
                            // Si el usuario ya dio like, lo quitamos
                            eventRef.child("InterestedBy").child(currentUserId).removeValue();
                            eventRef.child("Interested").setValue(Math.max(0, interestedCount - 1));
                            cont_interested--;
                            tvInteresados.setText(cont_interested +" "+ getString(R.string.interested_count));
                            btnLike.setBackgroundResource(R.drawable.likeline); // Cambiar a ícono no interesado.
                            Toast.makeText(EventDetailsActivity.this, getString(R.string.no_longer_interested), Toast.LENGTH_SHORT).show();
                        } else {
                            // Si el usuario no dio like, lo agregamos
                            eventRef.child("InterestedBy").child(currentUserId).setValue(true);
                            eventRef.child("Interested").setValue(interestedCount + 1);
                            cont_interested++;
                            tvInteresados.setText(cont_interested +" "+ getString(R.string.interested_count));
                            btnLike.setBackgroundResource(R.drawable.likefull); // Cambiar a ícono interesado.
                            Toast.makeText(EventDetailsActivity.this, getString(R.string.interested_event), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Si el nodo no existe, lo inicializamos
                        eventRef.child("InterestedBy").child(currentUserId).setValue(true);
                        eventRef.child("Interested").setValue(1);
                        cont_interested=1;
                        tvInteresados.setText(cont_interested +" "+ getString(R.string.interested_count));
                        btnLike.setBackgroundResource(R.drawable.likefull); // Cambiar a ícono "me gusta"
                        Toast.makeText(EventDetailsActivity.this, getString(R.string.interested_event), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(EventDetailsActivity.this, getString(R.string.interest_update_error), Toast.LENGTH_SHORT).show();
                }
            });
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
            builder.setTitle(getString(R.string.report_event_title));

            // Crear un EditText para escribir el motivo
            final EditText input = new EditText(EventDetailsActivity.this);
            input.setHint(getString(R.string.report_hint));
            builder.setView(input);

            // Botón para confirmar el reporte
            builder.setPositiveButton(getString(R.string.report), (dialog, which) -> {
                String motivoReporte = input.getText().toString().trim();

                if (motivoReporte.isEmpty()) {
                    Toast.makeText(EventDetailsActivity.this, getString(R.string.enter_reason_or_cancel), Toast.LENGTH_SHORT).show();
                    return;
                }

                String eventoID = intent.getStringExtra("eventId");

                if (eventoID == null || eventoID.isEmpty()) {
                    Toast.makeText(EventDetailsActivity.this, getString(R.string.event_id_not_found), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Buscar el organizadorUID usando eventoID
                DatabaseReference eventosRef = FirebaseDatabase.getInstance().getReference("Events");
                eventosRef.child(eventoID).get().addOnSuccessListener(eventoSnapshot -> {
                    if (eventoSnapshot.exists()) {

                        if (organizadorUID == null || organizadorUID.isEmpty()) {
                            Toast.makeText(EventDetailsActivity.this, getString(R.string.organizer_not_found), Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(EventDetailsActivity.this, getString(R.string.report_success), Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(EventDetailsActivity.this, getString(R.string.report_failure), Toast.LENGTH_SHORT).show();
                                        });

                            } else {
                                Toast.makeText(EventDetailsActivity.this, getString(R.string.organizer_not_found), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(EventDetailsActivity.this, getString(R.string.error_finding_organizer), Toast.LENGTH_SHORT).show();
                        });

                    } else {
                        Toast.makeText(EventDetailsActivity.this, getString(R.string.event_not_found), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(EventDetailsActivity.this, getString(R.string.search_event_error), Toast.LENGTH_SHORT).show();
                });
            });

            // Botón para cancelar
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

            // Mostrar el cuadro de diálogo
            builder.show();
        });

    }

    private void checkUserInterestStatus(String eventID) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Referencia al nodo de evento específico en Firebase
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events").child(eventID);

        // Verificar si el usuario está interesado
        eventRef.child("InterestedBy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(currentUserId)) {
                    // El usuario está interesado
                    btnLike.setBackgroundResource(R.drawable.likefull); // Cambia a ícono "like"
                } else {
                    // El usuario no está interesado
                    btnLike.setBackgroundResource(R.drawable.likeline); // Cambia a ícono "likeline"
                }

                // Verificar si existe el valor "Interesados" y actualizarlo
                eventRef.child("Interested").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Si existe, tomar su valor y guardarlo en la variable global
                            cont_interested = dataSnapshot.getValue(Integer.class);
                        } else {
                            // Si no existe, inicializarlo a 0
                            eventRef.child("Interested").setValue(0);
                            cont_interested = 0;
                        }

                        // Actualizar la UI después de obtener el valor correcto de "Interested"
                        tvInteresados.setText(cont_interested +" "+ getString(R.string.interested_count));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Error", "Error al leer el valor de 'Interesados': ", databaseError.toException());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(EventDetailsActivity.this, getString(R.string.error_checking_interest), Toast.LENGTH_SHORT).show();
            }
        });
    }



}
