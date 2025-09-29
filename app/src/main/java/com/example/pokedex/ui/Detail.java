package com.example.pokedex.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pokedex.R;
import com.example.pokedex.adapter.MoveAdapter;
import com.example.pokedex.data.api.PokeApi;
import com.example.pokedex.data.api.PokeApiClient;
import com.example.pokedex.data.model.PokemonModel;
import com.example.pokedex.data.model.PokemonMoves;
import com.example.pokedex.data.model.PokemonSpecies;
import com.example.pokedex.data.model.PokemonTypes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Detail extends AppCompatActivity {

    private Button bt_about, bt_status, bt_moves;
    private ImageView pokemonSprite, pokemonWeakness1, pokemonWeakness2, pokemonWeakness3, pokemonWeakness4, pokemonWeakness5;
    private TextView pokemonNumber, pokemonName, pokemonHeight_en, pokemonWeight_en, pokemonHeight_pt, pokemonWeight_pt, pokemonDescription, type, tvValueHp, tvValueAtk, tvValueDef, tvValueSpAtk, tvValueSpDef, tvValueSpeed;
    private PokeApi pokeApi;
    private ConstraintLayout background, cl_about, cl_status;
    private ProgressBar pbHp, pbAtk, pbDef, pbSpAtk, pbSpDef, pbSpeed;
    private int currentPokemonId;
    private String normalSpriteUrl;
    private RecyclerView rv_moves;
    private FloatingActionButton evolutionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initializeViews();
        setupRetrofitServices();

        evolutionButton.setOnClickListener(v -> {
            if (currentPokemonId != -1) {
                Intent intent = new Intent(Detail.this, EvolutionChain.class);
                intent.putExtra("POKEMON_ID", currentPokemonId);
                startActivity(intent);
            } else {
                Toast.makeText(Detail.this, "ID do Pokémon não disponível", Toast.LENGTH_SHORT).show();
            }
        });

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
        bt_about = findViewById(R.id.bt_left);
        bt_status = findViewById(R.id.bt_middle);
        bt_moves = findViewById(R.id.bt_right);
        cl_about = findViewById(R.id.cl_about);
        cl_status = findViewById(R.id.cl_status);
        rv_moves = findViewById(R.id.moveRecyclerView);
        pbHp = findViewById(R.id.pb_hp);
        pbAtk = findViewById(R.id.pb_atk);
        pbDef = findViewById(R.id.pb_def);
        pbSpAtk = findViewById(R.id.pb_satk);
        pbSpDef = findViewById(R.id.pb_sdef);
        pbSpeed = findViewById(R.id.pb_spd);
        tvValueHp = findViewById(R.id.tv_hp);
        tvValueAtk = findViewById(R.id.tv_atk);
        tvValueDef = findViewById(R.id.tv_def);
        tvValueSpAtk = findViewById(R.id.tv_satk);
        tvValueSpDef = findViewById(R.id.tv_sdef);
        tvValueSpeed = findViewById(R.id.tv_spd);
        evolutionButton = findViewById(R.id.evolutionButton);
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
            pokemonSprite.setImageResource(R.drawable.weekness_icon);
        }

        if (detail.getTypes() != null && !detail.getTypes().isEmpty()) {
            String typeName = detail.getTypes().get(0).getType().getName();
            type.setText(typeName.substring(0, 1).toUpperCase() + typeName.substring(1));

            int primaryColor = getTypeColor(typeName);

            int secondaryColor = Color.argb(
                    200,
                    Math.min(255, Math.max(0, Color.red(primaryColor) + 50)),
                    Math.min(255, Math.max(0, Color.green(primaryColor) + 50)),
                    Math.min(255, Math.max(0, Color.blue(primaryColor) + 50))
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

            int[] drawables = { R.drawable.tab_left, R.drawable.tab_middle, R.drawable.tab_right };
            Button[] buttons = { bt_about, bt_status, bt_moves };

            for (int i = 0; i < buttons.length; i++) {
                Drawable backgroundBt = ContextCompat.getDrawable(this, drawables[i]);
                if (backgroundBt != null) {
                    backgroundBt = backgroundBt.mutate();
                    if (i == 0) {
                        DrawableCompat.setTint(backgroundBt, primaryColor);
                        buttons[i].setBackground(backgroundBt);
                    } else {
                        DrawableCompat.setTint(backgroundBt, secondaryColor);
                        buttons[i].setBackground(backgroundBt);
                    }
                }
            }
            bt_about.setOnClickListener(v -> {
                cl_about.setVisibility(View.VISIBLE);
                cl_status.setVisibility(View.GONE);
                rv_moves.setVisibility(View.GONE);

                Drawable bgAbout = ContextCompat.getDrawable(this, R.drawable.tab_left).mutate();
                Drawable bgStatus = ContextCompat.getDrawable(this, R.drawable.tab_middle).mutate();
                Drawable bgMoves = ContextCompat.getDrawable(this, R.drawable.tab_right).mutate();

                DrawableCompat.setTint(bgAbout, primaryColor);
                DrawableCompat.setTint(bgStatus, secondaryColor);
                DrawableCompat.setTint(bgMoves, secondaryColor);

                bt_about.setBackground(bgAbout);
                bt_status.setBackground(bgStatus);
                bt_moves.setBackground(bgMoves);
            });
            bt_status.setOnClickListener(v -> {
                cl_about.setVisibility(View.GONE);
                cl_status.setVisibility(View.VISIBLE);
                rv_moves.setVisibility(View.GONE);

                Drawable bgAbout = ContextCompat.getDrawable(this, R.drawable.tab_left).mutate();
                Drawable bgStatus = ContextCompat.getDrawable(this, R.drawable.tab_middle).mutate();
                Drawable bgMoves = ContextCompat.getDrawable(this, R.drawable.tab_right).mutate();

                DrawableCompat.setTint(bgAbout, secondaryColor);
                DrawableCompat.setTint(bgStatus, primaryColor);
                DrawableCompat.setTint(bgMoves, secondaryColor);

                bt_about.setBackground(bgAbout);
                bt_status.setBackground(bgStatus);
                bt_moves.setBackground(bgMoves);
            });
            bt_moves.setOnClickListener(v -> {
                cl_about.setVisibility(View.GONE);
                cl_status.setVisibility(View.GONE);
                rv_moves.setVisibility(View.VISIBLE);

                Drawable bgAbout = ContextCompat.getDrawable(this, R.drawable.tab_left).mutate();
                Drawable bgStatus = ContextCompat.getDrawable(this, R.drawable.tab_middle).mutate();
                Drawable bgMoves = ContextCompat.getDrawable(this, R.drawable.tab_right).mutate();

                DrawableCompat.setTint(bgAbout, secondaryColor);
                DrawableCompat.setTint(bgStatus, secondaryColor);
                DrawableCompat.setTint(bgMoves, primaryColor);

                bt_about.setBackground(bgAbout);
                bt_status.setBackground(bgStatus);
                bt_moves.setBackground(bgMoves);
            });
        }

        fetchPokemonSpecies(detail.getId());
        fetchPokemonWeaknesses(detail.getTypes().get(0).getType().getName());
        if (detail.getStats() != null) {
            for (PokemonModel.Stats stat : detail.getStats()) {
                String statName = stat.getStat().getName();
                int value = stat.getBaseStat();
                populateIndividualStats(statName, value);
            }
        }
        fetchPokemonMoves(detail.getMoves());
    }

    private void populateIndividualStats(String statName, int value) {
        switch (statName) {
            case "hp":
                pbHp.setProgress(value);
                tvValueHp.setText(String.valueOf(value));
                break;
            case "attack":
                pbAtk.setProgress(value);
                tvValueAtk.setText(String.valueOf(value));
                break;
            case "defense":
                pbDef.setProgress(value);
                tvValueDef.setText(String.valueOf(value));
                break;
            case "special-attack":
                pbSpAtk.setProgress(value);
                tvValueSpAtk.setText(String.valueOf(value));
                break;
            case "special-defense":
                pbSpDef.setProgress(value);
                tvValueSpDef.setText(String.valueOf(value));
                break;
            case "speed":
                pbSpeed.setProgress(value);
                tvValueSpeed.setText(String.valueOf(value));
                break;
        }
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

    private void fetchPokemonMoves(List<PokemonModel.Moves> movesList) {
        if (movesList == null || movesList.isEmpty()) return;

        List<PokemonMoves> pokemonMoves = new ArrayList<>();
        final int[] count = {0};

        for (PokemonModel.Moves m : movesList) {
            String url = m.getMove().getUrl();
            String moveId = url.replace("https://pokeapi.co/api/v2/move/", "").replace("/", "");
            pokeApi.getPokemonMove(moveId).enqueue(new Callback<PokemonMoves>() {
                @Override
                public void onResponse(@NonNull Call<PokemonMoves> call, @NonNull Response<PokemonMoves> response) {
                    count[0]++;
                    if (response.isSuccessful() && response.body() != null) {
                        pokemonMoves.add(response.body());
                    }
                    if (count[0] == movesList.size()) {
                        setupMovesRecyclerView(pokemonMoves);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PokemonMoves> call, @NonNull Throwable t) {
                    count[0]++;
                    if (count[0] == movesList.size()) {
                        setupMovesRecyclerView(pokemonMoves);
                    }
                }
            });
        }
    }

    private void setupMovesRecyclerView(List<PokemonMoves> moves) {
        MoveAdapter adapter = new MoveAdapter(moves);
        RecyclerView recyclerView = findViewById(R.id.moveRecyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setAdapter(adapter);
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