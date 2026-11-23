package com.cesba.mascotas.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cesba.mascotas.R;
import com.cesba.mascotas.models.AreaConAforo;

import java.util.List;

public class AreasAdapter extends RecyclerView.Adapter<AreasAdapter.AreaViewHolder> {

    private List<AreaConAforo> listaAreas;

    public AreasAdapter(List<AreaConAforo> listaAreas) {
        this.listaAreas = listaAreas;
    }

    public void actualizarLista(List<AreaConAforo> nuevasAreas) {
        this.listaAreas = nuevasAreas; // Cambia 'listaAreas' si tu variable de lista se llama distinto
        notifyDataSetChanged();
    }

    @NonNull
    @Override



    public AreaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_area, parent, false);
        return new AreaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AreaViewHolder holder, int position) {
        AreaConAforo area = listaAreas.get(position);
        holder.tvNombre.setText(area.getNombre());
        holder.tvHorario.setText("Horario: " + area.getHorarioApertura() + " - " + area.getHorarioCierre() + " hrs");
        holder.tvReglas.setText("Reglas: " + area.getReglasRestricciones());
        holder.tvAforo.setText("Aforo: " + area.getAforoActual() + "/" + area.getCapacidadMaxima());

        holder.pbAforo.setMax(area.getCapacidadMaxima());
        holder.pbAforo.setProgress(area.getAforoActual());

        double porcentaje = ((double) area.getAforoActual() / area.getCapacidadMaxima()) * 100;
        if (porcentaje >= 100) {
            holder.tvAforo.setTextColor(Color.RED);
        } else if (porcentaje >= 70) {
            holder.tvAforo.setTextColor(Color.parseColor("#FFA500"));
        } else {
            holder.tvAforo.setTextColor(Color.parseColor("#2E7D32"));
        }
    }



    @Override
    public int getItemCount() {
        return listaAreas.size();
    }



    static class AreaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvHorario, tvReglas, tvAforo;
        ProgressBar pbAforo;

        public AreaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreArea);
            tvHorario = itemView.findViewById(R.id.tvHorarioArea);
            tvReglas = itemView.findViewById(R.id.tvReglasArea);
            tvAforo = itemView.findViewById(R.id.tvAforoArea);
            pbAforo = itemView.findViewById(R.id.pbAforoArea);
        }
    }

}
