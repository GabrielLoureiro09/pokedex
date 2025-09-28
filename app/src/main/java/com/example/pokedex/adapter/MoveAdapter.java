package com.example.pokedex.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pokedex.R;
import com.example.pokedex.data.model.PokemonMoves;

import java.util.List;

public class MoveAdapter extends RecyclerView.Adapter<MoveAdapter.MoveViewHolder> {

    private final List<PokemonMoves> movesList;

    public MoveAdapter(List<PokemonMoves> movesList) {
        this.movesList = movesList;
    }

    @NonNull
    @Override
    public MoveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_move_card, parent, false);
        return new MoveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoveViewHolder holder, int position) {
        PokemonMoves move = movesList.get(position);

        holder.tvMoveName.setText(capitalize(move.getName()));
        holder.tvPower.setText(move.getPower() > 0 ? String.valueOf(move.getPower()) : "-");
        holder.tvAccuracy.setText(move.getAccuracy() > 0 ? move.getAccuracy() + "%" : "-");
        holder.tvPP.setText(move.getPp() > 0 ? String.valueOf(move.getPp()) : "-");

        if (move.getType() != null && move.getType().getName() != null) {
            String typeName = move.getType().getName();

            String url = getTypeUrl(typeName); // usa a função que mapeia certinho

            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .into(holder.ivType);
        }
    }

    @Override
    public int getItemCount() {
        return movesList.size();
    }

    static class MoveViewHolder extends RecyclerView.ViewHolder {
        TextView tvMoveName, tvPower, tvAccuracy, tvPP;
        ImageView ivType;

        public MoveViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMoveName = itemView.findViewById(R.id.move_name);
            tvPower = itemView.findViewById(R.id.tv_pwr);
            tvAccuracy = itemView.findViewById(R.id.tv_acc);
            tvPP = itemView.findViewById(R.id.tv_pp);
            ivType = itemView.findViewById(R.id.type_move);
        }
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "-";
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private String getTypeUrl(String typeName) {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/types/generation-v/black-white/"
                + getTypeId(typeName) + ".png";
    }

    private int getTypeId(String typeName) {
        switch(typeName.toLowerCase()){
            case "normal": return 1;
            case "fighting": return 2;
            case "flying": return 3;
            case "poison": return 4;
            case "ground": return 5;
            case "rock": return 6;
            case "bug": return 7;
            case "ghost": return 8;
            case "steel": return 9;
            case "fire": return 10;
            case "water": return 11;
            case "grass": return 12;
            case "electric": return 13;
            case "psychic": return 14;
            case "ice": return 15;
            case "dragon": return 16;
            case "dark": return 17;
            case "fairy": return 18;
            default: return 0;
        }
    }

}
