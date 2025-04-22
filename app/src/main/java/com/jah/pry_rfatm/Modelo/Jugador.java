package com.jah.pry_rfatm.Modelo;

import java.io.Serializable;

public class Jugador extends Usuario implements Serializable {

    private String estilo;
    private Integer partidosJugados;
    private Integer victorias;
    private Integer derrotas;
    private Integer porcentajeVictorias;

    public Jugador() {}

    public Jugador(String equipoId, String fotoPerfil, String nombre, String tipoUsuario, String estilo, Integer partidosJugados, Integer victorias, Integer derrotas, Integer porcentajeVictorias) {
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
