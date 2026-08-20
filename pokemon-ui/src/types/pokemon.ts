export interface PokemonBase {
  id: string;
  name: string;
  spriteUrl: string;
  imageUrl: string;
  categories: string[];
  mass: number;
  skills: string[];
}

export interface DetailedPokemon extends PokemonBase {
  description: string;
  evolutionaryLineage: string[];
  localizedName?: string;
  geographicalMetadata?: string;
  internalTags?: string[];
}