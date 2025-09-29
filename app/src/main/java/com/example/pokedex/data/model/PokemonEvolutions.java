package com.example.pokedex.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PokemonEvolutions {

    @SerializedName("id")
    private int id;

    @SerializedName("chain")
    private ChainLink chain;

    public int getId() {
        return id;
    }

    public ChainLink getChain() {
        return chain;
    }
    public static class ChainLink {

        @SerializedName("species")
        private Species species;

        @SerializedName("evolves_to")
        private List<ChainLink> evolvesTo;

        public Species getSpecies() {
            return species;
        }

        public List<ChainLink> getEvolvesTo() {
            return evolvesTo;
        }
    }
    public static class Species {

        @SerializedName("name")
        private String name;

        @SerializedName("url")
        private String url;

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public int getId() {
            if (url == null || url.isEmpty()) return -1;
            String[] parts = url.split("/");
            return Integer.parseInt(parts[parts.length - 1].isEmpty() ? parts[parts.length - 2] : parts[parts.length - 1]);
        }
    }
}
