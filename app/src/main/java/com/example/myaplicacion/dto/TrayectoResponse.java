package com.example.myaplicacion.dto;

import java.util.List;

public class TrayectoResponse {

    private boolean encontrado;
    private int total;
    List<Trayecto>trayectos;

    public TrayectoResponse(){
    }

    public TrayectoResponse(boolean encontrado) {
        this.encontrado = encontrado;
    }

    public boolean isEncontrado() {
        return encontrado;
    }

    public void setEncontrado(boolean encontrado) {
        this.encontrado = encontrado;
    }

    public int getTotal() {
        return total;
    }

    public List<Trayecto> getTrayectos() {
        return trayectos;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
