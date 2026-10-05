package com.example.myaplicacion.ws;

import com.example.myaplicacion.dto.AccesoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface WebApiService {
    @GET("login.php")
    Call<AccesoResponse> validaAcceso(
            @Query("user") String username,
            @Query("pass") String password
    );
}
