package com.bla_middleware.poke_service.internal.pokemon.output;

import com.bla_middleware.poke_service.internal.pokemon.domain.Pokemon;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class BrowsePokemonUseCaseTestRed {

    private final PokemonRepositoryPort repositoryPort = Mockito.mock(PokemonRepositoryPort.class);
    private final BrowsePokemonUseCase browsePokemonUseCase = new BrowsePokemonUseCase(repositoryPort);

    @Test
    void shouldReturnPaginatedPokemonList() {
        int page = 0;
        int size = 2;
        List<Pokemon> mockPokemonList = List.of(
                new Pokemon("1", "bulbasaur", "sprite_url_1", List.of("grass"), 6.9, List.of("overgrow")),
                new Pokemon("4", "charmander", "sprite_url_2", List.of("fire"), 8.5, List.of("blaze"))
        );
        when(repositoryPort.findPaginated(page, size)).thenReturn(mockPokemonList);

        List<Pokemon> result = browsePokemonUseCase.execute(page, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("bulbasaur", result.get(0).name());
        assertEquals("charmander", result.get(1).name());
    }
}
