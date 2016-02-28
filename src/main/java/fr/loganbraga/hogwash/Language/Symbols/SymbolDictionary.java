package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collection;

public class SymbolDictionary {
	protected Map<String, Symbol> symbols;

	public SymbolDictionary() {
		this.symbols = new LinkedHashMap<String, Symbol>();
	}

	public void addEntry(Symbol symbol) throws SymbolAlreadyExistsException {
		Symbol s = this.getEntry(symbol.getName());
		if (s != null)
			throw new SymbolAlreadyExistsException(symbol);

		this.symbols.put(symbol.getName(), symbol);
	}

	public Symbol removeEntry(Symbol symbol) {
		return this.symbols.remove(symbol.getName());
	}

	public Symbol getEntry(String name) {
		return this.symbols.get(name);
	}

	public Collection<Symbol> getAllSymbols() {
		return this.symbols.values();
	}

	public int size() {
		return this.symbols.size();
	}

	public String toString() {
		return this.symbols.values().toString();
	}
}
