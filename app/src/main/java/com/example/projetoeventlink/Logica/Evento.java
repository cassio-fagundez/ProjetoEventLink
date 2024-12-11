package com.example.projetoeventlink.Logica;

public class Evento {

    private String EventID;
    private String Titulo;
    private String Descripcion;
    private String DireccionPresencial;
    private String URLVirtual;
    private String ImageURL;
    private String FechaInicio;
    private String FechaFinal = "";
    private String HoraInicio;
    private String HoraFinal = "";
    private String Categorias;
    private String OrganizadorUID;
    private double Latitud;
    private double Longitud;

    // Constructor vacío
    public Evento() {
    }

    public Evento(String eventID, String titulo, String descripcion, String direccionPresencial, String URLVirtual, String fechaInicio, String fechaFinal, String horaInicio, String horaFinal, String categorias, String organizadorUID, double latitud, double longitud) {
        EventID = eventID;
        Titulo = titulo;
        Descripcion = descripcion;
        DireccionPresencial = direccionPresencial;
        this.URLVirtual = URLVirtual;
        FechaInicio = fechaInicio;
        FechaFinal = fechaFinal;
        HoraInicio = horaInicio;
        HoraFinal = horaFinal;
        Categorias = categorias;
        OrganizadorUID = organizadorUID;
        Latitud = latitud;
        Longitud = longitud;
    }

    public String getEventID() {
        return EventID;
    }

    public void setEventID(String eventID) {
        EventID = eventID;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getDireccionPresencial() {
        return DireccionPresencial;
    }

    public void setDireccionPresencial(String direccionPresencial) {
        DireccionPresencial = direccionPresencial;
    }

    public String getURLVirtual() {
        return URLVirtual;
    }

    public void setURLVirtual(String URLVirtual) {
        this.URLVirtual = URLVirtual;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getFechaInicio() {
        return FechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        FechaInicio = fechaInicio;
    }

    public String getFechaFinal() {
        return FechaFinal;
    }

    public void setFechaFinal(String fechaFinal) {
        FechaFinal = fechaFinal;
    }

    public String getHoraInicio() {
        return HoraInicio;
    }

    public void setHoraInicio(String horaInicio) {
        HoraInicio = horaInicio;
    }

    public String getHoraFinal() {
        return HoraFinal;
    }

    public void setHoraFinal(String horaFinal) {
        HoraFinal = horaFinal;
    }

    public String getCategorias() {
        return Categorias;
    }

    public void setCategorias(String categorias) {
        Categorias = categorias;
    }

    public String getOrganizadorUID() {
        return OrganizadorUID;
    }

    public void setOrganizadorUID(String organizadorUID) {
        OrganizadorUID = organizadorUID;
    }

    public double getLatitud() {
        return Latitud;
    }

    public void setLatitud(double latitud) {
        Latitud = latitud;
    }

    public double getLongitud() {
        return Longitud;
    }

    public void setLongitud(double longitud) {
        Longitud = longitud;
    }
}
