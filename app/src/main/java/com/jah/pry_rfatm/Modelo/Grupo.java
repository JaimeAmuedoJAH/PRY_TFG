package com.jah.pry_rfatm.Modelo;

import java.io.Serializable;
import java.util.List;

/**
 * Representa un grupo dentro de una liga, compuesto por varios equipos.
 */
public class Grupo implements Serializable {

    /** Nombre del grupo */
    private String nombre;
    /** ID de la liga a la que pertenece el grupo */
    private String ligaId;
    /** Lista de IDs de equipos que conforman el grupo */
    private List<String> equipos;

    /** Constructor por defecto */
    public Grupo() {}

    /**
     * Constructor con parámetros.
     *
     * @param nombre Nombre del grupo
     * @param ligaId ID de la liga
     * @param equipos Lista de IDs de los equipos
     */
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
