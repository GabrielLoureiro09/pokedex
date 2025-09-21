package com.example.pokedex;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pokedex extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PokemonAdapter adapter;
    private List<Map<String, Object>> pokemonList;

    private PokeApi pokeApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        recyclerView = findViewById(R.id.pokemonRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        pokemonList = new ArrayList<>();
        adapter = new PokemonAdapter(this, pokemonList);
        recyclerView.setAdapter(adapter);

        pokeApi = ApiClient.getInstance().create(PokeApi.class);

        fetchPokemonList();
    }

    private void fetchPokemonList() {
        Call<PokemonListResponse> call = pokeApi.getPokemonList();
        call.enqueue(new Callback<PokemonListResponse>() {
            @Override
            public void onResponse(Call<PokemonListResponse> call, Response<PokemonListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PokemonListResponse.Result> results = response.body().results;
                    for (PokemonListResponse.Result item : results) {
                        String[] urlParts = item.url.split("/");
                        int id = Integer.parseInt(urlParts[urlParts.length - 1].isEmpty() ?
                                urlParts[urlParts.length - 2] : urlParts[urlParts.length - 1]);

                        fetchPokemonDetails(id);
                    }
                }
            }

            @Override
            public void onFailure(Call<PokemonListResponse> call, Throwable t) {
                Log.e("Pokedex", "Erro ao carregar lista de Pokémon", t);
            }
        });
    }

    private void fetchPokemonDetails(int id) {
        Call<PokemonResponse> call = pokeApi.getPokemon(id);
        call.enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonResponse p = response.body();

                    String formattedId = String.format("#%03d", p.id);

                    List<PokemonResponse.TypeSlot> sortedTypes = new ArrayList<>(p.types);
                    sortedTypes.sort((a, b) -> a.slot - b.slot);

                    List<String> types = new ArrayList<>();
                    for (PokemonResponse.TypeSlot slot : sortedTypes) {
                        String typeName = slot.type.name.toLowerCase();
                        if (!types.contains(typeName)) {
                            types.add(typeName);
                        }
                    }

                    Map<String, Object> pokemonMap = new HashMap<>();
                    pokemonMap.put("name", capitalize(p.name));
                    pokemonMap.put("id", formattedId);
                    pokemonMap.put("imageUrl", p.sprites.front_default);
                    pokemonMap.put("types", types);

                    pokemonList.add(pokemonMap);

                    Collections.sort(pokemonList, (p1, p2) -> {
                        int id1 = Integer.parseInt(((String)p1.get("id")).substring(1));
                        int id2 = Integer.parseInt(((String)p2.get("id")).substring(1));
                        return id1 - id2;
                    });

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<PokemonResponse> call, Throwable t) {
                Log.e("Pokedex", "Erro ao carregar Pokémon " + id, t);
            }
        });
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }
}
