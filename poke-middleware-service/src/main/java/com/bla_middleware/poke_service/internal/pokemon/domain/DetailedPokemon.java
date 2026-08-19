package com.bla_middleware.poke_service.internal.pokemon.domain;

import java.util.List;
import java.util.Map;

public record DetailedPokemon(
        String id,
        String name,
        String imageUrl,
        Map<String, Integer> coreStatistics, // Ej: {"hp": 45, "attack": 49}
        String description,                  // Narrative description of the Pokémon
        List<String> evolutionaryLineage     // Ej: ["Bulbasaur", "Ivysaur", "Venusaur"]
) {}
