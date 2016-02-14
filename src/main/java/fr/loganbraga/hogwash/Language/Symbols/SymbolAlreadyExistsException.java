package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.Symbol;

public class SymbolAlreadyExistsException extends Exception {
	public SymbolAlreadyExistsException(Symbol symbol) {
		super("Symbol '" + symbol.getName() + "' already exists in dictionary.");
	}
}
