package com.example.myaplicacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaplicacion.adapters.TrayectoAdapter;
import com.example.myaplicacion.dto.Trayecto;
import com.example.myaplicacion.dto.TrayectoResponse;
import com.example.myaplicacion.ws.WebApiAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaTrayectosActivity extends AppCompatActivity {

    private RecyclerView rvTrayectos;
    private ProgressBar progress;
    private TextView tvMensaje;
    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_trayectos_activity);

        rvTrayectos = findViewById(R.id.rvTrayectos);
        progress = findViewById(R.id.progress);
        tvMensaje = findViewById(R.id.tvMensaje);

        rvTrayectos.setLayoutManager(new LinearLayoutManager(this));
        rvTrayectos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        idUsuario = getIntent().getIntExtra("id_usuario", 1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTrayectos();
    }

    private void cargarTrayectos() {
        progress.setVisibility(View.VISIBLE);
        tvMensaje.setVisibility(View.GONE);

        WebApiAdapter.getApiService()
                .ConsultarTrayectos(idUsuario)
                .enqueue(new Callback<TrayectoResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TrayectoResponse> call,
                                           @NonNull Response<TrayectoResponse> response) {
                        progress.setVisibility(View.GONE);
                        TrayectoResponse body = response.body();

                        if (response.isSuccessful() && body != null && body.isEncontrado()) {
                            tvMensaje.setVisibility(View.GONE);
                            rvTrayectos.setAdapter(new TrayectoAdapter(body.getTrayectos(),
                                    trayecto -> abrirTrayecto(trayecto)));
                        } else {
                            rvTrayectos.setAdapter(null); // limpia la lista anterior
                            mostrarMensaje("Aún no tienes trayectos registrados");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TrayectoResponse> call,
                                          @NonNull Throwable t) {
                        progress.setVisibility(View.GONE);
                        mostrarMensaje("Sin conexión con el servidor");
                    }
                });
    }

    private void mostrarMensaje(String texto) {
        tvMensaje.setText(texto);
        tvMensaje.setVisibility(View.VISIBLE);
    }

    private void abrirTrayecto(Trayecto trayecto) {
        Intent intent = new Intent(ListaTrayectosActivity.this, MapaTrayectoActvity.class);
        intent.putExtra("id_usuario", idUsuario);
        intent.putExtra("trayecto", trayecto.getNombre());
        startActivity(intent);
    }
}