package com.jah.pry_rfatm.Modelo;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.Arrays;

public class Liga implements Serializable {

    private String nombre;
    private DocumentReference[] grupos;

    public Liga(){}

    public Liga(String nombre, DocumentReference[] grupos) {
        this.nombre = nombre;
        this.grupos = grupos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public DocumentReference[] getGrupos() {
        return grupos;
    }

    public void setGrupos(DocumentReference[] grupos) {
        this.grupos = grupos;
    }

    @Override
    public String toString() {
        return "Liga{" +
                "nombre='" + nombre + '\'' +
                ", grupos=" + Arrays.toString(grupos) +
                '}';
    }
}
