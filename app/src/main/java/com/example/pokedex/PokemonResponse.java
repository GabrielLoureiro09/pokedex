package com.example.pokedex;

import java.util.List;

public class PokemonResponse {
    public int id;
    public String name;
    public Sprites sprites;
    public List<TypeSlot> types;

    public static class Sprites {
        public String front_default;
    }

    public static class TypeSlot {
        public int slot;
        public Type type;

        public static class Type {
            public String name;
        }
    }

}
