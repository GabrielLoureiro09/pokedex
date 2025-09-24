package com.example.pokedex.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PokemonSpecies {

    @SerializedName("flavor_text_entries")
    private List<FlavorTextEntry> flavorTextEntries;
    @SerializedName("evolution_chain")
    private EvolutionChainInfo evolutionChain;

    public List<FlavorTextEntry> getFlavorTextEntries() { return flavorTextEntries; }
    public EvolutionChainInfo getEvolutionChain() { return evolutionChain; }

    public static class FlavorTextEntry {
        @SerializedName("flavor_text")
        private String flavorText;
        private Language language;
        public String getFlavorText() { return flavorText; }
        public Language getLanguage() { return language; }
    }
    public static class Language {
        private String name;
        public String getName() { return name; }
    }
    public static class EvolutionChainInfo {
        private String url;
        public String getUrl() { return url; }
    }
}