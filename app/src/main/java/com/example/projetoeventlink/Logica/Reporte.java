package com.example.projetoeventlink.Logica;

public class Reporte {

    private String ReporteID;
    private String UsuarioID;
    private String OrganizadorUID;
    private String DocumentoOrganizador;
    private String MotivoReporte;

    // Constructor vacío
    public Reporte() {
    }

    public Reporte(String reporteID, String usuarioID, String organizadorUID, String documentoOrganizador, String motivoReporte) {
        ReporteID = reporteID;
        UsuarioID = usuarioID;
        OrganizadorUID = organizadorUID;
        DocumentoOrganizador = documentoOrganizador;
        MotivoReporte = motivoReporte;
    }

    public String getReporteID() {
        return ReporteID;
    }

    public void setReporteID(String reporteID) {
        ReporteID = reporteID;
    }

    public String getUsuarioID() {
        return UsuarioID;
    }

    public void setUsuarioID(String usuarioID) {
        UsuarioID = usuarioID;
    }

    public String getOrganizadorUID() {
        return OrganizadorUID;
    }

    public void setOrganizadorUID(String organizadorUID) {
        OrganizadorUID = organizadorUID;
    }

    public String getDocumentoOrganizador() {
        return DocumentoOrganizador;
    }

    public void setDocumentoOrganizador(String documentoOrganizador) {
        DocumentoOrganizador = documentoOrganizador;
    }

    public String getMotivoReporte() {
        return MotivoReporte;
    }

    public void setMotivoReporte(String motivoReporte) {
        MotivoReporte = motivoReporte;
    }
}
