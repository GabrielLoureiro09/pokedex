package com.example.pokedex;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
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
        for (int i = 1; i <= 151; i++) {
            Call<PokemonResponse> call = pokeApi.getPokemon(i);
            int finalI = i;

            call.enqueue(new Callback<PokemonResponse>() {
                @Override
                public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PokemonResponse p = response.body();

                        // Formata ID
                        String formattedId = String.format("#%03d", p.id);

                        // Ordena tipos pelo slot
                        List<PokemonResponse.TypeSlot> sortedTypes = new ArrayList<>(p.types);
                        sortedTypes.sort((a, b) -> a.slot - b.slot);

                        // Cria nova lista de tipos (sempre nova instância)
                        List<String> types = new ArrayList<>();
                        for (PokemonResponse.TypeSlot slot : sortedTypes) {
                            types.add(slot.type.name.toLowerCase());
                        }

                        // Cria Map novo para cada Pokémon
                        Map<String, Object> pokemonMap = new HashMap<>();
                        pokemonMap.put("name", capitalize(p.name));
                        pokemonMap.put("id", formattedId);
                        pokemonMap.put("imageUrl", p.sprites.front_default);
                        pokemonMap.put("types", types);

                        // Adiciona à lista
                        pokemonList.add(pokemonMap);

                        // Ordena a lista pelo ID numérico
                        pokemonList.sort((p1, p2) -> {
                            int id1 = Integer.parseInt(((String)p1.get("id")).substring(1));
                            int id2 = Integer.parseInt(((String)p2.get("id")).substring(1));
                            return id1 - id2;
                        });

                        // Atualiza adapter
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<PokemonResponse> call, Throwable t) {
                    Log.e("Pokedex", "Erro ao carregar Pokémon " + finalI, t);
                }
            });
        }
    }

    private String capitalize(String str){
        if(str == null || str.isEmpty()) return str;
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }
}
