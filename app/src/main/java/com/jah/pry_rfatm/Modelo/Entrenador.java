package com.jah.pry_rfatm.Modelo;

import com.google.firebase.firestore.DocumentReference;

public class Entrenador extends Usuario{

    public Entrenador() {
    }

    public Entrenador(String equipoId, String fotoPerfil, String nombre, String tipoUsuario) {
        super(equipoId, fotoPerfil, nombre, tipoUsuario);
    }
}
