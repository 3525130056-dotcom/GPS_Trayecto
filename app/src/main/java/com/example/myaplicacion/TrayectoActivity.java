package com.example.myaplicacion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myaplicacion.dto.coordenadaResponse;
import com.example.myaplicacion.ws.WebApiAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrayectoActivity extends AppCompatActivity {

    private static final long INTERVALO_MS = 10000;
    private EditText etTrayecto;
    private Button btnCaptura;
    private TextView tvEstado;
    private TextView tvUltimaCoordenada;
    private FusedLocationProviderClient clienteUbicacion;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean capturando = false;
    private int puntosEnviados = 0;
    private int idUsuario;
    private String trayecto;

    private final ActivityResultLauncher<String[]> permisosLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    resultado -> {
                        Boolean finaccess = resultado.get(Manifest.permission.ACCESS_FINE_LOCATION);
                        if (Boolean.TRUE.equals(finaccess)) {
                            iniciarCaptura();
                        } else {
                            Toast.makeText(this,
                                    "Se necesita el permiso de ubicación",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trayecto);

        etTrayecto = findViewById(R.id.etTrayecto);
        btnCaptura = findViewById(R.id.btnCaptura);
        tvEstado = findViewById(R.id.tvEstado);
        tvUltimaCoordenada = findViewById(R.id.tvUltimaCoordenada);

        idUsuario = getIntent().getIntExtra("id_usuario", 1);

        clienteUbicacion = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVALO_MS)
                .setMinUpdateIntervalMillis(INTERVALO_MS / 2)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location ubicacion = locationResult.getLastLocation();
                if (ubicacion != null) {
                    enviarCoordenada(
                            ubicacion.getLatitude(),
                            ubicacion.getLongitude());
                }
            }
        };

        btnCaptura.setOnClickListener(v -> {
            if (capturando) {
                detenerCaptura();
            } else {
                validarYComenzar();
            }
        });

        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (capturando) {
                            Toast.makeText(TrayectoActivity.this,
                                    "Detén la captura antes de salir",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            setEnabled(false);
                            getOnBackPressedDispatcher().onBackPressed();
                        }
                    }
                });
    }

    private void validarYComenzar() {
        trayecto = etTrayecto.getText().toString().trim();
        if (trayecto.isEmpty()) {
            etTrayecto.setError("Escribe el nombre del trayecto");
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            iniciarCaptura();
        } else {
            permisosLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    @SuppressLint("MissingPermission")
    private void iniciarCaptura() {
        clienteUbicacion.requestLocationUpdates(locationRequest, locationCallback,
                Looper.getMainLooper());

        capturando = true;
        puntosEnviados = 0;
        etTrayecto.setEnabled(false);
        btnCaptura.setText("Detener captura");
        tvEstado.setText("Capturando... esperando la primera coordenada");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void detenerCaptura() {
        clienteUbicacion.removeLocationUpdates(locationCallback);

        capturando = false;
        etTrayecto.setEnabled(true);
        btnCaptura.setText("Iniciar captura");
        tvEstado.setText("Captura detenida. Puntos enviados: " + puntosEnviados);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void enviarCoordenada(double latitud, double longitud) {
        tvUltimaCoordenada.setText(String.format(Locale.US, "Última: %.7f, %.7f",
                latitud, longitud));

        WebApiAdapter.getApiService()
                .guardarCoordenada(idUsuario, trayecto, latitud, longitud)
                .enqueue(new Callback<coordenadaResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<coordenadaResponse> call,
                                           @NonNull Response<coordenadaResponse> response) {
                        coordenadaResponse body = response.body();
                        if (response.isSuccessful() && body != null && body.isGuardado()) {
                            puntosEnviados++;
                            tvEstado.setText("Capturando... puntos enviados: " + puntosEnviados);
                        } else {
                            tvEstado.setText("El servidor responde error (" + response.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<coordenadaResponse> call, @NonNull Throwable t) {
                        tvEstado.setText("Sin conexión, se reintentará con la siguiente coordenada");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (capturando) {
            clienteUbicacion.removeLocationUpdates(locationCallback);
        }
    }
}