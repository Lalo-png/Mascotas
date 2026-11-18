package com.cesba.mascotas.models;

public class Mascota {
    private int idMascota;
    private int idDueno;
    private String nombre;
    private String especie;
    private String raza;
    private double pesoKg;
    private String tamano;
    private String temperamento;
    private int vacunasAlDia;
    private String notasMedicas;

    public Mascota(int idMascota, int idDueno, String nombre, String especie, String raza, double pesoKg, String tamano, String temperamento, int vacunasAlDia, String notasMedicas) {
        this.idMascota = idMascota;
        this.idDueno = idDueno;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.pesoKg = pesoKg;
        this.tamano = tamano;
        this.temperamento = temperamento;
        this.vacunasAlDia = vacunasAlDia;
        this.notasMedicas = notasMedicas;
    }

    public Mascota(int idDueno, String nombre, String especie, String raza, double pesoKg, String tamano, String temperamento, int vacunasAlDia, String notasMedicas) {
        this.idDueno = idDueno;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.pesoKg = pesoKg;
        this.tamano = tamano;
        this.temperamento = temperamento;
        this.vacunasAlDia = vacunasAlDia;
        this.notasMedicas = notasMedicas;
    }

    public int getIdMascota() { return idMascota; }
    public int getIdDueno() { return idDueno; }
    public String getNombre() { return nombre; }
    public String getEspecie() { return especie; }
    public String getRaza() { return raza; }
    public double getPesoKg() { return pesoKg; }
    public String getTamano() { return tamano; }
    public String getTemperamento() { return temperamento; }
    public int getVacunasAlDia() { return vacunasAlDia; }
    public String getNotasMedicas() { return notasMedicas; }
}
