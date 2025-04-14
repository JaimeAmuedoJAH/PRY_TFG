package com.jah.pry_rfatm.Modelo;

import java.util.Arrays;

public class Grupo {

    private String nombre;
    private String ligaId;
    private String[] equipos;

    public Grupo(){}

    public Grupo(String nombre, String ligaId, String[] equipos) {
        this.nombre = nombre;
        this.ligaId = ligaId;
        this.equipos = equipos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLigaId() {
        return ligaId;
    }

    public void setLigaId(String ligaId) {
        this.ligaId = ligaId;
    }

    public String[] getEquipos() {
        return equipos;
    }

    public void setEquipos(String[] equipos) {
        this.equipos = equipos;
    }

    @Override
    public String toString() {
        return "Grupo{" +
                "nombre='" + nombre + '\'' +
                ", ligaId='" + ligaId + '\'' +
                ", equipos=" + Arrays.toString(equipos) +
                '}';
    }
}
