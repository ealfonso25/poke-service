import React, { useState, useEffect } from 'react';
import { pokemonService } from '../services/pokemonService';
import { type PokemonBase, type DetailedPokemon } from '../types/pokemon';
import { RefreshCw } from 'lucide-react';

interface PokemonCardProps {
  name: string;
  onClick: () => void;
}

export const PokemonCard: React.FC<PokemonCardProps> = ({ name, onClick }) => {
  const [details, setDetails] = useState<DetailedPokemon | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCardData = async () => {
      try {
        // Query your local backend. If it's not in Mongo, the backend will go to PokeAPI
        // and return the complete object with the real sprite structure
        const data = await pokemonService.getDetails(name);
        setDetails(data);
      } catch (error) {
        console.error("Error loading card for: " + name, error);
      } finally {
        setLoading(false);
      }
    };
    fetchCardData();
  }, [name]);

  if (loading) {
    return (
      <div className="bg-slate-800 p-4 rounded-xl border border-slate-750 flex items-center justify-center h-40">
        <RefreshCw className="animate-spin text-amber-500/50 w-6 h-6" />
      </div>
    );
  }

  // If details is null, we can still display the name and a placeholder sprite
  const displayName = details?.localizedName || name;
  const sprite = details?.imageUrl || 'https://githubusercontent.com';

  return (
    <div
      onClick={onClick}
      className="bg-slate-800 p-4 rounded-xl border border-slate-750 hover:border-amber-400 transition-all cursor-pointer flex flex-col items-center text-center group shadow-md"
    >
      <img
        src={sprite}
        alt={displayName}
        className="w-24 h-24 object-contain transform group-hover:scale-110 transition-transform"
      />
      <span className="text-xs text-slate-500 font-mono mt-1">
        {details?.id ? `#${details.id}` : '---'}
      </span>
      <h3 className="capitalize font-semibold text-base tracking-wide text-slate-200 mt-0.5 group-hover:text-amber-400 transition-colors">
        {displayName}
      </h3>

      {/* Print categories/REAL TYPEs */}
      <div className="flex gap-1 mt-2">
        {details?.categories?.map((cat) => (
          <span key={cat} className="text-[10px] bg-slate-900 text-amber-400/80 px-2 py-0.5 rounded-full font-mono capitalize">
            {cat}
          </span>
        ))}
      </div>
    </div>
  );
};
