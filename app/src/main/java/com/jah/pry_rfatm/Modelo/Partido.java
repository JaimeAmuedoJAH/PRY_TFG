package com.jah.pry_rfatm.Modelo;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.Date;

/**
 * Representa un partido disputado entre dos equipos.
 */
public class Partido implements Serializable {

    /** ID del equipo local */
    private String equipoLocalId;
    /** ID del equipo visitante */
    private String equipoVisitanteId;
    /** Estado del partido (ej. programado, finalizado) */
    private String estado;
    /** Fecha del partido */
    private Date fecha;
    /** ID del grupo en el que se disputa el partido */
    private String grupoId;
    /** Resultado del partido (puede ser una cadena con formato libre) */
    private String resultado;
    /** Número de sets ganados por el equipo principal */
    private Integer setsGanados;
    /** Número de sets perdidos por el equipo principal */
    private Integer setsPerdidos;

    /** Constructor por defecto */
    public Partido() {}

    /**
     * Constructor con todos los campos del partido.
     *
     * @param equipoLocalId ID del equipo local
     * @param equipovVisitanteId ID del equipo visitante
     * @param estado Estado del partido
     * @param fecha Fecha del partido
     * @param grupoId ID del grupo
     * @param resultado Resultado final del partido
     * @param setsGanados Sets ganados
     * @param setsPerdidos Sets perdidos
     */
    public Partido(String equipoLocalId, String equipovVisitanteId, String estado, Date fecha,
                   String grupoId, String resultado, Integer setsGanados, Integer setsPerdidos) {
        this.equipoLocalId = equipoLocalId;
        this.equipoVisitanteId = equipovVisitanteId;
        this.estado = estado;
        this.fecha = fecha;
        this.grupoId = grupoId;
        this.resultado = resultado;
        this.setsGanados = setsGanados;
        this.setsPerdidos = setsPerdidos;
    }

    public String getEquipoLocalId() {
        return equipoLocalId;
    }

    public void setEquipoLocalId(String equipoLocalId) {
        this.equipoLocalId = equipoLocalId;
    }

    public String getEquipoVisitanteId() {
        return equipoVisitanteId;
    }

    public void setEquipoVisitanteId(String equipoVisitanteId) {
        this.equipoVisitanteId = equipoVisitanteId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(String grupoId) {
        this.grupoId = grupoId;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public Integer getSetsGanados() {
        return setsGanados;
    }

    public void setSetsGanados(Integer setsGanados) {
        this.setsGanados = setsGanados;
    }

    public Integer getSetsPerdidos() {
        return setsPerdidos;
    }

    public void setSetsPerdidos(Integer setsPerdidos) {
        this.setsPerdidos = setsPerdidos;
    }

    @Override
    public String toString() {
        return "Partido{" +
                "equipoLocalId='" + equipoLocalId + '\'' +
                ", equipovVisitanteId='" + equipoVisitanteId + '\'' +
                ", estado='" + estado + '\'' +
                ", fecha='" + fecha + '\'' +
                ", grupoId='" + grupoId + '\'' +
                ", resultado='" + resultado + '\'' +
                ", setsGanados=" + setsGanados +
                ", setsPerdidos=" + setsPerdidos +
                '}';
    }
}
