package com.jah.pry_rfatm.Modelo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Grupo implements Serializable {

    private String nombre;
    private String ligaId;
    private List<String> equipos;

    public Grupo(){}

    public Grupo(String nombre, String ligaId, List<String> equipos) {
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

    public List<String> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<String> equipos) {
        this.equipos = equipos;
    }

    @Override
    public String toString() {
        return "Grupo{" +
                "nombre='" + nombre + '\'' +
                ", ligaId='" + ligaId + '\'' +
                ", equipos=" + equipos +
                '}';
    }
}
