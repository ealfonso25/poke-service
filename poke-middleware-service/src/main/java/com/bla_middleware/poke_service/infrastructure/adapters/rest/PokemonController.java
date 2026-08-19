package com.bla_middleware.poke_service.infrastructure.adapters.rest;

import com.bla_middleware.poke_service.internal.pokemon.domain.DetailedPokemon;
import com.bla_middleware.poke_service.internal.pokemon.domain.Pokemon;
import com.bla_middleware.poke_service.internal.pokemon.output.BrowsePokemonUseCase;
import com.bla_middleware.poke_service.internal.pokemon.output.GetPokemonDetailsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pokemon")
@RequiredArgsConstructor
public class PokemonController {

    private final BrowsePokemonUseCase browsePokemonUseCase;
    private final GetPokemonDetailsUseCase getPokemonDetailsUseCase;

    @GetMapping
    public ResponseEntity<List<Pokemon>> getPokemonPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<Pokemon> pokemonList = browsePokemonUseCase.execute(page, size);
        return ResponseEntity.ok(pokemonList);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<DetailedPokemon> getPokemonDetails(@PathVariable String identifier) {
        DetailedPokemon details = getPokemonDetailsUseCase.execute(identifier);
        return ResponseEntity.ok(details);
    }
}
