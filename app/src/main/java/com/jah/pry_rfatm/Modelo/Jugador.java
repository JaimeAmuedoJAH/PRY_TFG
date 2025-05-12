package com.jah.pry_rfatm.Modelo;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Representa un jugador dentro de un equipo, con sus estadísticas personales.
 */
public class Jugador extends Usuario implements Serializable {

    /** Estilo de juego del jugador */
    private String estilo;
    /** Total de partidos jugados */
    private Integer partidosJugados;
    /** Total de victorias obtenidas */
    private Integer victorias;
    /** Total de derrotas sufridas */
    private Integer derrotas;
    /** Porcentaje de victorias */
    private Integer porcentajeVictorias;

    /** Constructor por defecto */
    public Jugador() {}

    /**
     * Constructor que define el tipo de usuario.
     *
     * @param tipoUsuario Tipo de usuario (por ejemplo: "jugador")
     */
    public Jugador(String tipoUsuario) {
        super(tipoUsuario);
    }

    /**
     * Constructor completo.
     *
     * @param equipoId ID del equipo
     * @param fotoPerfil URL de la foto de perfil
     * @param nombre Nombre del jugador
     * @param tipoUsuario Tipo de usuario
     * @param estilo Estilo de juego
     * @param partidosJugados Partidos jugados
     * @param victorias Victorias
     * @param derrotas Derrotas
     * @param porcentajeVictorias Porcentaje de victorias
     */
    public Jugador(String equipoId, String fotoPerfil, String nombre, String tipoUsuario,
                   String estilo, Integer partidosJugados, Integer victorias,
                   Integer derrotas, Integer porcentajeVictorias) {
        super(equipoId, fotoPerfil, nombre, tipoUsuario);
        this.estilo = estilo;
        this.partidosJugados = partidosJugados;
        this.victorias = victorias;
        this.derrotas = derrotas;
        this.porcentajeVictorias = porcentajeVictorias;
    }

    public String getEstilo() {
        return estilo;
    }

    public void setEstilo(String estilo) {
        this.estilo = estilo;
    }

    public Integer getPartidosJugados() {
        return partidosJugados;
    }

    public void setPartidosJugados(Integer partidosJugados) {
        this.partidosJugados = partidosJugados;
    }

    public Integer getVictorias() {
        return victorias;
    }

    public void setVictorias(Integer victorias) {
        this.victorias = victorias;
    }

    public Integer getDerrotas() {
        return derrotas;
    }

    public void setDerrotas(Integer derrotas) {
        this.derrotas = derrotas;
    }

    public Integer getPorcentajeVictorias() {
        return porcentajeVictorias;
    }

    public void setPorcentajeVictorias(Integer porcentajeVictorias) {
        this.porcentajeVictorias = porcentajeVictorias;
    }

    @NonNull
    @Override
    public String toString() {
        return "Jugador{" +
                "estilo='" + estilo + '\'' +
                ", partidosJugados=" + partidosJugados +
                ", victorias=" + victorias +
                ", derrotas=" + derrotas +
                ", porcentajeVictorias=" + porcentajeVictorias +
                '}';
    }
}
