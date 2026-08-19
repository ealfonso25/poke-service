package com.bla_middleware.poke_service.internal.pokemon.output;

import com.bla_middleware.poke_service.internal.pokemon.domain.DetailedPokemon;
import com.bla_middleware.poke_service.internal.pokemon.domain.Pokemon;

import java.util.List;
import java.util.Optional;

public interface PokemonRepositoryPort {
     List<Pokemon> findPaginated(int page, int size);

     Optional<DetailedPokemon> findByNameOrId(String identifier);
}
