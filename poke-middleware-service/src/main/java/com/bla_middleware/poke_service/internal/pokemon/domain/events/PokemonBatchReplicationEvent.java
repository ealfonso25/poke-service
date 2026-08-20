package com.bla_middleware.poke_service.internal.pokemon.domain.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PokemonBatchReplicationEvent {
    // List of names to be replicated, the listener will fetch the details for each name and save it in the database
    private final List<String> pokemonNames;
}
