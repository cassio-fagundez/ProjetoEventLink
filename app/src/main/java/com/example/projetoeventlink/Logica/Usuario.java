package com.example.projetoeventlink.Logica;

public class Usuario {

    private String Nombre1;
    private String Nombre2;
    private String Apellido1;
    private String Apellido2;
    private String Genero;
    private String FechaNacimiento;
    private String Documento;

    public Usuario(String nombre1, String nombre2, String apellido1, String apellido2, String genero, String fechaNacimiento) {
        Nombre1 = nombre1;

        if (nombre2==""){
            Nombre2=null;
        } else {
            Nombre2 = nombre2;
        }

        Apellido1 = apellido1;

        if (apellido2==""){
            Apellido2=null;
        } else {
            Apellido2 = apellido2;
        }

        Genero = genero;
        FechaNacimiento = fechaNacimiento;
    }

    public String getNombre1() {
        return Nombre1;
    }

    public void setNombre1(String nombre1) {
        Nombre1 = nombre1;
    }

    public String getNombre2() {
        return Nombre2;
    }

    public void setNombre2(String nombre2) {
        Nombre2 = nombre2;
    }

    public String getApellido1() {
        return Apellido1;
    }

    public void setApellido1(String apellido1) {
        Apellido1 = apellido1;
    }

    public String getApellido2() {
        return Apellido2;
    }

    public void setApellido2(String apellido2) {
        Apellido2 = apellido2;
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

    public String getDocumento() {
        return Documento;
    }

    public void setDocumento(String documento) {
        Documento = documento;
    }
}
