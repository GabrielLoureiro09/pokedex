package com.example.pokedex.ui;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.pokedex.R;
import com.example.pokedex.data.api.PokeApi;
import com.example.pokedex.data.api.PokeApiClient;
import com.example.pokedex.data.model.PokemonModel;
import com.example.pokedex.data.model.PokemonSpecies;
import com.example.pokedex.data.model.PokemonTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Detail extends AppCompatActivity {

    private ImageView pokemonSprite, pokemonWeakness1, pokemonWeakness2, pokemonWeakness3, pokemonWeakness4, pokemonWeakness5;
    private TextView pokemonNumber, pokemonName, pokemonHeight_en, pokemonWeight_en, pokemonHeight_pt, pokemonWeight_pt, pokemonDescription, type;
    private PokeApi pokeApi;
    private ConstraintLayout background;
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
        pokemonWeakness1 = findViewById(R.id.weakness);
        pokemonWeakness2 = findViewById(R.id.weakness2);
        pokemonWeakness3 = findViewById(R.id.weakness3);
        pokemonWeakness4 = findViewById(R.id.weakness4);
        pokemonWeakness5 = findViewById(R.id.weakness5);
        background = findViewById(R.id.main);
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

            int primaryColor = getTypeColor(typeName);

            int secondaryColor = Color.argb(
                    200,
                    Math.min(255, (Color.red(primaryColor) - 170)),
                    Math.min(255, (Color.green(primaryColor) - 170)),
                    Math.min(255, (Color.blue(primaryColor) - 170))
            );

            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{primaryColor, secondaryColor}
            );

            gradientDrawable.setCornerRadius(0f);
            background.setBackground(gradientDrawable);

            GradientDrawable bg = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.type_background);
            if (bg != null) {
                type.setBackground(bg);
                bg.setColor(primaryColor);
            }
        }


        fetchPokemonSpecies(detail.getId());
        fetchPokemonWeaknesses(detail.getTypes().get(0).getType().getName());
    }

    private void fetchPokemonSpecies(int pokemonId) {
        pokeApi.getPokemonSpecies(pokemonId).enqueue(new Callback<PokemonSpecies>() {
            @Override
            public void onResponse(@NonNull Call<PokemonSpecies> call, @NonNull Response<PokemonSpecies> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonSpecies speciesData = response.body();

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
            public void onFailure(@NonNull Call<PokemonSpecies> call, @NonNull Throwable t) {
            }
        });
    }

    private void fetchPokemonWeaknesses(String typeName) {
        pokeApi.getPokemonTypes(typeName).enqueue(new Callback<PokemonTypes>() {
            @Override
            public void onResponse(@NonNull Call<PokemonTypes> call, @NonNull Response<PokemonTypes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonTypes typeData = response.body();

                    if (typeData.getDamageRelations() != null &&
                            typeData.getDamageRelations().getDoubleDamageFrom() != null) {

                        List<PokemonTypes.TypeRelation> weaknesses =
                                typeData.getDamageRelations().getDoubleDamageFrom();

                        // limpa os slots
                        pokemonWeakness1.setImageDrawable(null);
                        pokemonWeakness2.setImageDrawable(null);
                        pokemonWeakness3.setImageDrawable(null);
                        pokemonWeakness4.setImageDrawable(null);
                        pokemonWeakness5.setImageDrawable(null);

                        if (!weaknesses.isEmpty())
                            Glide.with(Detail.this).load(getTypeUrl(weaknesses.get(0).getName())).into(pokemonWeakness1);
                        if (weaknesses.size() > 1)
                            Glide.with(Detail.this).load(getTypeUrl(weaknesses.get(1).getName())).into(pokemonWeakness2);
                        if (weaknesses.size() > 2)
                            Glide.with(Detail.this).load(getTypeUrl(weaknesses.get(2).getName())).into(pokemonWeakness3);
                        if (weaknesses.size() > 3)
                            Glide.with(Detail.this).load(getTypeUrl(weaknesses.get(3).getName())).into(pokemonWeakness4);
                        if (weaknesses.size() > 4)
                            Glide.with(Detail.this).load(getTypeUrl(weaknesses.get(4).getName())).into(pokemonWeakness5);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonTypes> call, @NonNull Throwable t) {
                Toast.makeText(Detail.this, "Erro ao carregar fraquezas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getTypeUrl(String typeName) {
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