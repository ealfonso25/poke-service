package com.bla_middleware.poke_service.infrastructure.adapters.cache;

import com.bla_middleware.poke_service.internal.pokemon.domain.events.PokemonUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PokemonCacheListener {

    // Delete all the saved pages when the event is triggered
    @CacheEvict(value = "pokemonPages", allEntries = true)
    @EventListener
    public void onPokemonUpdated(PokemonUpdatedEvent event) {
        log.info("Invalidating cache for pages due to Pokémon update: {}", event.getPokemonId());
    }
}
