package com.jah.pry_rfatm.Modelo;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.Date;

public class Partido implements Serializable {

    private String equipoLocalId;
    private String equipoVisitanteId;
    private String estado;
    private Date fecha;
    private String grupoId;
    private String resultado;
    private Integer setsGanados;
    private Integer setsPerdidos;

    public Partido(){}

    public Partido(String equipoLocalId, String equipovVisitanteId, String estado, Date fecha, String grupoId, String resultado,
                   Integer setsGanados, Integer setsPerdidos) {
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
