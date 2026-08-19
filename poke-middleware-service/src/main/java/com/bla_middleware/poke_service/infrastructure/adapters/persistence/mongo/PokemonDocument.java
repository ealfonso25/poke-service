package com.bla_middleware.poke_service.infrastructure.adapters.persistence.mongo;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "pokemons")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PokemonDocument {
    @Id
    private String id;
    private String name;
    private String spriteUrl;
    private List<String> categories;
    private double mass;
    private List<String> skills;
}
