package com.jah.pry_rfatm.Modelo;

import java.io.Serializable;

/**
 * Representa un entrenador en el sistema.
 * Hereda de la clase {@link Usuario} y contiene la misma información,
 * ya que los entrenadores comparten la estructura básica de usuario.
 */
public class Entrenador extends Usuario implements Serializable {

    /**
     * Constructor vacío necesario para Firebase o deserialización automática.
     */
    public Entrenador() {
    }

    /**
     * Constructor que inicializa solo el tipo de usuario.
     *
     * @param tipoUsuario Tipo de usuario, normalmente debe ser "entrenador".
     */
    public Entrenador(String tipoUsuario) {
        super(tipoUsuario);
    }

    /**
     * Constructor completo que inicializa todos los campos del entrenador.
     *
     * @param equipoId     ID del equipo al que pertenece.
     * @param fotoPerfil   URL de la foto de perfil.
     * @param nombre       Nombre del entrenador.
     * @param tipoUsuario  Tipo de usuario, normalmente "entrenador".
     */
    public Entrenador(String equipoId, String fotoPerfil, String nombre, String tipoUsuario) {
        super(equipoId, fotoPerfil, nombre, tipoUsuario);
    }
}
