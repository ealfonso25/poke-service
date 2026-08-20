package com.bla_middleware.poke_service.infrastructure.adapters.external;

import java.util.List;

public record PokeApiPaginationResponse(
        int count,
        String next,
        String previous,
        List<PokeApiPokemonResponse.PokeApiNamedResource> results
) {}
