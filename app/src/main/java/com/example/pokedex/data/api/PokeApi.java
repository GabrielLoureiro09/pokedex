package com.example.pokedex.data.api;

import com.example.pokedex.data.model.*;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface PokeApi {
    // Busca a lista inicial de Pokémon
    @GET("pokemon")
    Call<PokemonResponse> getPokemonList(@Query("limit") int limit);

    // Busca os detalhes principais de um Pokémon (sprites, stats...)
    @GET("pokemon/{idOrName}")
    Call<PokemonModel> getPokemonDetail(@Path("idOrName") String idOrName);
}