package com.example.myaplicacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
        String usuario = valorUser.getText().toString();
        String password = valorPassword.getText().toString();

        Call<AccesoResponse> call = WebApiAdapter.getApiService().validaAcceso(usuario, password);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<AccesoResponse> call, Response<AccesoResponse> response) {
        if (response.isSuccessful()) {
            AccesoResponse dto = response.body();
            if ( dto != null && dto.isAcceso()) {
                Intent i = new Intent(this, Menu.class);
                startActivity(i);
            } else {
                // datos incorrectos
                Toast.makeText(this, "Datos de acceso incorrectos", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onFailure(Call<AccesoResponse> call, Throwable t) {
        Toast.makeText(this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
    }
}
