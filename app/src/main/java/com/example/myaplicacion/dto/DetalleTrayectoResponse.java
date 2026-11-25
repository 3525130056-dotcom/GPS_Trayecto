package com.example.myaplicacion.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DetalleTrayectoResponse {

    @SerializedName("encontrado")
    private boolean encontrado;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("coordenadas")
    private List<coordenada> coordenadas;

    public boolean isEncontrado() {
        return encontrado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public List<coordenada> getCoordenadas() {
        return coordenadas;
    }
}