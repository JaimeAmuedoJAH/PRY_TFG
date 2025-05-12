package com.jah.pry_rfatm.Modelo;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Representa una liga deportiva, que puede estar compuesta por varios grupos.
 */
public class Liga implements Serializable {

    /** Nombre de la liga */
    private String nombre;
    /** Referencias a los documentos de grupos en Firestore */
    private DocumentReference[] grupos;

    /** Constructor por defecto */
    public Liga() {}

    /**
     * Constructor con parámetros.
     *
     * @param nombre Nombre de la liga
     * @param grupos Arreglo de referencias a grupos
     */
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
