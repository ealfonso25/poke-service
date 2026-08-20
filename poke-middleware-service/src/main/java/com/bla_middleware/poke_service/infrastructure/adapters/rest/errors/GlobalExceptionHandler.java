package com.bla_middleware.poke_service.infrastructure.adapters.rest.errors;

import com.bla_middleware.poke_service.internal.pokemon.domain.exceptions.InvalidPokemonPayloadException;
import com.bla_middleware.poke_service.internal.pokemon.domain.exceptions.PokemonNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PokemonNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(PokemonNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Not Found", "message", ex.getMessage()));
    }

    @ExceptionHandler(InvalidPokemonPayloadException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(InvalidPokemonPayloadException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Bad Request", "message", ex.getMessage()));
    }
}
