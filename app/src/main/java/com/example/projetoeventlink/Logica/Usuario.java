package com.example.projetoeventlink.Logica;

public class Usuario {

    private String Nombres;
    private String Apellidos;
    private String Genero;
    private String FechaNacimiento;
    private int Documento;

    public Usuario(String nombres, String apellidos, String genero, String fechaNacimiento) {
        this.Nombres = nombres;
        this.Apellidos = apellidos;
        this.Genero = genero;
        this.FechaNacimiento = fechaNacimiento;
    }

    public String getNombres() {
        return Nombres;
    }

    public void setNombres(String nombres) {
        Nombres = nombres;
    }

    public String getApellidos() {
        return Apellidos;
    }

    public void setApellidos(String apellidos) {
        Apellidos = apellidos;
    }

    public String getGenero() {
        return Genero;
    }

    public void setGenero(String genero) {
        Genero = genero;
    }

    public String getFechaNacimiento() {
        return FechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        FechaNacimiento = fechaNacimiento;
    }

    public int getDocumento() {
        return Documento;
    }

    public void setDocumento(int documento) {
        Documento = documento;
    }
}
