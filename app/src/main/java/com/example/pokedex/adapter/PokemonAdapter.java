package com.example.pokedex.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.pokedex.ui.Detail;
import com.example.pokedex.data.model.PokemonModel;
import com.example.pokedex.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    public enum SortType {
        ID_ASC, ID_DESC, NAME_ASC, NAME_DESC
    }

    private final List<PokemonModel> pokemonDetailList = new ArrayList<>();
    private final Context context;
    private final com.example.pokedex.data.api.PokeApi pokeApiService;

    public PokemonAdapter(Context context) {
        this.context = context;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        pokeApiService = retrofit.create(com.example.pokedex.data.api.PokeApi.class);
    }

    public void sortList(SortType sortType) {
        Comparator<PokemonModel> comparator;
        switch (sortType) {
            case ID_DESC: comparator = Comparator.comparingInt(PokemonModel::getId).reversed(); break;
            case NAME_ASC: comparator = Comparator.comparing(PokemonModel::getName); break;
            case NAME_DESC: comparator = Comparator.comparing(PokemonModel::getName).reversed(); break;
            default: comparator = Comparator.comparingInt(PokemonModel::getId); break;
        }
        pokemonDetailList.sort(comparator);
        notifyDataSetChanged();
    }

    public void submitList(List<PokemonModel> newPokemonDetails) {
        pokemonDetailList.clear();
        pokemonDetailList.addAll(newPokemonDetails);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pokemon_card, parent, false);
        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        if (position >= pokemonDetailList.size()) return;

        PokemonModel detail = pokemonDetailList.get(position);
        holder.pokemonNumber.setText(String.format(Locale.getDefault(), "#%03d", detail.getId()));
        holder.pokemonName.setText(detail.getName().substring(0, 1).toUpperCase() + detail.getName().substring(1));

        String imageUrl = null;
        if (detail.getSprites() != null) {
            if (detail.getSprites().getVersions() != null && detail.getSprites().getVersions().getGenerationV() != null && detail.getSprites().getVersions().getGenerationV().getBlackWhite() != null && detail.getSprites().getVersions().getGenerationV().getBlackWhite().getAnimated() != null && detail.getSprites().getVersions().getGenerationV().getBlackWhite().getAnimated().getFrontDefault() != null) {
                imageUrl = detail.getSprites().getVersions().getGenerationV().getBlackWhite().getAnimated().getFrontDefault();
            } else if (detail.getSprites().getOther() != null && detail.getSprites().getOther().getHome() != null && detail.getSprites().getOther().getHome().getFrontDefault() != null) {
                imageUrl = detail.getSprites().getOther().getHome().getFrontDefault();
            } else {
                imageUrl = detail.getSprites().getFrontDefault();
            }
        }

        Glide.with(context).load(imageUrl).into(holder.pokemonSprite);

        holder.type1.setImageDrawable(null);
        holder.type2.setImageDrawable(null);

        List<PokemonModel.Types> types = detail.getTypes();

        if (!types.isEmpty()) {
            // Tipo 1
            String type1Name = types.get(0).getType().getName();
            Glide.with(context).load(getTypeUrl(type1Name)).into(holder.type1);
            holder.cardView.setCardBackgroundColor(Color.parseColor(
                    TYPE_COLORS.getOrDefault(type1Name, "#FFFFFF")));

            // Tipo 2
            if (types.size() > 1) {
                String type2Name = types.get(1).getType().getName();
                Glide.with(context).load(getTypeUrl(type2Name)).into(holder.type2);
            } else {
                holder.type2.setImageDrawable(null);
            }
        } else {
            holder.type1.setImageDrawable(null);
            holder.type2.setImageDrawable(null);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detail.class);
            intent.putExtra("POKEMON_ID", detail.getId());
            context.startActivity(intent);
        });
    }

    private ImageView createTypeImageView(String typeName) {
        ImageView imageView = new ImageView(context);
        String resourceName = typeName.toLowerCase(Locale.ROOT) + "_type";

        int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        if (resourceId != 0) {
            imageView.setImageResource(resourceId);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(70), dpToPx(30));
        params.setMarginEnd(dpToPx(8));
        imageView.setLayoutParams(params);
        return imageView;
    }

    @Override
    public int getItemCount() {
        return pokemonDetailList.size();
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static class PokemonViewHolder extends RecyclerView.ViewHolder {
        TextView pokemonNumber, pokemonName;
        ImageView pokemonSprite, type1, type2;
        CardView cardView;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            pokemonNumber = itemView.findViewById(R.id.pokemon_id);
            pokemonSprite = itemView.findViewById(R.id.pokemon_img);
            pokemonName = itemView.findViewById(R.id.pokemon_name);
            type1 = itemView.findViewById(R.id.pokemon_type);
            type2 = itemView.findViewById(R.id.pokemon_type2);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
    private String getTypeUrl(String typeName){
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/types/generation-v/black-white/"
                + getTypeId(typeName) + ".png";
    }

    private int getTypeId(String typeName){
        switch(typeName.toLowerCase()){
            case "normal":
            case "fairy":
                return 1;
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
        put("fairy", "#A8A77A");
    }};

}