package com.jah.pry_rfatm.Modelo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Representa un equipo deportivo con su información básica y estadística.
 */
public class Equipo implements Serializable {

    /** Nombre del equipo */
    private String nombre;
    /** Lista de IDs de los entrenadores del equipo */
    private List<String> entrenadorId;
    /** URL o ruta del escudo del equipo */
    private String escudo;
    /** ID del grupo al que pertenece el equipo */
    private String grupoId;
    /** ID de la liga a la que pertenece el equipo */
    private String ligaId;
    /** Localización geográfica del equipo */
    private String localizacion;
    /** Cantidad de partidos jugados */
    private Integer partidosJugados;
    /** Cantidad de victorias obtenidas */
    private Integer victorias;
    /** Cantidad de derrotas sufridas */
    private Integer derrotas;
    /** Porcentaje de victorias del equipo */
    private Integer porcentajeVictorias;
    /** Lista de IDs de los jugadores titulares */
    private List<String> jugadores;
    /** Lista de IDs de los jugadores suplentes */
    private List<String> suplentes;

    /** Constructor por defecto */
    public Equipo() {}

    /**
     * Constructor con todos los campos.
     *
     * @param nombre Nombre del equipo
     * @param entrenadorId Lista de IDs de entrenadores
     * @param grupoId ID del grupo
     * @param escudo Escudo del equipo
     * @param ligaId ID de la liga
     * @param localizacion Localización del equipo
     * @param partidosJugados Partidos jugados
     * @param victorias Victorias obtenidas
     * @param derrotas Derrotas sufridas
     * @param porcentajeVictorias Porcentaje de victorias
     * @param jugadores Lista de jugadores titulares
     * @param suplentes Lista de suplentes
     */
    public Equipo(String nombre, List<String> entrenadorId, String grupoId, String escudo, String ligaId, String localizacion,
                  Integer partidosJugados, Integer victorias, Integer derrotas, Integer porcentajeVictorias, List<String> jugadores, List<String> suplentes) {
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
        this.suplentes = suplentes;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<String> getEntrenadorId() {
        return entrenadorId;
    }

    public void setEntrenadorId(List<String> entrenadorId) {
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

    public List<String> getSuplentes() {
        return suplentes;
    }

    public void setSuplentes(List<String> suplentes) {
        this.suplentes = suplentes;
    }

    @Override
    public String toString() {
        return "Equipo{" +
                "nombre='" + nombre + '\'' +
                ", entrenadorId=" + entrenadorId +
                ", escudo='" + escudo + '\'' +
                ", grupoId='" + grupoId + '\'' +
                ", ligaId='" + ligaId + '\'' +
                ", localizacion='" + localizacion + '\'' +
                ", partidosJugados=" + partidosJugados +
                ", victorias=" + victorias +
                ", derrotas=" + derrotas +
                ", porcentajeVictorias=" + porcentajeVictorias +
                ", jugadores=" + jugadores +
                ", suplentes=" + suplentes +
                '}';
    }
}
