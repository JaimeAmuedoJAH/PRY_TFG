package com.jah.pry_rfatm.Modelo;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Usuario implements Serializable {

    private String equipoId;
    private String fotoPerfil;
    private String nombre;
    private String tipoUsuario;

    public Usuario() {}

    public Usuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Usuario(String equipoId, String fotoPerfil, String nombre, String tipoUsuario) {
        this.equipoId = equipoId;
        this.fotoPerfil = fotoPerfil;
        this.nombre = nombre;
        this.tipoUsuario = tipoUsuario;
    }

    public String getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(String equipoId) {
        this.equipoId = equipoId;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    @NonNull
    @Override
    public String toString() {
        return "Usuario{" +
                "equipoId='" + equipoId + '\'' +
                ", fotoPerfil='" + fotoPerfil + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipoUsuario='" + tipoUsuario + '\'' +
                '}';
    }
}
