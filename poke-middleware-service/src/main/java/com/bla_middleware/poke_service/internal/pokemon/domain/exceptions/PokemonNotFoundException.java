package com.bla_middleware.poke_service.internal.pokemon.domain.exceptions;

public class PokemonNotFoundException extends RuntimeException {
    public String getPokemonId() { return getMessage().replaceAll("\\D+", ""); }
    public PokemonNotFoundException(String id) { super("Pokemon local no encontrado con ID: " + id); }
}
