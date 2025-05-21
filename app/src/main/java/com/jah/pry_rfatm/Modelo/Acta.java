package com.jah.pry_rfatm.Modelo;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

public class Acta {

    private String partidoId;
    private String resultadoFinal;
    private String setsEquipoABC;
    private String setsEquipoXYZ;
    private Map<String, List<String>> partidoAY;
    private Map<String, List<String>> partidoBX;
    private Map<String, List<String>> partidoCZ;
    private Map<String, List<String>> partidoAX;
    private Map<String, List<String>> partidoCY;
    private Map<String, List<String>> partidoBZ;
    private Map<String, List<String>> partidoAX2;

    public Acta(){}

    public Acta(String partidoId, String resultadoFinal, String setsEquipoABC, String setsEquipoXYZ, Map<String, List<String>> partidoAX2, Map<String, List<String>> partidoBZ,
                Map<String, List<String>> partidoCY, Map<String, List<String>> partidoAX, Map<String, List<String>> partidoCZ, Map<String, List<String>> partidoBX,
                Map<String, List<String>> partidoAY) {
        this.partidoId = partidoId;
        this.resultadoFinal = resultadoFinal;
        this.setsEquipoABC = setsEquipoABC;
        this.setsEquipoXYZ = setsEquipoXYZ;
        this.partidoAX2 = partidoAX2;
        this.partidoBZ = partidoBZ;
        this.partidoCY = partidoCY;
        this.partidoAX = partidoAX;
        this.partidoCZ = partidoCZ;
        this.partidoBX = partidoBX;
        this.partidoAY = partidoAY;
    }

    public String getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(String partidoId) {
        this.partidoId = partidoId;
    }

    public String getResultadoFinal() {
        return resultadoFinal;
    }

    public void setResultadoFinal(String resultadoFinal) {
        this.resultadoFinal = resultadoFinal;
    }

    public String getSetsEquipoABC() {
        return setsEquipoABC;
    }

    public void setSetsEquipoABC(String setsEquipoABC) {
        this.setsEquipoABC = setsEquipoABC;
    }

    public String getSetsEquipoXYZ() {
        return setsEquipoXYZ;
    }

    public void setSetsEquipoXYZ(String setsEquipoXYZ) {
        this.setsEquipoXYZ = setsEquipoXYZ;
    }

    public Map<String, List<String>> getPartidoAY() {
        return partidoAY;
    }

    public void setPartidoAY(Map<String, List<String>> partidoAY) {
        this.partidoAY = partidoAY;
    }

    public Map<String, List<String>> getPartidoBX() {
        return partidoBX;
    }

    public void setPartidoBX(Map<String, List<String>> partidoBX) {
        this.partidoBX = partidoBX;
    }

    public Map<String, List<String>> getPartidoCZ() {
        return partidoCZ;
    }

    public void setPartidoCZ(Map<String, List<String>> partidoCZ) {
        this.partidoCZ = partidoCZ;
    }

    public Map<String, List<String>> getPartidoAX() {
        return partidoAX;
    }

    public void setPartidoAX(Map<String, List<String>> partidoAX) {
        this.partidoAX = partidoAX;
    }

    public Map<String, List<String>> getPartidoCY() {
        return partidoCY;
    }

    public void setPartidoCY(Map<String, List<String>> partidoCY) {
        this.partidoCY = partidoCY;
    }

    public Map<String, List<String>> getPartidoBZ() {
        return partidoBZ;
    }

    public void setPartidoBZ(Map<String, List<String>> partidoBZ) {
        this.partidoBZ = partidoBZ;
    }

    public Map<String, List<String>> getPartidoAX2() {
        return partidoAX2;
    }

    public void setPartidoAX2(Map<String,List<String>> partidoAX2) {
        this.partidoAX2 = partidoAX2;
    }

    @NonNull
    @Override
    public String toString() {
        return "Acta{" +
                ", resultadoFinal='" + resultadoFinal + '\'' +
                ", setsEquipoABC='" + setsEquipoABC + '\'' +
                ", setsEquipoXYZ='" + setsEquipoXYZ + '\'' +
                ", partidoAY=" + partidoAY +
                ", partidoBX=" + partidoBX +
                ", partidoCZ=" + partidoCZ +
                ", partidoAX=" + partidoAX +
                ", partidoCY=" + partidoCY +
                ", partidoBZ=" + partidoBZ +
                ", partidoAX2=" + partidoAX2 +
                '}';
    }
}
