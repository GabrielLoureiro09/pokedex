package com.example.pokedex;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    private Context context;
    private List<Map<String, Object>> pokemonList;

    public PokemonAdapter(Context context, List<Map<String, Object>> pokemonList) {
        this.context = context;
        this.pokemonList = pokemonList;
    }

    public static class PokemonViewHolder extends RecyclerView.ViewHolder {
        TextView name, id;
        ImageView img, type1, type2;
        CardView cardView;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.pokemon_name);
            id = itemView.findViewById(R.id.pokemon_id);
            img = itemView.findViewById(R.id.pokemon_img);
            type1 = itemView.findViewById(R.id.pokemon_type);
            type2 = itemView.findViewById(R.id.pokemon_type2);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_pokemon_card, parent, false);
        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        Map<String, Object> pokemon = pokemonList.get(position);

        // Nome e ID
        holder.name.setText((String) pokemon.get("name"));
        holder.id.setText((String) pokemon.get("id"));

        // Imagem do Pokémon
        String imageUrl = (String) pokemon.get("imageUrl");
        Glide.with(context)
                .load(imageUrl)
                .into(holder.img);

        // Tipos
        List<String> types = (List<String>) pokemon.get("types");
        if(types.size() > 0){
            String typeUrl = getTypeUrl(types.get(0));
            Glide.with(context).load(typeUrl).into(holder.type1);
        }
        if(types.size() > 1){
            String typeUrl = getTypeUrl(types.get(1));
            Glide.with(context).load(typeUrl).into(holder.type2);
        }

        String color = TYPE_COLORS.getOrDefault(types.get(0), "#FFFFFF");
        holder.cardView.setCardBackgroundColor(Color.parseColor(color));
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    // Função para pegar ícone do tipo (black-white)
    private String getTypeUrl(String typeName){
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/types/generation-v/black-white/"
                + getTypeId(typeName) + ".png";
    }

    private int getTypeId(String typeName){
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
            case "unknown": return 10001;
            case "shadow": return 10002;
            default: return 0;
        }
    }
    private static final Map<String, String> TYPE_COLORS = new HashMap<String, String>() {{
        put("normal", "#A8A77A");
        put("fighting", "#C22E28");
        put("rock", "#B6A136");
        put("fire", "#EE8130");
        put("poison", "#A33EA1");
        put("ghost", "#735797");
        put("water", "#6390F0");
        put("ground", "#E2BF65");
        put("dragon", "#6F35FC");
        put("electric", "#F7D02C");
        put("flying", "#A98FF3");
        put("dark", "#705746");
        put("grass", "#7AC74C");
        put("psychic", "#F95587");
        put("steel", "#B7B7CE");
        put("ice", "#96D9D6");
        put("bug", "#A6B91A");
        put("fairy", "#D685AD");
    }};

}
