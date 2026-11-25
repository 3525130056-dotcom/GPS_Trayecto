package com.example.myaplicacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuPrincipalActivity extends AppCompatActivity {

  private ListView listaTrayectos;
  private String[] elementos;
  private int idUsuario;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_menu_principal);

    idUsuario = getIntent().getIntExtra("id_usuario", 1);

    listaTrayectos = findViewById(R.id.lista_menu);
    elementos = new String[]{"Capturar trayecto", "Lista de trayectos"};

    ArrayAdapter<String> adaptador = new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            elementos
    );
    listaTrayectos.setAdapter(adaptador);

    listaTrayectos.setOnItemClickListener((parent, view, position, id) -> {
      Intent nuevaActividad = null;
      switch (position) {
        case 0:
          nuevaActividad = new Intent(MenuPrincipalActivity.this, TrayectoActivity.class);
          break;
        case 1:
          nuevaActividad = new Intent(MenuPrincipalActivity.this, ListaTrayectosActivity.class);
          break;
      }

      if (nuevaActividad != null) {
        nuevaActividad.putExtra("id_usuario", idUsuario);
        startActivity(nuevaActividad);
      }
    });
  }
}