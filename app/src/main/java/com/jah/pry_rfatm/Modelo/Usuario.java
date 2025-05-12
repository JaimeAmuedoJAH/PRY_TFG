package com.jah.pry_rfatm.Modelo;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Representa un usuario del sistema, que puede tener distintos roles (jugador, entrenador, etc.).
 */
public class Usuario implements Serializable {

    /** ID del equipo al que pertenece el usuario */
    private String equipoId;
    /** URL o ruta de la foto de perfil del usuario */
    private String fotoPerfil;
    /** Nombre del usuario */
    private String nombre;
    /** Tipo de usuario (por ejemplo: jugador, entrenador) */
    private String tipoUsuario;

    /** Constructor por defecto */
    public Usuario() {}

    /**
     * Constructor con tipo de usuario.
     *
     * @param tipoUsuario Tipo del usuario
     */
    public Usuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    /**
     * Constructor completo.
     *
     * @param equipoId ID del equipo
     * @param fotoPerfil Foto de perfil
     * @param nombre Nombre del usuario
     * @param tipoUsuario Tipo de usuario
     */
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
