import React, { useState, useEffect } from 'react';
import { pokemonService } from './services/pokemonService';
import { type PokemonBase, type DetailedPokemon } from './types/pokemon';
import { Search, Edit2, MapPin, Tag, RefreshCw } from 'lucide-react';
import { PokemonCard } from './components/PokemonCard';

export default function App() {
  const [pokemons, setPokemons] = useState<PokemonBase[]>([]);
  const [selectedPokemon, setSelectedPokemon] = useState<DetailedPokemon | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    localizedName: '',
    geographicalMetadata: '',
    internalTags: ''
  });


  useEffect(() => {
    loadPage(page);
  }, [page]);

  const loadPage = async (pageNumber: number) => {
    setLoading(true);
    try {
      const data = await pokemonService.getPaginated(pageNumber, 12);
      setPokemons(data);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSelectPokemon = async (identifier: string) => {
    setLoading(true);
    setError(null);
    try {
      const details = await pokemonService.getDetails(identifier);
      setSelectedPokemon(details);
      setFormData({
        localizedName: details.localizedName || '',
        geographicalMetadata: details.geographicalMetadata || '',
        internalTags: details.internalTags?.join(', ') || ''
      });
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedPokemon) return;

    try {
      const updatedData = {
        ...selectedPokemon,
        localizedName: formData.localizedName,
        geographicalMetadata: formData.geographicalMetadata,
        internalTags: formData.internalTags.split(',').map(t => t.trim()).filter(Boolean)
      };

      const result = await pokemonService.updateLocal(selectedPokemon.id, updatedData);
      setSelectedPokemon(result);
      alert('¡Pokémon modificado con éxito en la base de datos local!');
      loadPage(page); // Refrescar la lista principal
    } catch (err: any) {
      alert(err.message);
    }
  };

  return (
    <div className="min-h-screen bg-slate-900 text-slate-100 p-6 font-sans">
      <header className="max-w-7xl mx-auto mb-8 flex flex-col md:flex-row justify-between items-center gap-4 border-b border-slate-800 pb-6">
        <h1 className="text-3xl font-bold tracking-wider text-amber-400">Pokédex Full-Stack Sync</h1>

        {/* Barra de búsqueda en tiempo real */}
        <div className="relative w-full md:w-80">
          <input
            type="text"
            placeholder="Buscar por nombre o ID..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSelectPokemon(searchQuery)}
            className="w-full bg-slate-800 text-slate-100 pl-10 pr-4 py-2 rounded-lg border border-slate-700 focus:outline-none focus:border-amber-400"
          />
          <Search className="absolute left-3 top-2.5 text-slate-400 w-5 h-5" />
        </div>
      </header>

      {error && <div className="max-w-7xl mx-auto mb-4 bg-red-900/50 border border-red-500 p-4 rounded-lg text-red-200">{error}</div>}

      <main className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2">
          {/* Condicional para alternar entre el Loader o el Grid de Tarjetas */}
          {loading ? (
            <div className="flex flex-col items-center justify-center py-32 bg-slate-800/30 rounded-2xl border border-slate-800 border-dashed">
              <RefreshCw className="animate-spin text-amber-400 w-12 h-12 mb-4" />
              <p className="text-slate-400 font-medium tracking-wide animate-pulse">
                Sincronizando con la Pokédex... Por favor espera
              </p>
            </div>
          ) : (
            /* Si no está cargando, pintamos las tarjetas y la paginación de forma segura */
            <>
              {/* Grid de Pokémon Responsivo usando Tarjetas Inteligentes Asíncronas */}
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
                {pokemons.map((poke) => (
                  <PokemonCard
                    key={poke.name}
                    name={poke.name}
                    onClick={() => handleSelectPokemon(poke.name)}
                  />
                ))}
              </div>

              {/* Paginación */}
              <div className="flex justify-between mt-6">
                <button
                  disabled={page === 0}
                  onClick={() => setPage(p => Math.max(0, p - 1))}
                  className="bg-slate-800 px-4 py-2 rounded disabled:opacity-50 hover:bg-slate-700 transition-colors"
                >
                  Anterior
                </button>
                <span className="py-2 text-slate-400 font-mono">Página {page + 1}</span>
                <button
                  onClick={() => setPage(p => p + 1)}
                  className="bg-slate-800 px-4 py-2 rounded hover:bg-slate-700 transition-colors"
                >
                  Siguiente
                </button>
              </div>
            </>
          )}
        </div>


        {/* LADO DERECHO: Vista Detallada y Modificación Local (US02 y US04) */}
        <div className="bg-slate-800 p-6 rounded-2xl border border-slate-700 h-fit">
          {selectedPokemon ? (
            <div>
              <div className="flex flex-col items-center border-b border-slate-700 pb-4 mb-4">
                <img src={selectedPokemon.imageUrl} alt={selectedPokemon.name} className="w-40 h-40 object-contain" />
                <h2 className="text-2xl font-bold capitalize mt-2 text-amber-400">
                  {selectedPokemon.localizedName || selectedPokemon.name}
                </h2>
                {selectedPokemon.localizedName && <span className="text-xs text-slate-400">Original: {selectedPokemon.name}</span>}
              </div>

              <p className="text-sm text-slate-300 italic mb-4">{selectedPokemon.description}</p>

              {/* Formulario CRUD de Modificación Local */}
              <form onSubmit={handleUpdate} className="space-y-4 border-t border-slate-700 pt-4">
                <h4 className="font-semibold text-sm tracking-wider uppercase text-slate-400 flex items-center gap-2">
                  <Edit2 className="w-4 h-4" /> Personalización Local (MongoDB)
                </h4>

                <div>
                  <label className="block text-xs text-slate-400 mb-1">Nombre Localizado (400 si está vacío)</label>
                  <input
                    type="text"
                    value={formData.localizedName}
                    onChange={(e) => setFormData({...formData, localizedName: e.target.value})}
                    className="w-full bg-slate-900 border border-slate-700 rounded p-2 text-sm focus:border-amber-400 focus:outline-none"
                    placeholder="Ej. Mi Pikachu Guardián"
                  />
                </div>

                <div>
                  <label className="block text-xs text-slate-400 mb-1 flex items-center gap-1">
                    <MapPin className="w-3 h-3" /> Metadatos Geográficos
                  </label>
                  <input
                    type="text"
                    value={formData.geographicalMetadata}
                    onChange={(e) => setFormData({...formData, geographicalMetadata: e.target.value})}
                    className="w-full bg-slate-900 border border-slate-700 rounded p-2 text-sm focus:border-amber-400 focus:outline-none"
                    placeholder="Ej. Servidor Local, Rack 3"
                  />
                </div>

                <div>
                  <label className="block text-xs text-slate-400 mb-1 flex items-center gap-1">
                    <Tag className="w-3 h-3" /> Etiquetas Internas (Separadas por comas)
                  </label>
                  <input
                    type="text"
                    value={formData.internalTags}
                    onChange={(e) => setFormData({...formData, internalTags: e.target.value})}
                    className="w-full bg-slate-900 border border-slate-700 rounded p-2 text-sm focus:border-amber-400 focus:outline-none"
                    placeholder="Ej. PRODUCTIVO, REPLICADO"
                  />
                </div>

                <button
                  type="submit"
                  className="w-full bg-amber-500 hover:bg-amber-600 text-slate-900 font-bold py-2 rounded transition-colors text-sm"
                >
                  Guardar Cambios Locales
                </button>
              </form>
            </div>
          ) : (
            <div className="text-center py-12 text-slate-500">
              Selecciona un Pokémon de la lista o búscalo arriba para ver su detalle y editar sus campos propietarios.
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
