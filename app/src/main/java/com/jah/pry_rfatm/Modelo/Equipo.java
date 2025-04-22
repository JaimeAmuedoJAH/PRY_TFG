package com.jah.pry_rfatm.Modelo;

import java.io.Serializable;
import java.util.List;

public class Equipo implements Serializable {

    private String nombre;
    private String entrenadorId;
    private String escudo;
    private String grupoId;
    private String ligaId;
    private String localizacion;
    private Integer partidosJugados;
    private Integer victorias;
    private Integer derrotas;
    private Integer porcentajeVictorias;
    private List<String> jugadores;

    public Equipo(){}

    public Equipo(String nombre, String entrenadorId, String grupoId, String escudo, String ligaId, String localizacion,
                  Integer partidosJugados, Integer victorias, Integer derrotas, Integer porcentajeVictorias, List<String> jugadores) {
        this.nombre = nombre;
        this.entrenadorId = entrenadorId;
        this.grupoId = grupoId;
        this.escudo = escudo;
        this.ligaId = ligaId;
        this.localizacion = localizacion;
        this.partidosJugados = partidosJugados;
        this.victorias = victorias;
        this.derrotas = derrotas;
        this.porcentajeVictorias = porcentajeVictorias;
        this.jugadores = jugadores;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEntrenadorId() {
        return entrenadorId;
    }

    public void setEntrenadorId(String entrenadorId) {
        this.entrenadorId = entrenadorId;
    }

    public String getEscudo() {
        return escudo;
    }

    public void setEscudo(String escudo) {
        this.escudo = escudo;
    }

    public String getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(String grupoId) {
        this.grupoId = grupoId;
    }

    public String getLigaId() {
        return ligaId;
    }

    public void setLigaId(String ligaId) {
        this.ligaId = ligaId;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
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

    public List<String> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<String> jugadores) {
        this.jugadores = jugadores;
    }

    @Override
    public String toString() {
        return "Equipo{" +
                "nombre='" + nombre + '\'' +
                ", entrenadorId='" + entrenadorId + '\'' +
                ", escudo='" + escudo + '\'' +
                ", grupoId='" + grupoId + '\'' +
                ", ligaId='" + ligaId + '\'' +
                ", localización='" + localizacion + '\'' +
                ", partidosJugados=" + partidosJugados +
                ", victorias=" + victorias +
                ", derrotas=" + derrotas +
                ", porcentajeVictorias=" + porcentajeVictorias +
                ", jugadores=" + jugadores +
                '}';
    }
}
