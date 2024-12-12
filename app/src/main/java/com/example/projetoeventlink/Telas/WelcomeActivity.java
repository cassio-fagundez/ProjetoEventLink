package com.example.projetoeventlink.Telas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetoeventlink.Logica.EventAdapter;
import com.example.projetoeventlink.Logica.Evento;
import com.example.projetoeventlink.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {


    private BottomAppBar bottomAppBar;
    private FloatingActionButton floatingActionButton;
    private LinearLayout LoginRegisterButtons;
    private Button btnForLogin, btnForRegister;
    private TextView tvNome;
    private FirebaseAuth authProfile;
    private DatabaseReference referenceProfile;
    private BottomNavigationView bnView;
    private FloatingActionButton faButton;

    final Marker[] lastSelectedMarker = {null};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        tvNome = findViewById(R.id.tvNome);
        LoginRegisterButtons = findViewById(R.id.LogInRegisterButtons);
        btnForLogin = findViewById(R.id.btnForLogin);
        btnForRegister = findViewById(R.id.btnForRegister);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            LoginRegisterButtons.setVisibility(View.VISIBLE);
        } else {


            referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users").child(firebaseUser.getUid());

            referenceProfile.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        bottomAppBar.setVisibility(View.VISIBLE);
                        floatingActionButton.setVisibility(View.VISIBLE);

                        String Nome = snapshot.child("nombre1").getValue(String.class);
                        if (Nome != null && !Nome.isEmpty()) {
                            String[] nameParts = Nome.split(" ");
                            tvNome.setVisibility(View.VISIBLE);
                            tvNome.setText(nameParts[0]);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(WelcomeActivity.this, getString(R.string.error_login_failed_password), Toast.LENGTH_SHORT).show();
                }
            });

        }

        btnForLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, UserLoginActivity.class));
            }
        });

        btnForRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, UserRegisterActivity.class));
            }
        });

        // Configuración de los ítems del Bottom Navigation Bar.

        bnView = findViewById(R.id.bottomNavigationView);
        faButton = findViewById(R.id.floatingActionButton);

        bnView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.events_list) {
                    Intent intent = new Intent(getApplicationContext(), EventListActivity.class);
                    startActivity(intent);

                } else if (item.getItemId() == R.id.menu_refresh) {
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);

                } else if (item.getItemId() == R.id.menu_settings) {
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);

                } else if (item.getItemId() == R.id.menu_logout) {
                    authProfile.signOut();
                    Toast.makeText(WelcomeActivity.this, getString(R.string.toast_sucessful_logout), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    // Flags para que el usuario no pueda regresar y loguearse.
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                return true;
            }
        });

        faButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();

                    // Referencia a la base de datos donde se almacenan los datos del usuario
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(userId);

                    // Verificar si el campo 'documento' está registrado
                    userRef.child("documento").get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Obtener el valor del documento
                            String documento = task.getResult().getValue(String.class);

                            if (documento != null && !documento.equals("0")) {
                                Intent intent = new Intent(getApplicationContext(), EventRegisterActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.document_required), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_verifying_document), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.login_required), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // MAPA

// Configuración de osmdroid
        Configuration.getInstance().setUserAgentValue(getPackageName());
// Inicializar el mapa
        MapView mapView = findViewById(R.id.mapView);
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

// Configurar la ubicación inicial del mapa
        GeoPoint initialPoint = new GeoPoint(-30.8962053, -55.5378717);
        mapView.getController().setZoom(13.0);
        mapView.getController().setCenter(initialPoint);

// Referencia a la base de datos de eventos
        DatabaseReference eventosRef = FirebaseDatabase.getInstance().getReference("Events");

// Lista para almacenar los eventos
        eventosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Evento> listaDeEventos = new ArrayList<>();

                // Recuperar los datos de los eventos
                for (DataSnapshot data : snapshot.getChildren()) {
                    Evento evento = data.getValue(Evento.class);
                    if (evento != null) {
                        listaDeEventos.add(evento);
                    }
                }

                // Configurar el adaptador y filtrar los eventos
                EventAdapter eventAdapter = new EventAdapter(getApplicationContext(), listaDeEventos);
                List<Evento> filteredEvents = eventAdapter.getFilteredEventsForMap();

                // Añadir los marcadores al mapa
                for (Evento event : filteredEvents) {
                    if (event.getLatitud() != 0.0 && event.getLongitud() != 0.0) {
                        GeoPoint eventLocation = new GeoPoint(event.getLatitud(), event.getLongitud());
                        Marker marker = new Marker(mapView);
                        marker.setPosition(eventLocation);
                        marker.setTitle(event.getTitulo());

                        marker.setSnippet(event.getDescripcion());

                        String datosEvento = getString(R.string.event_date);
                        if(event.getFechaFinal() != null && !event.getFechaFinal().isEmpty()){
                            datosEvento = datosEvento + event.getFechaInicio()+ " - " + event.getFechaFinal();
                        } else { datosEvento = datosEvento + event.getFechaInicio(); }

                        if(event.getHoraFinal() != null && !event.getHoraFinal().isEmpty()){
                            datosEvento = datosEvento + "\n" + getString(R.string.event_time) + event.getHoraInicio() + " - " + event.getHoraFinal();
                        } else {
                            datosEvento = datosEvento + "\n" + getString(R.string.event_time) + event.getHoraInicio();
                        }

                        marker.setSubDescription(datosEvento);

                        marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.eventlink_marker, null));


                        // Configurar el listener para ocultar el info window
                        marker.setOnMarkerClickListener((clickedMarker, mapView) -> {
                            if (lastSelectedMarker[0] != null && lastSelectedMarker[0] == clickedMarker) {
                                // Si el marcador tocado es el mismo, ocultar su info window
                                clickedMarker.closeInfoWindow();
                                lastSelectedMarker[0] = null;
                            } else {
                                // Mostrar el info window y registrar este marcador como el seleccionado
                                clickedMarker.showInfoWindow();
                                lastSelectedMarker[0] = clickedMarker;
                            }
                            return true;
                        });

                        mapView.getOverlays().add(marker);
                    }
                }

                // Agregar un listener para ocultar el info window al tocar el mapa
                mapView.setOnClickListener(v -> {
                    if (lastSelectedMarker[0] != null) {
                        lastSelectedMarker[0].closeInfoWindow();
                        lastSelectedMarker[0] = null;
                    }
                });

                // Refrescar el mapa
                mapView.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WelcomeActivity.this, getString(R.string.error_loading_events) + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}