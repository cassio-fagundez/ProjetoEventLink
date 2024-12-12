package com.example.projetoeventlink.Logica;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetoeventlink.R;
import com.example.projetoeventlink.Telas.EventDetailsActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Evento> eventList;
    private Context context;

    public EventAdapter(Context context, List<Evento> eventList) {
        this.context = context;
        this.eventList = eventList;
    }


    public List<Evento> getFilteredEventsForMap() {
        List<Evento> filteredEvents = new ArrayList<>();
        for (Evento event : eventList) {
            String label = calculateEventLabel(event.getFechaInicio(), event.getFechaFinal());
            if ("Hoy".equals(label) || "Próximamente".equals(label)) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Evento event = eventList.get(position);

        // Setear datos en el item del RecyclerView
        holder.tvEventTitle.setText(event.getTitulo());
        if (!Objects.equals(event.getFechaFinal(), "") || event.getFechaFinal() != null) {
            holder.tvEventDate.setText(String.format("%s - %s", event.getFechaInicio(), event.getFechaFinal()));

        } else {
            holder.tvEventDate.setText(String.format("%s", event.getFechaInicio()));
        }

        if (!Objects.equals(event.getHoraFinal(), "") || event.getHoraFinal() != null) {
            holder.tvEventTime.setText(String.format("%s - %s", event.getHoraInicio(), event.getHoraFinal()));
        } else {
            holder.tvEventTime.setText(String.format("%s", event.getHoraInicio()));
        }

        // Calcular etiqueta según fecha/hora actual
        String label = calculateEventLabel(event.getFechaInicio(), event.getFechaFinal());

        if (label.equals("Hoy")) {
            label = context.getString(R.string.label_hoy);
        } else if (label.equals("Próximamente")) {
            label = context.getString(R.string.label_proximamente);
        } else if (label.equals("Finalizado")) {
            label = context.getString(R.string.label_finalizado);
        } else  label = context.getString(R.string.label_sinestado);

        holder.tvEventLabel.setText(label); // Mostrar etiqueta

        // Listener para el clic en el elemento
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EventDetailsActivity.class);

            // Pasar todos los datos del evento a la nueva Activity
            intent.putExtra("eventId", event.getEventID());
            intent.putExtra("titulo", event.getTitulo());
            intent.putExtra("fechaInicio", event.getFechaInicio());
            intent.putExtra("fechaFinal", event.getFechaFinal());
            intent.putExtra("horaInicio", event.getHoraInicio());
            intent.putExtra("horaFinal", event.getHoraFinal());
            intent.putExtra("descripcion", event.getDescripcion());
            intent.putExtra("direccionPresencial", event.getDireccionPresencial());
            intent.putExtra("urlVirtual", event.getURLVirtual());
            intent.putExtra("categorias", event.getCategorias());
            intent.putExtra("organizadorUID", event.getOrganizadorUID());
            intent.putExtra("latitud", event.getLatitud());
            intent.putExtra("longitud", event.getLongitud());
            //intent.putExtra("imageURL", event.getImageURL());

            v.getContext().startActivity(intent);
        });
    }

    private String calculateEventLabel(String fechaInicio, String fechaFinal) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar today = Calendar.getInstance(); // Fecha y hora actuales

        try {
            // Parsear fecha de inicio
            Date startDate = dateFormat.parse(fechaInicio);
            // Parsear fecha de finalización (puede ser nula)
            Date endDate = (fechaFinal != null && !fechaFinal.isEmpty()) ? dateFormat.parse(fechaFinal) : null;

            if (startDate != null) {
                // Caso 1: Evento en curso (hoy o entre fecha inicio y final)
                if (isSameDay(today.getTime(), startDate) ||
                        (endDate != null && today.getTime().after(startDate) && !today.getTime().after(endDate))) {
                    return "Hoy";
                }
                // Caso 2: Evento futuro
                if (today.getTime().before(startDate)) {
                    return "Próximamente";
                }
                // Caso 3: Evento pasado (finalizado)
                if (endDate != null && today.getTime().after(endDate)) {
                    return "Finalizado";
                }
                if (endDate == null && today.getTime().after(startDate)) {
                    return "Finalizado";
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "Sin estado"; // Si hay un error en la fecha
    }


    // Método auxiliar para verificar si dos fechas son el mismo día
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }


    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventTitle, tvEventDate, tvEventTime, tvEventLabel;

        public EventViewHolder(View itemView) {
            super(itemView);

            // Vincular vistas del item_event.xml
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvEventTime = itemView.findViewById(R.id.tvEventTime);
            tvEventLabel = itemView.findViewById(R.id.tvEventLabel);
        }


    }
}
