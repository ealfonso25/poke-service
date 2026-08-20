package com.bla_middleware.poke_service.infrastructure.config;

import com.bla_middleware.poke_service.internal.pokemon.output.BrowsePokemonUseCase;
import com.bla_middleware.poke_service.internal.pokemon.output.GetPokemonDetailsUseCase;
import com.bla_middleware.poke_service.internal.pokemon.output.PokemonRepositoryPort;
import com.bla_middleware.poke_service.internal.pokemon.output.UpdatePokemonUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PokemonConfig {

    @Bean
    public BrowsePokemonUseCase browsePokemonUseCase(PokemonRepositoryPort pokemonRepositoryPort) {
        // Spring will inject the implementation of PokemonRepositoryPort
        // (which is  MongoPokemonRepositoryAdapter) and will inject it into the use case.
        return new BrowsePokemonUseCase(pokemonRepositoryPort);
    }

    @Bean
    public GetPokemonDetailsUseCase getPokemonDetailsUseCase(PokemonRepositoryPort pokemonDetailsRepositoryPort) {
        return new GetPokemonDetailsUseCase(pokemonDetailsRepositoryPort);
    }

    @Bean
    public UpdatePokemonUseCase updatePokemonUseCase(PokemonRepositoryPort pokemonRepositoryPort) {
        return new UpdatePokemonUseCase(pokemonRepositoryPort);
    }

}

