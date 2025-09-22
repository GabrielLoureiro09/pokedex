package com.example.pokedex.data.model;

import java.util.List;

public class PokemonListResponse {
    public List<Result> results;

    public static class Result {
        public String name;
        public String url;
    }
}
