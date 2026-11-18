package com.cesba.mascotas.models;

public class Dueno {
    private int idDueno;
    private String nombre;
    private String telefono;
    private String email;
    private String contactoEmergencia;
    private String fechaRegistro;

    public Dueno(int idDueno, String nombre, String telefono, String email, String contactoEmergencia, String fechaRegistro) {
        this.idDueno = idDueno;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.contactoEmergencia = contactoEmergencia;
        this.fechaRegistro = fechaRegistro;
    }

    public Dueno(String nombre, String telefono, String email, String contactoEmergencia) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.contactoEmergencia = contactoEmergencia;
    }

    public int getIdDueno() { return idDueno; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public String getContactoEmergencia() { return contactoEmergencia; }
    public String getFechaRegistro() { return fechaRegistro; }

    @Override
    public String toString() {
        return nombre + " (" + telefono + ")";
    }
}