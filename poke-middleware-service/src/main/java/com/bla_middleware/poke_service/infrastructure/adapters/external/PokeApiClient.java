package com.bla_middleware.poke_service.infrastructure.adapters.external;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PokeApiClient {

    private final RestClient restClient;

    public PokeApiClient() {
        // Initialize the RestClient with the base URL of the PokeAPI
        this.restClient = RestClient.builder()
                .baseUrl("https://pokeapi.co")
                .build();
    }

    public PokeApiPokemonResponse fetchPokemonFromExternalApi(String nameOrId) {
        String exactPath = "/api/v2/pokemon/" + nameOrId.toLowerCase().trim() + "/";
        System.out.println("--- Outgoing request to: https://pokeapi.co" + exactPath + " ---");

        try {
            return restClient.get()
                    .uri(exactPath)
                    .retrieve()
                    .body(PokeApiPokemonResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error consulting PokeAPI at path [" + exactPath + "]: " + e.getMessage(), e);
        }
    }

    public PokeApiPaginationResponse fetchPaginatedFromExternalApi(int page, int size) {
        int limit = size;
        int offset = page * size;

        String paginatedPath = String.format("/api/v2/pokemon/?limit=%d&offset=%d", limit, offset);
        System.out.println("--- Outgoing paginated request to: https://pokeapi.co" + paginatedPath + " ---");

        try {
            return restClient.get()
                    .uri(paginatedPath)
                    .retrieve()
                    .body(PokeApiPaginationResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching paginated data from PokeAPI: " + e.getMessage(), e);
        }
    }
}
