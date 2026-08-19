package com.bla_middleware.poke_service.infrastructure.adapters.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoPokemonRepository extends MongoRepository<PokemonDocument, String> {
}
