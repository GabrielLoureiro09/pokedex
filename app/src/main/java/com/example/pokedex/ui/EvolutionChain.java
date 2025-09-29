package com.example.pokedex.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.pokedex.R;
import com.example.pokedex.data.api.PokeApi;
import com.example.pokedex.data.api.PokeApiClient;
import com.example.pokedex.data.model.PokemonEvolutions;
import com.example.pokedex.data.model.PokemonModel;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EvolutionChain extends AppCompatActivity {

    private LinearLayout containerEvolutions;
    private PokeApi pokeApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evolution_chain);

        containerEvolutions = findViewById(R.id.container_evolutions);

        Retrofit retrofit = PokeApiClient.getInstance();
        pokeApi = retrofit.create(PokeApi.class);

        int pokemonId = getIntent().getIntExtra("POKEMON_ID", -1);
        if (pokemonId != -1) {
            fetchEvolutionChain(pokemonId);
        }
    }

    private void fetchEvolutionChain(int pokemonId) {
        pokeApi.getPokemonSpecies(pokemonId).enqueue(new Callback<com.example.pokedex.data.model.PokemonSpecies>() {
            @Override
            public void onResponse(Call<com.example.pokedex.data.model.PokemonSpecies> call, Response<com.example.pokedex.data.model.PokemonSpecies> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Extrai o ID da evolution chain direto da URL
                    String url = response.body().getEvolutionChain().getUrl();
                    int evolutionId = Integer.parseInt(url.split("/")[url.split("/").length - 1].isEmpty() ?
                            url.split("/")[url.split("/").length - 2] :
                            url.split("/")[url.split("/").length - 1]);
                    fetchEvolutionChainDetails(evolutionId);
                }
            }

            @Override
            public void onFailure(Call<com.example.pokedex.data.model.PokemonSpecies> call, Throwable t) {}
        });
    }

    private void fetchEvolutionChainDetails(int evolutionId) {
        pokeApi.getPokemonEvolutionChain(evolutionId).enqueue(new Callback<PokemonEvolutions>() {
            @Override
            public void onResponse(Call<PokemonEvolutions> call, Response<PokemonEvolutions> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addEvolutionToLayout(response.body().getChain());
                }
            }

            @Override
            public void onFailure(Call<PokemonEvolutions> call, Throwable t) {}
        });
    }

    private void addEvolutionToLayout(PokemonEvolutions.ChainLink chain) {
        if (chain == null) return;

        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.item_evolution, containerEvolutions, false);
        ImageView sprite = view.findViewById(R.id.iv_evolution_sprite);
        TextView name = view.findViewById(R.id.tv_evolution_name);
        TextView arrow = view.findViewById(R.id.tv_arrow);
        ImageView typeIcon = view.findViewById(R.id.tv_evolution_type); // Novo ImageView no layout

        // Nome
        String capitalized = chain.getSpecies().getName().substring(0, 1).toUpperCase() + chain.getSpecies().getName().substring(1);
        name.setText(capitalized);

        // Sprite
        int speciesId = chain.getSpecies().getId();
        Glide.with(this)
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + speciesId + ".png")
                .into(sprite);

        // Tipo real com ícone
        pokeApi.getPokemonDetail(String.valueOf(speciesId)).enqueue(new Callback<PokemonModel>() {
            @Override
            public void onResponse(Call<PokemonModel> call, Response<PokemonModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String primaryType = response.body().getTypes().get(0).getType().getName();

                    Glide.with(EvolutionChain.this)
                            .load(getTypeUrl(primaryType))
                            .into(typeIcon);
                }
            }

            @Override
            public void onFailure(Call<PokemonModel> call, Throwable t) {}
        });

        containerEvolutions.addView(view);

        // Seta para próximo estágio
        if (chain.getEvolvesTo() != null && !chain.getEvolvesTo().isEmpty()) {
            arrow.setVisibility(View.VISIBLE);
            addEvolutionToLayout(chain.getEvolvesTo().get(0));
        } else {
            arrow.setVisibility(View.GONE);
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

    private int getTypeColor(String typeName) {
        switch (typeName.toLowerCase(Locale.ROOT)) {
            case "grass": return Color.parseColor("#78C850");
            case "fire": return Color.parseColor("#F08030");
            case "water": return Color.parseColor("#6890F0");
            case "electric": return Color.parseColor("#F8D030");
            case "ice": return Color.parseColor("#98D8D8");
            case "fighting": return Color.parseColor("#C03028");
            case "poison": return Color.parseColor("#A040A0");
            case "ground": return Color.parseColor("#E0C068");
            case "flying": return Color.parseColor("#A890F0");
            case "psychic": return Color.parseColor("#F85888");
            case "bug": return Color.parseColor("#A8B820");
            case "rock": return Color.parseColor("#B8A038");
            case "ghost": return Color.parseColor("#705898");
            case "dragon": return Color.parseColor("#7038F8");
            case "dark": return Color.parseColor("#705848");
            case "steel": return Color.parseColor("#B8B8D0");
            case "fairy": return Color.parseColor("#EE99AC");
            default: return Color.GRAY; // fallback
        }
    }
}
