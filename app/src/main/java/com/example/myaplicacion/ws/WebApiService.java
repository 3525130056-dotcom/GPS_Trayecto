package com.example.myaplicacion.ws;

import com.example.myaplicacion.dto.AccesoResponse;
import com.example.myaplicacion.dto.EliminarResponse;
import com.example.myaplicacion.dto.TrayectoResponse;
import com.example.myaplicacion.dto.coordenadaResponse;
import com.example.myaplicacion.dto.DetalleTrayectoResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WebApiService {

    @GET("guardar_coordenada.php")
    Call<coordenadaResponse> guardarCoordenada(
            @Query("id_usuario") int idUsuario,
            @Query("trayecto") String trayecto,
            @Query("latitud") double latitud,
            @Query("longitud") double longitud
    );

    @POST("login.php")
    Call<AccesoResponse> validaAcceso(
            @Query("user") String username,
            @Query("pass") String password
    );

    @GET("listar_trayectos.php")
    Call<TrayectoResponse> ConsultarTrayectos(
            @Query("id_usuario") int idUsuario
    );

    @GET("obtener_coordenadas.php")
    Call<DetalleTrayectoResponse> getCoordenadas(
            @Query("id_usuario") int idUsuario,
            @Query("trayecto") String trayecto
    );

    @GET("eliminar_trayecto.php")
    Call<EliminarResponse> eliminarTrayecto(
            @Query("id_usuario") int idUsuario,
            @Query("trayecto") String trayecto
    );

}