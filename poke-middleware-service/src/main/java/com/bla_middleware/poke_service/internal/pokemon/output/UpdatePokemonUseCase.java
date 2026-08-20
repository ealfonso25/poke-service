package com.bla_middleware.poke_service.internal.pokemon.output;

import com.bla_middleware.poke_service.internal.pokemon.domain.DetailedPokemon;
import com.bla_middleware.poke_service.internal.pokemon.domain.exceptions.InvalidPokemonPayloadException;
import com.bla_middleware.poke_service.internal.pokemon.domain.exceptions.PokemonNotFoundException;

public class UpdatePokemonUseCase {

    private final PokemonRepositoryPort repositoryPort;

    public UpdatePokemonUseCase(PokemonRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public DetailedPokemon execute(String id, DetailedPokemon request) {
        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new InvalidPokemonPayloadException(" Field 'name' is mandatory.");
        }
        if (request.imageUrl() == null || request.imageUrl().trim().isEmpty()) {
            throw new InvalidPokemonPayloadException(" Field 'imageUrl' is mandatory.");
        }

        repositoryPort.findLocalById(id)
                .orElseThrow(() -> new PokemonNotFoundException(id));

        return repositoryPort.synchronizeLocalData(request);
    }
}
