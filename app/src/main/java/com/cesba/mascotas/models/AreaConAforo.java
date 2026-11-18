package com.cesba.mascotas.models;

public class AreaConAforo {
    private int idArea;
    private String nombre;
    private int capacidadMaxima;
    private String reglasRestricciones;
    private int activa;
    private String horarioApertura;
    private String horarioCierre;
    private int aforoActual;

    public AreaConAforo(int idArea, String nombre, int capacidadMaxima, String reglasRestricciones, int activa, String horarioApertura, String horarioCierre, int aforoActual) {
        this.idArea = idArea;
        this.nombre = nombre;
        this.capacidadMaxima = capacidadMaxima;
        this.reglasRestricciones = reglasRestricciones;
        this.activa = activa;
        this.horarioApertura = horarioApertura;
        this.horarioCierre = horarioCierre;
        this.aforoActual = aforoActual;
    }

    public int getIdArea() { return idArea; }
    public String getNombre() { return nombre; }
    public int getCapacidadMaxima() { return capacidadMaxima; }
    public String getReglasRestricciones() { return reglasRestricciones; }
    public int getActiva() { return activa; }
    public String getHorarioApertura() { return horarioApertura; }
    public String getHorarioCierre() { return horarioCierre; }
    public int getAforoActual() { return aforoActual; }

    @Override
    public String toString() {
        return nombre + " (" + aforoActual + "/" + capacidadMaxima + ")";
    }
}