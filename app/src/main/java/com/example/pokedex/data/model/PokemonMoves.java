package com.example.pokedex.data.model;

import java.util.List;

public class PokemonMoves {
    private String name;
    private int power;
    private int accuracy;
    private int pp;
    private Type type;
    private List<EffectEntry> effectEntries;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPower() { return power; }
    public void setPower(int power) { this.power = power; }
    public int getAccuracy() { return accuracy; }
    public void setAccuracy(int accuracy) { this.accuracy = accuracy; }
    public int getPp() { return pp; }
    public void setPp(int pp) { this.pp = pp; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public static class Type {
        private String name;

        public String getName() { return name; }
    }

    public static class EffectEntry {
        private String effect;
        private Language language;
        public String getEffect() { return effect; }
        public Language getLanguage() { return language; }

        public static class Language {
            private String name;

            public String getName() { return name; }
        }
    }
}
