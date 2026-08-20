package com.bla_middleware.poke_service.infrastructure.adapters.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PokeApiPokemonResponse(
        int id,
        String name,
        PokeApiSprites sprites,
        int weight,
        List<PokeApiAbilityWrapper> abilities,
        List<PokeApiTypeWrapper> types
) {

    public record PokeApiSprites(
            @JsonProperty("front_default") String frontDefault,
            PokeApiOther other
    ) {
    }

    public record PokeApiOther(
            @JsonProperty("official-artwork") PokeApiOfficialArtwork officialArtwork
    ) {
    }

    public record PokeApiOfficialArtwork(
            @JsonProperty("front_default") String frontDefault
    ) {
    }

    public record PokeApiAbilityWrapper(PokeApiNamedResource ability) {
    }

    public record PokeApiTypeWrapper(PokeApiNamedResource type) {
    }

    public record PokeApiNamedResource(String name, String url) {
    }

}

