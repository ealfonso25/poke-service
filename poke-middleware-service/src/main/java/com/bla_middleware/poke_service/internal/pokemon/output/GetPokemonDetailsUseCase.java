package com.bla_middleware.poke_service.internal.pokemon.output;

import com.bla_middleware.poke_service.internal.pokemon.domain.DetailedPokemon;

public class GetPokemonDetailsUseCase {

    private final PokemonRepositoryPort repositoryPort;

    public GetPokemonDetailsUseCase(PokemonRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public DetailedPokemon execute(String identifier) {
        return repositoryPort.findByNameOrId(identifier)
                .orElseThrow(() -> new RuntimeException("Pokemon not found: " + identifier));
    }
}

