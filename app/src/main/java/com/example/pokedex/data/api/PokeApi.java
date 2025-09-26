package com.example.pokedex.data.api;

import com.example.pokedex.data.model.*;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface PokeApi {
    @GET("pokemon")
    Call<PokemonResponse> getPokemonList(@Query("limit") int limit);

    @GET("pokemon/{idOrName}")
    Call<PokemonModel> getPokemonDetail(@Path("idOrName") String idOrName);

    @GET("pokemon-species/{id}")
    Call<PokemonSpecies> getPokemonSpecies(@Path("id") int id);

    @GET("type/{name}")
    Call<PokemonTypes> getPokemonTypes(@Path("name") String name);
}