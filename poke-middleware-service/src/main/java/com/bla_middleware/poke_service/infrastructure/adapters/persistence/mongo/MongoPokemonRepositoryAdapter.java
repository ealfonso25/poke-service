package com.bla_middleware.poke_service.infrastructure.adapters.persistence.mongo;

import com.bla_middleware.poke_service.internal.pokemon.domain.DetailedPokemon;
import com.bla_middleware.poke_service.internal.pokemon.domain.Pokemon;
import com.bla_middleware.poke_service.internal.pokemon.output.PokemonRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MongoPokemonRepositoryAdapter implements PokemonRepositoryPort {

    private final MongoPokemonRepository repository;

    @Override
    // key generates a unique cache key based on the page and size parameters, ensuring that different pages and sizes are cached separately.
    @Cacheable(value = "pokemonPages", key = "#page + '-' + #size")
    public List<Pokemon> findPaginated(int page, int size) {
        // Implementation of the logic to fetch paginated data from MongoDB using the repository
        return repository.findAll(PageRequest.of(page, size))
                .stream()
                .map(doc -> new Pokemon(
                        doc.getId(),
                        doc.getName(),
                        doc.getSpriteUrl(),
                        doc.getCategories(),
                        doc.getMass(),
                        doc.getSkills()
                ))
                .toList();
    }

    @Override
    public Optional<DetailedPokemon> findByNameOrId(String identifier) {
        return Optional.empty();
    }
}
