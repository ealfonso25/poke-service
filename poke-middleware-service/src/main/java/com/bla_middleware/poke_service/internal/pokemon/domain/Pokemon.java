package com.bla_middleware.poke_service.internal.pokemon.domain;

import java.util.List;

public record Pokemon(
        String id,
        String name,
        String spriteUrl,
        List<String> categories, // Types ["grass", "poison"]
        double mass,             // Weight
        List<String> skills      // Abilities (abilities of PokeAPI)
) {}
