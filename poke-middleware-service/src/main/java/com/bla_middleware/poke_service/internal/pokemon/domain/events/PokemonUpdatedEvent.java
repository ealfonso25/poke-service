package com.bla_middleware.poke_service.internal.pokemon.domain.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PokemonUpdatedEvent {
    private final String pokemonId;
}
