package com.example.myaplicacion;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myaplicacion.dto.DetalleTrayectoResponse;
import com.example.myaplicacion.dto.coordenada;
import com.example.myaplicacion.ws.WebApiAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.widget.Button;
import android.widget.Toast;
import com.example.myaplicacion.dto.EliminarResponse;
import com.google.gson.Gson;

public class MapaTrayectoActvity extends AppCompatActivity {
    private WebView webViewMapa;
    private TextView tvResumen;
    private int idUsuario;
    private String trayecto;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mapa_trayecto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        idUsuario = getIntent().getIntExtra("id_usuario", 1);
        trayecto = getIntent().getStringExtra("trayecto");
        if (trayecto == null || trayecto.isEmpty()) trayecto = "laboratorio";

        ((TextView) findViewById(R.id.tvTitulo)).setText(trayecto);
        tvResumen = findViewById(R.id.tvResumen);

        webViewMapa = findViewById(R.id.webViewMapa);
        webViewMapa.getSettings().setJavaScriptEnabled(true);
        webViewMapa.setWebViewClient(new WebViewClient());

        cargarCoordenadas();

        Button btnEliminar = findViewById(R.id.btnEliminar);
        btnEliminar.setOnClickListener(v -> eliminarTrayecto());
    }

    private void cargarCoordenadas() {
        WebApiAdapter.getApiService().getCoordenadas(idUsuario, trayecto)
                .enqueue(new Callback<DetalleTrayectoResponse>() {
                    @Override
                    public void onResponse(Call<DetalleTrayectoResponse> call, Response<DetalleTrayectoResponse> response) {
                        DetalleTrayectoResponse body = response.body();
                        if (response.isSuccessful() && body != null && body.isEncontrado()) {
                            dibujarTrayecto(body.getCoordenadas());
                        } else {
                            tvResumen.setText("Este trayecto no tiene coordenadas o no existe");
                        }
                    }

                    @Override
                    public void onFailure(Call<DetalleTrayectoResponse> call, Throwable t) {
                        tvResumen.setText("Error: " + t.getMessage());
                    }
                });
    }

    private void eliminarTrayecto() {
        Button btn = findViewById(R.id.btnEliminar);
        btn.setEnabled(false);

        WebApiAdapter.getApiService().eliminarTrayecto(idUsuario, trayecto)
                .enqueue(new Callback<EliminarResponse>() {
                    @Override
                    public void onResponse(Call<EliminarResponse> call, Response<EliminarResponse> response) {
                        EliminarResponse data = response.body();

                        if (data == null && response.errorBody() != null) {
                            try {
                                data = new Gson().fromJson(
                                        response.errorBody().string(), EliminarResponse.class);
                            } catch (Exception e) {
                                data = null;
                            }
                        }

                        if (response.isSuccessful() && data != null && data.isEliminado()) {
                            Toast.makeText(MapaTrayectoActvity.this,
                                    "Trayecto eliminado", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            btn.setEnabled(true);
                            String msg = (data != null && data.getMensaje() != null)
                                    ? data.getMensaje()
                                    : "Sin conexión con el servidor";
                            Toast.makeText(MapaTrayectoActvity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<EliminarResponse> call, Throwable t) {
                        btn.setEnabled(true);
                        Toast.makeText(MapaTrayectoActvity.this,
                                "Sin conexión con el servidor", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void dibujarTrayecto(List<coordenada> coord) {
        if (coord == null || coord.isEmpty()) {
            tvResumen.setText("No hay coordenadas para mostrar");
            return;
        }

        StringBuilder pts = new StringBuilder("[");
        for (int i = 0; i < coord.size(); i++) {
            pts.append("[").append(coord.get(i).getLatitud()).append(",").append(coord.get(i).getLongitud()).append("]");
            if (i < coord.size() - 1) pts.append(",");
        }
        pts.append("]");

        tvResumen.setText("Puntos trazados: " + coord.size());
        webViewMapa.loadDataWithBaseURL("https://unpkg.com", generarHtmlMapa(pts.toString()), "text/html", "UTF-8", null);
    }





    private String generarHtmlMapa(String puntosJson) {
        String iconoVerde = "L.divIcon({className:'',iconSize:[34,46],iconAnchor:[17,46],popupAnchor:[0,-42],"
                + "html:\"<svg viewBox='0 0 34 46' width='34' height='46'>"
                + "<path d='M17 1C8.7 1 2 7.7 2 16c0 11 15 28 15 28s15-17 15-28C32 7.7 25.3 1 17 1z' fill='#6BBF59' stroke='#1B3B14' stroke-width='2'/>"
                + "<circle cx='17' cy='16' r='8.5' fill='#FFFFFF'/>"
                + "<circle cx='17' cy='16' r='4' fill='#2E7D32'/>"
                + "</svg>\"})";

        return "<!DOCTYPE html><html><head><meta name='viewport' content='width=device-width,initial-scale=1,user-scalable=no'/>"
                + "<link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'/>"
                + "<script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>"
                + "<style>html,body,#map{height:100%;width:100%;margin:0;padding:0}</style></head><body><div id='map'></div><script>"
                + "var p=" + puntosJson + ",m=L.map('map').setView(p[0],16);"
                + "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{maxZoom:19}).addTo(m);"
                + "var l=L.polyline(p,{color:'#000000',weight:6}).addTo(m);"
                + "L.marker(p[0],{icon:" + iconoVerde + "}).addTo(m).bindPopup('Inicio: " + trayecto + "');"
                + "if(p.length>1)L.marker(p[p.length-1]).addTo(m).bindPopup('Fin');"
                + "m.fitBounds(l.getBounds(),{padding:[40,40]});</script></body></html>";
    }
}