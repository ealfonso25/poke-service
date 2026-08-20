package com.bla_middleware.poke_service.internal.pokemon.domain.exceptions;

public class InvalidPokemonPayloadException extends RuntimeException {
    public InvalidPokemonPayloadException(String message) { super(message); }
}
