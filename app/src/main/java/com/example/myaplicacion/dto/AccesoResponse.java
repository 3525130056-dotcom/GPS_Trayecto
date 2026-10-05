package com.example.myaplicacion.dto;

public class AccesoResponse {

    private boolean acceso;
    private String mensaje;

    public AccesoResponse(){

    }

    public boolean isAcceso(){
        return acceso;
    }

    public void setAcceso(boolean acceso){
        this.acceso = acceso;
    }

    public String getMensaje(){
        return mensaje;
    }

    public void setMensaje(String mensaje){
        this.mensaje = mensaje;
    }
}
