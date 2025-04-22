package com.jah.pry_rfatm.Modelo;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.Arrays;

public class Grupo implements Serializable {

    private String nombre;
    private DocumentReference ligaId;
    private DocumentReference[] equipos;

    public Grupo(){}

    public Grupo(String nombre, DocumentReference ligaId, DocumentReference[] equipos) {
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

    public DocumentReference getLigaId() {
        return ligaId;
    }

    public void setLigaId(DocumentReference ligaId) {
        this.ligaId = ligaId;
    }

    public DocumentReference[] getEquipos() {
        return equipos;
    }

    public void setEquipos(DocumentReference[] equipos) {
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
