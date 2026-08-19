package com.bla_middleware.poke_service.internal.pokemon.output;

import com.bla_middleware.poke_service.internal.pokemon.domain.Pokemon;

import java.util.List;


public class BrowsePokemonUseCase {

    private final PokemonRepositoryPort pokemonRepositoryPort;

    public BrowsePokemonUseCase(PokemonRepositoryPort pokemonRepositoryPort) {
        this.pokemonRepositoryPort = pokemonRepositoryPort;
    }

    public List<Pokemon> execute(int page, int size) {
        return pokemonRepositoryPort.findPaginated(page, size);
    }
}
