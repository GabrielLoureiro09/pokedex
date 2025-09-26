package com.example.pokedex.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PokemonTypes {
    private String name;
    private int id;

    @SerializedName("damage_relations")
    private DamageRelations damageRelations;

    public String getName() { return name; }
    public int getId() { return id; }
    public DamageRelations getDamageRelations() { return damageRelations; }

    // Classe interna para as relações de dano
    public static class DamageRelations {
        @SerializedName("double_damage_from")
        private List<TypeRelation> doubleDamageFrom;

        @SerializedName("double_damage_to")
        private List<TypeRelation> doubleDamageTo;

        @SerializedName("half_damage_from")
        private List<TypeRelation> halfDamageFrom;

        @SerializedName("no_damage_from")
        private List<TypeRelation> noDamageFrom;

        public List<TypeRelation> getDoubleDamageFrom() { return doubleDamageFrom; }
        public List<TypeRelation> getDoubleDamageTo() { return doubleDamageTo; }
        public List<TypeRelation> getHalfDamageFrom() { return halfDamageFrom; }
        public List<TypeRelation> getNoDamageFrom() { return noDamageFrom; }
    }

    // Cada objeto dentro de double_damage_from, etc.
    public static class TypeRelation {
        private String name;
        private String url;

        public String getName() { return name; }
        public String getUrl() { return url; }
    }
}
