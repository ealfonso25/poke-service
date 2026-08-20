package com.bla_middleware.poke_service.internal.pokemon.domain.events;

import com.bla_middleware.poke_service.internal.pokemon.domain.DetailedPokemon;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PokemonReplicatedEvent {
    // Transport the complete object for the listener to use it and save it in the database
    private final DetailedPokemon pokemon;
}
