package com.example.myaplicacion.dto;

import com.google.gson.annotations.SerializedName;

public class coordenadaResponse {
    @SerializedName("trayecto_id")
    private String trayectoId;
    @SerializedName("guardado")
    private boolean guardado;
    @SerializedName("mensaje")
    private String mensaje;

    public boolean isGuardado(){
        return guardado;
    }

    public String getMensaje(){
        return mensaje;
    }

    public String getTrayectoId(){
        return trayectoId;
    }
}
