package com.example.pokedex.data.api;

import com.example.pokedex.data.model.PokemonListResponse;
import com.example.pokedex.data.model.PokemonResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PokeApi {

    @GET("pokemon?limit=151")
    Call<PokemonListResponse> getPokemonList();

    @GET("pokemon/{id}")
    Call<PokemonResponse> getPokemon(@Path("id") int id);

}
