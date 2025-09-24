package com.example.pokedex.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pokedex.R;
import com.example.pokedex.adapter.PokemonAdapter;
import com.example.pokedex.data.api.PokeApi;
import com.example.pokedex.data.model.Pokemon;
import com.example.pokedex.data.model.PokemonModel;
import com.example.pokedex.data.model.PokemonResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Pokedex extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PokemonAdapter pokemonAdapter;
    private EditText searchInput;
    private PokeApi pokeApiService;

    private final List<PokemonModel> initialPokemonDetails = new ArrayList<>();
    private int detailsFetchedCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        recyclerView = findViewById(R.id.pokemonRecyclerView);
        searchInput = findViewById(R.id.search_input);

        setupRetrofit();
        pokemonAdapter = new PokemonAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(pokemonAdapter);

        setupSearchView();
        fetchInitialPokemonData();
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        pokeApiService = retrofit.create(PokeApi.class);
    }

    private void fetchInitialPokemonData() {
        pokeApiService.getPokemonList(151).enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(@NonNull Call<PokemonResponse> call, @NonNull Response<PokemonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pokemon> pokemonNameList = response.body().getResults();
                    initialPokemonDetails.clear();
                    detailsFetchedCounter = 0;
                    for (Pokemon pokemon : pokemonNameList) {
                        fetchDetailsForPokemon(pokemon, pokemonNameList.size());
                    }
                } else {
                    Toast.makeText(Pokedex.this, "Falha ao buscar dados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonResponse> call, @NonNull Throwable t) {
                Toast.makeText(Pokedex.this, "Falha ao buscar dados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDetailsForPokemon(Pokemon pokemon, int totalToFetch) {
        String idOrName = pokemon.getUrl().split("/")[6];
        pokeApiService.getPokemonDetail(idOrName).enqueue(new Callback<PokemonModel>() {
            @Override
            public void onResponse(@NonNull Call<PokemonModel> call, @NonNull Response<PokemonModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    initialPokemonDetails.add(response.body());
                }
                detailsFetchedCounter++;
                if (detailsFetchedCounter == totalToFetch) {
                    onAllInitialDetailsFetched();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonModel> call, @NonNull Throwable t) {
                detailsFetchedCounter++;
                if (detailsFetchedCounter == totalToFetch) {
                    onAllInitialDetailsFetched();
                }
            }
        });
    }

    private void setupSearchView() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterLocalList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void onAllInitialDetailsFetched() {
        initialPokemonDetails.sort(Comparator.comparingInt(PokemonModel::getId));
        pokemonAdapter.submitList(new ArrayList<>(initialPokemonDetails));
    }

    private void filterLocalList(String query) {
        List<PokemonModel> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(initialPokemonDetails);
        } else {
            for (PokemonModel pokemon : initialPokemonDetails) {
                if (pokemon.getName().toLowerCase(Locale.ROOT).startsWith(query.toLowerCase(Locale.ROOT))) {
                    filteredList.add(pokemon);
                }
            }
        }
        pokemonAdapter.submitList(filteredList);
    }
}