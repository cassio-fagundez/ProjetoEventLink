package com.example.projetoeventlink.Telas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.projetoeventlink.Logica.EventAdapter;
import com.example.projetoeventlink.Logica.Evento;
import com.example.projetoeventlink.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EventListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<Evento> eventList;
    private RadioGroup radioGroupOptions; // RadioGroup para opciones
    private static final String TAG = "LOGEventListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        // Inicialización del RecyclerView
        recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Crear la lista que se pasará al adapter
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(getApplicationContext(), eventList);
        recyclerView.setAdapter(eventAdapter);

        // Inicialización del RadioGroup
        radioGroupOptions = findViewById(R.id.radioGroupOptions);

        // Escuchar cambios en el RadioGroup
        radioGroupOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioMyEvents) {
                cargarMisEventos();
            } else if (checkedId == R.id.radioOtherEvents) {
                cargarOtrosEventos();
            }
        });

        // Cargar los eventos iniciales (por defecto "Mis eventos")
        cargarMisEventos();
    }

    private void cargarMisEventos() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("Events");

        // Filtrar eventos por el UID del usuario actual
        eventsRef.orderByChild("organizadorUID").equalTo(currentUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                eventList.clear();

                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Evento event = eventSnapshot.getValue(Evento.class);
                    if (event != null) {
                        eventList.add(event);
                    }
                }

                ordenarEventosPorEstadoYFecha(eventList);
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error al obtener los eventos: " + error.getMessage());
                Toast.makeText(EventListActivity.this, getString(R.string.error_al_cargar_eventos), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarOtrosEventos() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("Events");

        // Filtrar eventos excluyendo los del usuario actual
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                eventList.clear();

                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Evento event = eventSnapshot.getValue(Evento.class);
                    if (event != null && !currentUserUid.equals(event.getOrganizadorUID())) {
                        eventList.add(event);
                    }
                }

                ordenarEventosPorEstadoYFecha(eventList);
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error al obtener los eventos: " + error.getMessage());
                Toast.makeText(EventListActivity.this, getString(R.string.error_al_cargar_eventos), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ordenarEventosPorEstadoYFecha(ArrayList<Evento> eventos) {
        eventos.sort((evento1, evento2) -> {
            long fechaActual = System.currentTimeMillis();
            String estado1 = determinarEstadoEvento(evento1, fechaActual);
            String estado2 = determinarEstadoEvento(evento2, fechaActual);

            int prioridad1 = obtenerPrioridadEstado(estado1);
            int prioridad2 = obtenerPrioridadEstado(estado2);

            if (prioridad1 != prioridad2) {
                return Integer.compare(prioridad1, prioridad2);
            }

            long fechaInicio1 = convertirFechaALong(evento1.getFechaInicio());
            long fechaInicio2 = convertirFechaALong(evento2.getFechaInicio());

            if ("Próximamente".equals(estado1)) {
                return Long.compare(fechaInicio1, fechaInicio2);
            } else if ("Finalizado".equals(estado1)) {
                return Long.compare(fechaInicio2, fechaInicio1);
            }

            return 0;
        });
    }

    private String determinarEstadoEvento(Evento evento, long fechaActual) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date startDate = dateFormat.parse(evento.getFechaInicio());
            Date endDate = (evento.getFechaFinal() != null && !evento.getFechaFinal().isEmpty())
                    ? dateFormat.parse(evento.getFechaFinal())
                    : null;

            if (startDate != null) {
                if (fechaActual < startDate.getTime()) {
                    return "Próximamente";
                }
                if (endDate != null && fechaActual > endDate.getTime()) {
                    return "Finalizado";
                }
                if (endDate == null ||
                        (fechaActual >= startDate.getTime() && fechaActual <= endDate.getTime())) {
                    return "Hoy";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Sin estado";
    }

    private int obtenerPrioridadEstado(String estado) {
        switch (estado) {
            case "Hoy":
                return 1;
            case "Próximamente":
                return 2;
            case "Finalizado":
                return 3;
            default:
                return 4;
        }
    }

    private long convertirFechaALong(String fecha) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = dateFormat.parse(fecha);
            return (date != null) ? date.getTime() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
