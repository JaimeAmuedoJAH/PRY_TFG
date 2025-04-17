package com.jah.pry_rfatm.Modelo;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;

public class Usuario {

    private DocumentReference equipoId;
    private String fotoPerfil;
    private String nombre;
    private String tipoUsuario;

    public Usuario() {}

    public Usuario(DocumentReference equipoId, String fotoPerfil, String nombre, String tipoUsuario) {
        this.equipoId = equipoId;
        this.fotoPerfil = fotoPerfil;
        this.nombre = nombre;
        this.tipoUsuario = tipoUsuario;
    }

    public DocumentReference getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(DocumentReference equipoId) {
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
