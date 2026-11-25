package com.example.myaplicacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myaplicacion.dto.AccesoResponse;
import com.example.myaplicacion.ws.WebApiAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements Callback<AccesoResponse> {

    private EditText valorUser;
    private EditText valorPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        valorUser = findViewById(R.id.CampoUser);
        valorPassword = findViewById(R.id.campoPass);
    }

    public void validaAcceso(View v) {
        String usuario = valorUser.getText().toString().trim();
        String password = valorPassword.getText().toString().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<AccesoResponse> call = WebApiAdapter.getApiService().validaAcceso(usuario, password);
        call.enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<AccesoResponse> call, @NonNull Response<AccesoResponse> response) {
        if (response.isSuccessful()) {
            AccesoResponse dto = response.body();
            if (dto != null && dto.isAcceso()) {
                int idUsuario = 1; // Valor por defecto
                if (dto.getUsuario() != null) {
                    idUsuario = dto.getUsuario().getId_usuario();
                }

                Intent i = new Intent(MainActivity.this, MenuPrincipalActivity.class);
                i.putExtra("id_usuario", idUsuario);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Datos de acceso incorrectos", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Error en el servidor: " + response.code(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(@NonNull Call<AccesoResponse> call, @NonNull Throwable t) {
        Toast.makeText(this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
    }
}