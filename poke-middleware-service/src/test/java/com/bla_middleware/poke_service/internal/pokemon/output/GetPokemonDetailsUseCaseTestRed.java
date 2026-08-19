package com.bla_middleware.poke_service.internal.pokemon.output;

import com.bla_middleware.poke_service.internal.pokemon.domain.DetailedPokemon;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GetPokemonDetailsUseCaseTest {

    private final PokemonRepositoryPort repositoryPort = Mockito.mock(PokemonRepositoryPort.class);
    private final GetPokemonDetailsUseCase getPokemonDetailsUseCase = new GetPokemonDetailsUseCase(repositoryPort);

    @Test
    void shouldReturnDetailedPokemonWhenExists() {
        // Given
        String identifier = "bulbasaur";
        DetailedPokemon mockDetailed = new DetailedPokemon(
                "1",
                "bulbasaur",
                "official-artwork-url",
                Map.of("hp", 45, "attack", 49),
                "A strange seed was planted on its back.",
                List.of("bulbasaur", "ivysaur", "venusaur")
        );
        when(repositoryPort.findByNameOrId(identifier)).thenReturn(Optional.of(mockDetailed));

        // When
        DetailedPokemon result = getPokemonDetailsUseCase.execute(identifier);

        // Then
        assertNotNull(result);
        assertEquals("bulbasaur", result.name());
        assertEquals(45, result.coreStatistics().get("hp"));
        assertEquals("A strange seed was planted on its back.", result.description());
        assertEquals(3, result.evolutionaryLineage().size());
    }

    @Test
    void shouldThrowExceptionWhenPokemonNotFound() {
        // Given
        String identifier = "unknown";
        when(repositoryPort.findByNameOrId(identifier)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> getPokemonDetailsUseCase.execute(identifier));
    }
}
