package com.jah.pry_rfatm.Modelo;

import java.io.Serializable;

public class Entrenador extends Usuario implements Serializable {

    public Entrenador() {
    }

    public Entrenador(String equipoId, String fotoPerfil, String nombre, String tipoUsuario) {
        super(equipoId, fotoPerfil, nombre, tipoUsuario);
    }
}
