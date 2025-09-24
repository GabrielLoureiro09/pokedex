package com.example.pokedex.ui;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.pokedex.R;
import com.example.pokedex.data.api.PokeApi;
import com.example.pokedex.data.api.PokeApiClient;
import com.example.pokedex.data.model.PokemonModel;
import com.example.pokedex.data.model.PokemonSpecies;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Detail extends AppCompatActivity {

    private ImageView pokemonSprite;
    private TextView pokemonNumber, pokemonName, pokemonHeight_en, pokemonWeight_en, pokemonHeight_pt, pokemonWeight_pt, pokemonDescription, type;
    private PokeApi pokeApi;
    private int currentPokemonId;

    private String normalSpriteUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initializeViews();
        setupRetrofitServices();

        currentPokemonId = getIntent().getIntExtra("POKEMON_ID", -1);
        if (currentPokemonId != -1) {
            fetchPokemonDetails(currentPokemonId);
        } else {
            Toast.makeText(this, "Erro: ID do Pokémon não encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        pokemonSprite = findViewById(R.id.pkn_img);
        pokemonNumber = findViewById(R.id.pkn_id);
        pokemonName = findViewById(R.id.pkn_name);
        pokemonHeight_en = findViewById(R.id.height_en);
        pokemonWeight_en = findViewById(R.id.weight_en);
        pokemonHeight_pt = findViewById(R.id.height_pt);
        pokemonWeight_pt = findViewById(R.id.weight_pt);
        pokemonDescription = findViewById(R.id.pkn_description);
        type = findViewById(R.id.pkn_type);
    }

    private void setupRetrofitServices() {
        Retrofit retrofit = PokeApiClient.getInstance();
        pokeApi = retrofit.create(PokeApi.class);
    }
    private void fetchPokemonDetails(int pokemonId) {
        pokeApi.getPokemonDetail(String.valueOf(pokemonId)).enqueue(new Callback<PokemonModel>() {
            @Override
            public void onResponse(@NonNull Call<PokemonModel> call, @NonNull Response<PokemonModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonModel detail = response.body();
                    if (detail.getSprites() != null && detail.getSprites().getOther() != null && detail.getSprites().getOther().getHome() != null) {
                        normalSpriteUrl = detail.getSprites().getOther().getHome().getFrontDefault();
                    }
                    populateUI(detail);
                } else {
                    Toast.makeText(Detail.this, "Falha ao carregar detalhes", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<PokemonModel> call, @NonNull Throwable t) {
                Toast.makeText(Detail.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUI(PokemonModel detail) {
        pokemonName.setText(detail.getName().substring(0, 1).toUpperCase() + detail.getName().substring(1));
        pokemonNumber.setText(String.format(Locale.getDefault(), "#%03d", detail.getId()));

        float heightInMeters = detail.getHeight() / 10.0f;
        float weightInKg = detail.getWeight() / 10.0f;

        pokemonHeight_en.setText(String.format(Locale.getDefault(), "%d dm", detail.getHeight()));
        pokemonWeight_en.setText(String.format(Locale.getDefault(), "%d hg", detail.getWeight()));
        pokemonHeight_pt.setText(String.format(Locale.getDefault(), "%.1f m", heightInMeters));
        pokemonWeight_pt.setText(String.format(Locale.getDefault(), "%.1f kg", weightInKg));

        if (normalSpriteUrl != null) {
            Glide.with(this).load(normalSpriteUrl).into(pokemonSprite);
        } else {
            pokemonSprite.setImageResource(R.drawable.ic_search_resized);
        }

        if (detail.getTypes() != null && !detail.getTypes().isEmpty()) {
            String typeName = detail.getTypes().get(0).getType().getName();
            type.setText(typeName.substring(0, 1).toUpperCase() + typeName.substring(1));

            GradientDrawable bg = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.type_background);
            int bgColor = getTypeColor(typeName);
            if (bg != null) {
                type.setBackground(bg);
                bg.setColor(bgColor);
            }
        }

        fetchPokemonSpecies(detail.getId());
    }

    private void fetchPokemonSpecies(int pokemonId) {
        pokeApi.getPokemonSpecies(pokemonId).enqueue(new Callback<PokemonSpecies>() {
            @Override
            public void onResponse(@NonNull Call<PokemonSpecies> call, @NonNull Response<PokemonSpecies> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonSpecies speciesData = response.body();

                    // Filtra apenas entradas em inglês
                    List<PokemonSpecies.FlavorTextEntry> englishEntries = new ArrayList<>();
                    for (PokemonSpecies.FlavorTextEntry entry : speciesData.getFlavorTextEntries()) {
                        if (entry.getLanguage() != null && "en".equals(entry.getLanguage().getName())) {
                            englishEntries.add(entry);
                        }
                    }

                    if (!englishEntries.isEmpty()) {
                        Random random = new Random();
                        int randomIndex = random.nextInt(englishEntries.size());

                        String flavorText = englishEntries.get(randomIndex).getFlavorText();

                        flavorText = flavorText.replace("\n", " ").replace("\f", " ");

                        pokemonDescription.setText(" \"" + flavorText + "\" ");
                    } else {
                        pokemonDescription.setText("Descrição não disponível.");
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<PokemonSpecies> call, @NonNull Throwable t) {}
        });
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