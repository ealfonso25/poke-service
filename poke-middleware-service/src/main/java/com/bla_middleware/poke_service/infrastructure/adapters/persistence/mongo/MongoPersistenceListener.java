package com.bla_middleware.poke_service.infrastructure.adapters.persistence.mongo;

import com.bla_middleware.poke_service.infrastructure.adapters.external.PokeApiClient;
import com.bla_middleware.poke_service.internal.pokemon.domain.DetailedPokemon;
import com.bla_middleware.poke_service.internal.pokemon.domain.events.PokemonBatchReplicationEvent;
import com.bla_middleware.poke_service.internal.pokemon.domain.events.PokemonReplicatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MongoPersistenceListener {

    private final MongoPokemonRepository mongoRepository;
    private final PokeApiClient pokeApiClient;

    @Async // ◄--- No blocking the main thread, allowing the event to be processed in the background
    @EventListener
    public void handlePokemonReplicated(PokemonReplicatedEvent event) {
        DetailedPokemon pokemon = event.getPokemon();

        // We check if it already exists just in case
        if (mongoRepository.existsById(pokemon.id())) {
            return;
        }

        // We build the document for MongoDB using Lombok's Builder
        PokemonDocument document = PokemonDocument.builder()
                .id(pokemon.id())
                .name(pokemon.name())
                .spriteUrl(pokemon.imageUrl())
                .categories(pokemon.evolutionaryLineage())
                .mass(0.0)
                .skills(List.of())
                .build();

        mongoRepository.save(document);
        System.out.println(">> [Successful Replication] Pokémon '" + pokemon.name() + "' saved asynchronously in MongoDB.");
    }

    @Async
    @EventListener
    public void handleBatchReplication(PokemonBatchReplicationEvent event) {
        System.out.println(">>Secondary thread. Starting batch replication for Pokémon names: " + event.getPokemonNames());

        for (String name : event.getPokemonNames()) {
            try {
                // 1. Verify if the Pokémon already exists in the local MongoDB replica
                if (mongoRepository.findByName(name.toLowerCase()).isPresent()) {
                    continue;
                }

                // 2. If it does not exist, use the client to fetch its full details from PokeAPI
                var apiResponse = pokeApiClient.fetchPokemonFromExternalApi(name);

                // Map the fields to the MongoDB document using Lombok's Builder
                String artwork = (apiResponse.sprites() != null && apiResponse.sprites().other() != null && apiResponse.sprites().other().officialArtwork() != null)
                        ? apiResponse.sprites().other().officialArtwork().frontDefault()
                        : (apiResponse.sprites() != null ? apiResponse.sprites().frontDefault() : null);

                List<String> types = apiResponse.types().stream().map(t -> t.type().name()).toList();
                List<String> abilities = apiResponse.abilities().stream().map(a -> a.ability().name()).toList();

                PokemonDocument document = PokemonDocument.builder()
                        .id(String.valueOf(apiResponse.id()))
                        .name(apiResponse.name())
                        .spriteUrl(artwork)
                        .categories(types)
                        .mass(apiResponse.weight() / 10.0) // PokéAPI handles weight in hectograms, converting to kg
                        .skills(abilities)
                        .build();

                // 3. Physically save in the Docker container of MongoDB
                mongoRepository.save(document);
                System.out.println(">> [Batch Replica] Successful asynchronous save for: " + name);

            } catch (Exception e) {
                System.err.println(">> [Error in Batch] Could not asynchronously replicate " + name + ": " + e.getMessage());
            }
        }
        System.out.println(">> [Secondary Thread] Batch replication processing completed.");
    }

}
