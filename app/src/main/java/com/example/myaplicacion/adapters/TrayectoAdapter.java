package com.example.myaplicacion.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaplicacion.R;
import com.example.myaplicacion.dto.Trayecto;

import java.util.List;

public class TrayectoAdapter extends RecyclerView.Adapter<TrayectoAdapter.TrayectoViewHolder> {

    public interface OnTrayectoClickListener {
        void onTrayectoClick(Trayecto trayecto);
    }

    private final OnTrayectoClickListener listener;
    private final List<Trayecto> trayectos;

    public TrayectoAdapter(List<Trayecto> trayectos, OnTrayectoClickListener listener) {
        this.trayectos = trayectos;
        this.listener = listener;
    }

    static class TrayectoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvDetalle;

        TrayectoViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDetalle = itemView.findViewById(R.id.tvDetalle);
        }
    }

    @NonNull
    @Override
    public TrayectoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trayecto, parent, false);
        return new TrayectoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull TrayectoViewHolder holder, int position) {
        Trayecto trayecto = trayectos.get(position);

        // Ajusta los getters según los nombres exactos en tu modelo Trayecto
        holder.tvNombre.setText(trayecto.getNombre());
        if (holder.tvDetalle != null) {
            // En lugar de getDescripcion(), usa getPuntos() o elimina/comenta esa línea:
            holder.tvDetalle.setText("Puntos: " + trayecto.getPuntos());
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrayectoClick(trayecto);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trayectos != null ? trayectos.size() : 0;
    }
}
