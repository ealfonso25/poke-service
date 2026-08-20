const BASE_URL = 'http://localhost:8080/api/pokemon';

export const pokemonService = {
  // US01: Pagination
  getPaginated: async (page: number, size: number): Promise<PokemonBase[]> => {
    const response = await fetch(`${BASE_URL}?page=${page}&size=${size}`);
    if (!response.ok) throw new Error('Error al cargar la lista de Pokémon');
    return response.json();
  },

  // US02: Details (Triggers a replica in Mongo if it doesn't exist)
  getDetails: async (identifier: string): Promise<DetailedPokemon> => {
    const response = await fetch(`${BASE_URL}/${identifier}`);
    if (!response.ok) throw new Error('Pokémon not found');
    return response.json();
  },

  // US04: Local Modification (PATCH)
  updateLocal: async (id: string, data: Partial<DetailedPokemon>): Promise<DetailedPokemon> => {
    const response = await fetch(`${BASE_URL}/${id}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    if (response.status === 400) throw new Error('Malformed data (400)');
    if (response.status === 404) throw new Error('Pokémon not registered locally (404)');
    return response.json();
  }
};
