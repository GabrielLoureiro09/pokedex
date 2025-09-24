package com.example.pokedex.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PokemonModel {

    private int id;
    private String name;
    private List<Types> types;
    private Sprites sprites;
    private int height;
    private int weight;
    private List<Stats> stats;

    public int getId() { return id; }
    public String getName() { return name; }
    public List<Types> getTypes() { return types; }
    public Sprites getSprites() { return sprites; }
    public int getHeight() { return height; }
    public int getWeight() { return weight; }
    public List<Stats> getStats() { return stats; }

    public static class Types {
        @SerializedName("slot")
        private int slot;
        private Type type;
        public String getSlot(int slot) { return getType().getName(); }
        public Type getType() { return type; }
    }

    public static class Type {
        private String name;
        public String getName() { return name; }
    }

    public static class Sprites {
        @SerializedName("front_default")
        private String frontDefault;
        private Other other;
        private Versions versions;

        public String getFrontDefault() { return frontDefault; }
        public Other getOther() { return other; }
        public Versions getVersions() { return versions; }

        public static class Other {
            private Home home;
            private OfficialArtwork officialArtwork;

            public Home getHome() { return home; }
            public OfficialArtwork getOfficialArtwork() { return officialArtwork; }
        }

        public static class Home {
            @SerializedName("front_default")
            private String frontDefault;

            // Eu adiciono este campo para capturar a URL do sprite shiny.
            @SerializedName("front_shiny")
            private String frontShiny;

            public String getFrontDefault() { return frontDefault; }

            // E este é o método getter que estava faltando.
            public String getFrontShiny() { return frontShiny; }
        }

        public static class OfficialArtwork {
            @SerializedName("front_default")
            private String frontDefault;
            public String getFrontDefault() { return frontDefault; }
        }

        public static class Versions {
            @SerializedName("generation-v")
            private GenerationV generationV;
            public GenerationV getGenerationV() { return generationV; }
        }

        public static class GenerationV {
            @SerializedName("black-white")
            private BlackWhite blackWhite;
            public BlackWhite getBlackWhite() { return blackWhite; }
        }

        public static class BlackWhite {
            private Animated animated;
            public Animated getAnimated() { return animated; }
        }

        public static class Animated {
            @SerializedName("front_default")
            private String frontDefault;
            public String getFrontDefault() { return frontDefault; }
        }
    }

    public static class Stats {
        @SerializedName("base_stat")
        private int baseStat;
        private Stat stat;
        public int getBaseStat() { return baseStat; }
        public Stat getStat() { return stat; }
    }

    public static class Stat {
        private String name;
        public String getName() { return name; }
    }
}