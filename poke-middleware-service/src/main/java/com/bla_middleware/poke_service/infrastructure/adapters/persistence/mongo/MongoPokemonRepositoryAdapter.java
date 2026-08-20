package com.bla_middleware.poke_service.infrastructure.adapters.persistence.mongo;

import com.bla_middleware.poke_service.infrastructure.adapters.external.PokeApiClient;
import com.bla_middleware.poke_service.infrastructure.adapters.external.PokeApiPaginationResponse;
import com.bla_middleware.poke_service.infrastructure.adapters.external.PokeApiPokemonResponse;
import com.bla_middleware.poke_service.internal.pokemon.domain.DetailedPokemon;
import com.bla_middleware.poke_service.internal.pokemon.domain.Pokemon;
import com.bla_middleware.poke_service.internal.pokemon.domain.events.PokemonBatchReplicationEvent;
import com.bla_middleware.poke_service.internal.pokemon.domain.events.PokemonReplicatedEvent;
import com.bla_middleware.poke_service.internal.pokemon.domain.events.PokemonUpdatedEvent;
import com.bla_middleware.poke_service.internal.pokemon.output.PokemonRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MongoPokemonRepositoryAdapter implements PokemonRepositoryPort {

    private final MongoPokemonRepository repository;
    private final PokeApiClient pokeApiClient;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    // key generates a unique cache key based on the page and size parameters, ensuring that different pages and sizes are cached separately.
    @Cacheable(value = "pokemonPages", key = "#page + '-' + #size")
    public List<Pokemon> findPaginated(int page, int size) {
        List<PokemonDocument> localDocs = repository.findAll(PageRequest.of(page, size)).getContent();

        if (localDocs.isEmpty()) {
            // a batch event can be triggered here to preload the database.
            System.out.println("Local replica is empty for the requested page.");
        }

        System.out.println("--- Local replica is empty for the requested page. Searching in PokeAPI... ---");
        PokeApiPaginationResponse apiResponse = pokeApiClient.fetchPaginatedFromExternalApi(page, size);

        List<String> namesToReplicate = apiResponse.results().stream()
                .map(PokeApiPokemonResponse.PokeApiNamedResource::name)
                .toList();

        // Trigger an asynchronous event to replicate the Pokémon details in the background
        eventPublisher.publishEvent(new PokemonBatchReplicationEvent(namesToReplicate));

        // 3. Transform the external API response into the domain model (Pokemon) and return it
        return apiResponse.results().stream()
                .map(resource -> {
                    // 1. Extraemos el ID numérico de forma segura limpiando la barra final
                    String url = resource.url(); // Ej: "https://pokeapi.co"
                    String cleanUrl = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
                    String id = cleanUrl.substring(cleanUrl.lastIndexOf("/") + 1);

                    // 2. Construimos la URL real del sprite oficial usando el ID numérico limpio
                    String spriteUrl = "https://githubusercontent.com" + id + ".png";

                    // 3. Retornamos el objeto Pokemon mapeado correctamente
                    return new Pokemon(
                            id,             // id (Usamos el número limpio como identificador único)
                            resource.name(), // name
                            spriteUrl,       // spriteUrl (¡Ahora sí es una imagen válida!)
                            List.of(),       // categories
                            0.0,             // mass
                            List.of()        // skills
                    );
                })
                .toList();
    }

    @Override
    public Optional<DetailedPokemon> findByNameOrId(String identifier) {
        // 1. First, try to find the Pokémon in the local MongoDB replica by ID or name (case-insensitive)
        Optional<PokemonDocument> localPokemon = repository.findById(identifier)
                .or(() -> repository.findByName(identifier.toLowerCase()));

        if (localPokemon.isPresent()) {
            System.out.println("--- Serving from local replica (MongoDB) ---");
            PokemonDocument doc = localPokemon.get();
            return Optional.of(mapToDetailedPokemon(doc));
        }

        // 2. If not found locally, fetch from the external PokeAPI
        System.out.println("--- Not serving from local.. Searching PokeAPI... ---");
        PokeApiPokemonResponse apiResponse = pokeApiClient.fetchPokemonFromExternalApi(identifier);

        // 3. Map the external response to the domain model
        DetailedPokemon domainPokemon = mapApiToDomain(apiResponse);

        // 4. EVENT-DRIVEN DISPATCH: Emit the replication event asynchronously
        eventPublisher.publishEvent(new PokemonReplicatedEvent(domainPokemon));

        return Optional.of(domainPokemon);
    }

    @Override
    public Optional<DetailedPokemon> findLocalById(String id) {
        return repository.findById(id)
                .map(this::mapToDetailedPokemon);
    }

    @Override
    public DetailedPokemon synchronizeLocalData(DetailedPokemon pokemon) {
        String spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemon.id() + ".png";

        PokemonDocument document = PokemonDocument.builder()
                .id(pokemon.id())
                .name(pokemon.name())
                .spriteUrl(spriteUrl)
                .categories(pokemon.evolutionaryLineage())
                .mass(0.0)
                .skills(List.of())
                .build();

        repository.save(document);
        System.out.println(">> [Local update] Pokémon '" + pokemon.name() + "' synchronized in MongoDB.");

        // EVENT-DRIVEN INVALIDATION: Notify other components that the Pokémon has been updated to clean cache
        eventPublisher.publishEvent(new PokemonUpdatedEvent(pokemon.id()));

        return pokemon;
    }


    // Mapping methods to convert between the MongoDB document and the domain model, and between the external API response and the domain model
    private DetailedPokemon mapToDetailedPokemon(PokemonDocument doc) {
        return new DetailedPokemon(doc.getId(), doc.getName(), doc.getSpriteUrl(), Map.of(), "Descripción local", List.of());
    }

    private DetailedPokemon mapApiToDomain(PokeApiPokemonResponse res) {
        String artwork = res.sprites().other() != null && res.sprites().other().officialArtwork() != null
                ? res.sprites().other().officialArtwork().frontDefault()
                : res.sprites().frontDefault();

        List<String> types = res.types().stream().map(t -> t.type().name()).toList();
        List<String> abilities = res.abilities().stream().map(a -> a.ability().name()).toList();

        return new DetailedPokemon(
                String.valueOf(res.id()),
                res.name(),
                artwork,
                Map.of("weight", res.weight()),
                "Pokemon replicated from PokeAPI",
                types
        );
    }
}
