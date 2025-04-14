package com.jah.pry_rfatm.Modelo;

import java.util.Arrays;

public class Liga {

    private String nombre;
    private String[] grupos;

    public Liga(){}

    public Liga(String nombre, String[] grupos) {
        this.nombre = nombre;
        this.grupos = grupos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String[] getGrupos() {
        return grupos;
    }

    public void setGrupos(String[] grupos) {
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
