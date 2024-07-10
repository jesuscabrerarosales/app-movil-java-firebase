package com.example.pc3.model;

public class Persona {
    public String idsensor;
    public String fecha;
    public String hora;
    public String valor;

    public Persona() {
    }

    public String getIdsensor() {
        return idsensor;
    }

    public void setIdsensor(String idsensor) {
        this.idsensor = idsensor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return valor;
    }
}
