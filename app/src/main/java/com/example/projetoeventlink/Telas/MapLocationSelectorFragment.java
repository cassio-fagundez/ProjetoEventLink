package com.example.projetoeventlink.Telas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.example.projetoeventlink.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

public class MapLocationSelectorFragment extends DialogFragment {

    private MapView mapView;
    private GeoPoint selectedLocation;
    private final int LOCATION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;


    // Interfaz para devolver la ubicación seleccionada
    public interface OnLocationSelectedListener {
        void onLocationSelected(double latitude, double longitude);
    }

    private OnLocationSelectedListener listener;

    public void setOnLocationSelectedListener(OnLocationSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_location_selector, container, false);

        // Inicializa osmdroid
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        // Configura el MapView
        mapView = view.findViewById(R.id.map);
        mapView.setMultiTouchControls(true);

        // Inicializa el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Verifica permisos y configura la ubicación inicial
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Obtén la ubicación actual del usuario
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    GeoPoint initialPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mapView.getController().setZoom(12.0);
                    mapView.getController().setCenter(initialPoint);
                } else {
                    // Si no se puede obtener la ubicación, usa la predeterminada
                    GeoPoint initialPoint = new GeoPoint(-30.8962053, -55.5378717);
                    mapView.getController().setZoom(12.0);
                    mapView.getController().setCenter(initialPoint);
                }
            });
        } else {
            // Solicita el permiso al usuario
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }


        // Detecta clics en el mapa
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
                // Elimina los marcadores existentes
                for (int i = mapView.getOverlays().size() - 1; i >= 0; i--) {
                    if (mapView.getOverlays().get(i) instanceof Marker) {
                        mapView.getOverlays().remove(i);
                    }
                }

                // Crea y agrega un nuevo marcador
                Marker marker = new Marker(mapView);
                marker.setPosition(geoPoint);
                marker.setTitle("Ubicación seleccionada");
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.eventlink_marker, null));

                mapView.getOverlays().add(marker);

                // Guarda la ubicación seleccionada
                selectedLocation = geoPoint;
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint geoPoint) {
                // Puedes manejar eventos de pulsación larga aquí si lo necesitas
                return false;
            }
        });

        // Agrega el overlay al mapa
        mapView.getOverlays().add(mapEventsOverlay);


        // Configura el botón de confirmar
        Button btnConfirmar = view.findViewById(R.id.btn_confirmar);
        btnConfirmar.setOnClickListener(v -> {
            if (selectedLocation != null && listener != null) {
                listener.onLocationSelected(selectedLocation.getLatitude(), selectedLocation.getLongitude());
            }
            dismiss(); // Cierra el diálogo
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) {
            mapView.onDetach();
        }
    }
}
